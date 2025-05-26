package com.smart_receipt.service;

import com.smart_receipt.config.security.JwtUser;
import com.smart_receipt.dto.CategoryStatsDto;
import com.smart_receipt.dto.ProductDto;
import com.smart_receipt.dto.ReceiptDto;
import com.smart_receipt.request.ProductUpdateRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;


@Tag(name = "CategoryService", description = "Сервис для работы с чеками пользователя")
public interface ReceiptService {

    ReceiptDto uploadReceiptImage(JwtUser jwtUser, MultipartFile file);

    ReceiptDto uploadReceiptText(JwtUser jwtUser, String text);

    List<ReceiptDto> getAllReceipts(JwtUser jwtUser, List<String> categories, BigDecimal minPrice, BigDecimal maxPrice,
                                    LocalDate from, LocalDate to);

    ReceiptDto getReceipt(JwtUser jwtUser, Long receiptId);

    ProductDto updateProduct(JwtUser jwtUser, Long itemId, ProductUpdateRequest request);

    List<CategoryStatsDto> getStats(JwtUser jwtUser, Integer month, Integer year);
}