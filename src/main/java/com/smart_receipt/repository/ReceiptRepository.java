package com.smart_receipt.repository;


import com.smart_receipt.model.Receipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface ReceiptRepository extends JpaRepository<Receipt, Long>, JpaSpecificationExecutor<Receipt> {

    Optional<Receipt> findByIdAndUser_Id(Long receiptId, Long id);
}
