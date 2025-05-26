package com.smart_receipt.repository;


import com.smart_receipt.model.User;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(@NotBlank String email);

    Boolean existsByEmail(String email);

    @Query("SELECT u.id FROM User u")
    List<Long> findAllUserIds();
}
