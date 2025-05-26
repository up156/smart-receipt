package com.smart_receipt.service.impl;

import com.smart_receipt.config.security.JwtUser;
import com.smart_receipt.dto.CategoryDto;
import com.smart_receipt.dto.CategoryStatsDto;
import com.smart_receipt.dto.ProductDto;
import com.smart_receipt.dto.ReceiptDto;
import com.smart_receipt.dto.RecognitionProductDto;
import com.smart_receipt.dto.RecognitionResultDto;
import com.smart_receipt.ex.CategoryNotFoundException;
import com.smart_receipt.ex.ProductNotFoundException;
import com.smart_receipt.ex.ReceiptNotFoundException;
import com.smart_receipt.ex.UserNotFoundException;
import com.smart_receipt.kafka.ReceiptEventPublisher;
import com.smart_receipt.mapper.CategoryMapper;
import com.smart_receipt.mapper.ProductMapper;
import com.smart_receipt.mapper.ReceiptMapper;
import com.smart_receipt.model.Category;
import com.smart_receipt.model.CategoryStats;
import com.smart_receipt.model.Product;
import com.smart_receipt.model.Receipt;
import com.smart_receipt.model.User;
import com.smart_receipt.repository.CategoryRepository;
import com.smart_receipt.repository.CategoryStatsRepository;
import com.smart_receipt.repository.ProductRepository;
import com.smart_receipt.repository.ReceiptRepository;
import com.smart_receipt.repository.UserRepository;
import com.smart_receipt.request.ProductUpdateRequest;
import com.smart_receipt.service.ReceiptService;
import com.smart_receipt.service.RecognitionService;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReceiptServiceImpl implements ReceiptService {

    private final RecognitionService recognitionService;

    private final ReceiptEventPublisher receiptEventPublisher;

    private final CategoryRepository categoryRepository;

    private final CategoryStatsRepository categoryStatsRepository;

    private final UserRepository userRepository;

    private final ReceiptRepository receiptRepository;

    private final ProductRepository productRepository;

    private final ReceiptMapper receiptMapper;

    private final ProductMapper productMapper;

    private final CategoryMapper categoryMapper;

    private static final String USER_NOT_FOUND_MESSAGE = "User not found";
    private static final String CATEGORY_NOT_FOUND_MESSAGE = "Category not found";
    private static final String PRODUCT_NOT_FOUND_MESSAGE = "Product not found";
    private static final String RECEIPT_NOT_FOUND_MESSAGE = "Receipt not found";


    @Override
    @Transactional
    @CacheEvict(value = "receipts", allEntries = true)
    public ReceiptDto uploadReceiptImage(JwtUser jwtUser, MultipartFile file) {

        log.info("Receipt service started processing image recognition");
        User user = userRepository.findById(jwtUser.getId()).orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND_MESSAGE));
        List<CategoryDto> categories = categoryRepository.findByUser_Id(jwtUser.getId())
                .stream()
                .map(categoryMapper::mapToCategoryDto)
                .toList();
        if (CollectionUtils.isEmpty(categories)) {
            throw new CategoryNotFoundException(CATEGORY_NOT_FOUND_MESSAGE);
        }
        RecognitionResultDto recognitionResult = recognitionService.processImage(categories, file);
        ReceiptDto result = processRecognitionResult(user, recognitionResult);
        receiptEventPublisher.publishReceiptProcessed(jwtUser.getId());
        return result;
    }

    @Override
    @Transactional
    @CacheEvict(value = "receipts", allEntries = true)
    public ReceiptDto uploadReceiptText(JwtUser jwtUser, String text) {

        log.info("Receipt service started processing text recognition");
        User user = userRepository.findById(jwtUser.getId()).orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND_MESSAGE));
        List<CategoryDto> categories = categoryRepository.findByUser_Id(jwtUser.getId())
                .stream()
                .map(categoryMapper::mapToCategoryDto)
                .toList();
        if (CollectionUtils.isEmpty(categories)) {
            throw new CategoryNotFoundException(CATEGORY_NOT_FOUND_MESSAGE);
        }
        RecognitionResultDto recognitionResult = recognitionService.processText(categories, text);
        ReceiptDto result = processRecognitionResult(user, recognitionResult);
        receiptEventPublisher.publishReceiptProcessed(jwtUser.getId());
        return result;
    }


    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "receipts", key = "#root.methodName + ':' + #jwtUser.id + ':' + #categories + ':' + #minPrice + ':' + #maxPrice + ':' + #from + ':' + #to")
    public List<ReceiptDto> getAllReceipts(JwtUser jwtUser, List<String> categories, BigDecimal minPrice,
                                           BigDecimal maxPrice, LocalDate from, LocalDate to) {

        log.info("Receipt service started get all receipts for user: {} with parameters: {}, {}, {}, {}, {}",
                jwtUser, categories, minPrice, maxPrice, from, to);

        Specification<Receipt> spec = createSpecification(jwtUser.getId(), categories, minPrice, maxPrice, from, to);

        return receiptRepository.findAll(spec).stream()
                .map(receiptMapper::toReceiptDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ReceiptDto getReceipt(JwtUser jwtUser, Long receiptId) {

        log.info("Receipt service started get receipt for user: {} with receipt id: {}", jwtUser, receiptId);
        Receipt receipt = receiptRepository.findByIdAndUser_Id(receiptId, jwtUser.getId())
                .orElseThrow(() -> new ReceiptNotFoundException(RECEIPT_NOT_FOUND_MESSAGE));
        return receiptMapper.toReceiptDto(receipt);
    }

    @Override
    @Transactional
    @CacheEvict(value = "receipts", allEntries = true)
    public ProductDto updateProduct(JwtUser jwtUser, Long productId, ProductUpdateRequest request) {

        log.info("Receipt service started update product for user: {} with product id: {} and request: {}", jwtUser, productId, request);

        Product toUpdate = productRepository.findByIdAndReceipt_User_Id(productId, jwtUser.getId())
                .orElseThrow(() -> new ProductNotFoundException(PRODUCT_NOT_FOUND_MESSAGE));

        if (request.categoryId() != null) {
            Category category = categoryRepository.findByIdAndUser_Id(request.categoryId(), jwtUser.getId())
                    .orElseThrow(() -> new CategoryNotFoundException(CATEGORY_NOT_FOUND_MESSAGE));
            toUpdate.setCategory(category);
            receiptEventPublisher.publishReceiptProcessed(jwtUser.getId());
        }

        if (request.name() != null) {
            toUpdate.setName(request.name());
        }
        if (request.price() != null) {
            toUpdate.setPrice(request.price());
            receiptEventPublisher.publishReceiptProcessed(jwtUser.getId());
        }

        return productMapper.mapToProductDto(productRepository.save(toUpdate));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryStatsDto> getStats(JwtUser jwtUser, Integer month, Integer year) {

        log.info("Receipt service started get stats for user: {} for month: {} year: {}", jwtUser, month, year);
        List<CategoryStats> categoryStats = categoryStatsRepository.findAllByCategory_User_IdAndMonthAndYear(jwtUser.getId(), month, year);
        return categoryStats.stream()
                .map(categoryMapper::mapToCategoryStatsDto)
                .toList();
    }


    private ReceiptDto processRecognitionResult(User user, RecognitionResultDto recognitionResult) {

        log.info("Receipt service started processing work with recognition result: {} and user id: {}", recognitionResult, user.getId());

        Set<String> categoryNames = recognitionResult.getProducts().stream()
                .map(RecognitionProductDto::getCategoryName)
                .collect(Collectors.toSet());

        List<Category> categories = categoryRepository.findByUser_IdAndNameIn(user.getId(), categoryNames);
        Map<String, Category> categoryMap = categories.stream()
                .collect(Collectors.toMap(Category::getName, Function.identity()));

        List<Product> productList = recognitionResult
                .getProducts()
                .stream()
                .map(dto -> Product.builder()
                        .name(dto.getName())
                        .price(dto.getPrice())
                        .category(Optional.ofNullable(categoryMap.get(dto.getCategoryName()))
                                .orElseThrow(() -> new CategoryNotFoundException(CATEGORY_NOT_FOUND_MESSAGE)))
                        .build())
                .toList();

        BigDecimal totalPrice = productList.stream()
                .map(Product::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Receipt receipt = Receipt.builder()
                .receiptDate(LocalDate.now())
                .products(productList)
                .totalPrice(totalPrice)
                .user(user)
                .build();

        user.addReceipt(receipt);
        productList.forEach(p -> p.setReceipt(receipt));
        return receiptMapper.toReceiptDto(receiptRepository.save(receipt));
    }

    private Specification<Receipt> createSpecification(Long userId, List<String> categories, BigDecimal minPrice,
                                                       BigDecimal maxPrice, LocalDate from, LocalDate to) {

        return (root, query, cb) -> {
            root.fetch("products", JoinType.LEFT);
            String receiptDate = "receiptDate";
            String totalPrice = "totalPrice";

            if (query != null) {
                query.distinct(true);
            }

            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.equal(root.get("user").get("id"), userId));

            if (from != null && to != null) {
                predicates.add(cb.between(root.get(receiptDate), from, to));
            } else if (from != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get(receiptDate), from));
            } else if (to != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get(receiptDate), to));
            }

            if (minPrice != null && maxPrice != null) {
                predicates.add(cb.between(root.get(totalPrice), minPrice, maxPrice));
            } else if (minPrice != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get(totalPrice), minPrice));
            } else if (maxPrice != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get(totalPrice), maxPrice));
            }

            if (categories != null && !categories.isEmpty()) {
                Join<Receipt, Product> productJoin = root.join("products", JoinType.INNER);
                predicates.add(productJoin.get("category").get("name").in(categories));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}