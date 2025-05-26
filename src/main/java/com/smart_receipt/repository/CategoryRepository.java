package com.smart_receipt.repository;


import com.smart_receipt.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Boolean existsByNameAndUser_Id(String name, Long userId);

    List<Category> findByUser_Id(Long userId);

    List<Category> findByUser_IdAndNameIn(Long id, Set<String> categoryNames);

    Optional<Category> findByIdAndUser_Id(Long categoryId, Long userId);
}
