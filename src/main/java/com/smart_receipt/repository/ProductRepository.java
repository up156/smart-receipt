package com.smart_receipt.repository;


import com.smart_receipt.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByIdAndReceipt_User_Id(Long productId, Long userId);

    @Transactional
    @Query("""
        SELECT COALESCE(SUM(p.price), 0)
        FROM Product p
        WHERE p.category.id = :categoryId
          AND p.receipt.user.id = :userId
          AND p.receipt.receiptDate >= :startDate
          AND p.receipt.receiptDate < :endDate
    """)
    BigDecimal findTotalPriceByCategoryIdAndUserIdAndDateRange(
            @Param("categoryId") Long categoryId,
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Modifying
    @Query("UPDATE Product p SET p.category = NULL WHERE p.category.id = :categoryId")
    void clearCategoryByCategoryId(@Param("categoryId") Long categoryId);
}

