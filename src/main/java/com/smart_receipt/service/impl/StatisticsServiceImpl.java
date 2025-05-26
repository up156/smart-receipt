package com.smart_receipt.service.impl;

import com.smart_receipt.model.Category;
import com.smart_receipt.model.CategoryStats;
import com.smart_receipt.repository.CategoryRepository;
import com.smart_receipt.repository.CategoryStatsRepository;
import com.smart_receipt.repository.ProductRepository;
import com.smart_receipt.repository.UserRepository;
import com.smart_receipt.service.StatisticsService;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final CategoryRepository categoryRepository;

    private final CategoryStatsRepository categoryStatsRepository;

    private final ProductRepository productRepository;

    private final UserRepository userRepository;

    private final MeterRegistry meterRegistry;

    private static final String CATEGORY_NOT_FOUND_MESSAGE = "Category not found";

    @Override
    @Transactional
    @CacheEvict(value = "receipts", allEntries = true)
    public void updateStatistics(Long userId) {

        log.info("Statistics service started update statistics for user: {}", userId);

        meterRegistry.counter("statistics.update.count").increment();

        List<Category> categories = categoryRepository.findByUser_Id(userId);
        if (!CollectionUtils.isEmpty(categories)) {


            LocalDate now = LocalDate.now();
            int month = now.getMonthValue();
            int year = now.getYear();
            LocalDate startDate = LocalDate.of(year, month, 1);
            LocalDate endDate = startDate.plusMonths(1);
            List<CategoryStats> existingStats = categoryStatsRepository.findAllByCategory_User_IdAndMonthAndYear(userId, month, year);
            Map<Long, CategoryStats> existingStatsMap = existingStats.stream()
                    .collect(Collectors.toMap(stat -> stat.getCategory().getId(), Function.identity()));

            List<CategoryStats> resultStats = new ArrayList<>();

            for (Category category : categories) {

                CategoryStats stat = existingStatsMap.get(category.getId());
                BigDecimal monthlyTotal = productRepository
                        .findTotalPriceByCategoryIdAndUserIdAndDateRange(category.getId(), userId, startDate, endDate);

                if (stat == null) {
                    stat = CategoryStats
                            .builder()
                            .category(category)
                            .month(month)
                            .year(year)
                            .monthlyLimit(category.getMonthlyLimit())
                            .build();
                }

                stat.setMonthTotal(monthlyTotal);
                resultStats.add(stat);
            }

            Set<Long> currentCategoryIds = categories.stream()
                    .map(Category::getId)
                    .collect(Collectors.toSet());

            List<CategoryStats> obsoleteStats = existingStats.stream()
                    .filter(stat -> !currentCategoryIds.contains(stat.getCategory().getId()))
                    .toList();

            categoryStatsRepository.saveAll(resultStats);
            categoryStatsRepository.deleteAll(obsoleteStats);
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = "receipts", allEntries = true)
    public void recalculateAll() {

        log.info("Statistics service started full statistics recalculation");
        List<Long> allUserIds = userRepository.findAllUserIds();
        allUserIds.parallelStream().forEach(this::updateStatistics);
        log.info("Completed full statistics recalculation");
    }

}