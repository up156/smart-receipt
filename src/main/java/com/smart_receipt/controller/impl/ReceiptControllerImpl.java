package com.smart_receipt.controller.impl;


import com.smart_receipt.config.security.JwtUser;
import com.smart_receipt.controller.ReceiptController;
import com.smart_receipt.dto.CategoryStatsDto;
import com.smart_receipt.dto.ProductDto;
import com.smart_receipt.dto.ReceiptDto;
import com.smart_receipt.request.ProductUpdateRequest;
import com.smart_receipt.service.ReceiptService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;


@RestController
@RequiredArgsConstructor
public class ReceiptControllerImpl implements ReceiptController {

    private final ReceiptService receiptService;

    @Override
    public ResponseEntity<ReceiptDto> uploadReceiptImage(JwtUser jwtUser, MultipartFile file) {
        return ResponseEntity.ok(receiptService.uploadReceiptImage(jwtUser, file));
    }

    @Override
    public ResponseEntity<ReceiptDto> uploadReceiptText(JwtUser jwtUser, String text) {
        return ResponseEntity.ok(receiptService.uploadReceiptText(jwtUser, text));
    }

    @Override
    public ResponseEntity<List<ReceiptDto>> getAllReceipts(JwtUser jwtUser, List<String> categories, BigDecimal minPrice, BigDecimal maxPrice, LocalDate from, LocalDate to) {
        return ResponseEntity.ok(receiptService.getAllReceipts(jwtUser, categories, minPrice, maxPrice, from, to));
    }

    @Override
    public ResponseEntity<ReceiptDto> getReceipt(JwtUser jwtUser, Long receiptId) {
        return ResponseEntity.ok(receiptService.getReceipt(jwtUser, receiptId));
    }

    @Override
    public ResponseEntity<ProductDto> updateProduct(JwtUser jwtUser, Long itemId, ProductUpdateRequest request) {
        return ResponseEntity.ok(receiptService.updateProduct(jwtUser, itemId, request));
    }

    @Override
    public ResponseEntity<List<CategoryStatsDto>> getStats(JwtUser jwtUser, Integer month, Integer year) {
        return ResponseEntity.ok(receiptService.getStats(jwtUser, month, year));
    }
}
