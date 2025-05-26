package com.smart_receipt.repository;


import com.smart_receipt.model.CategoryStats;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryStatsRepository extends JpaRepository<CategoryStats, Long> {

    List<CategoryStats> findAllByCategory_User_IdAndMonthAndYear(Long id, Integer month, Integer year);
}
