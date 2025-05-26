package com.smart_receipt.service;

import com.smart_receipt.dto.CategoryDto;
import com.smart_receipt.dto.RecognitionResultDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "RecognitionService", description = "Сервис для работы с распознаванием текста чека")
public interface RecognitionService {

    RecognitionResultDto processImage(List<CategoryDto> categories, MultipartFile file);

    RecognitionResultDto processText(List<CategoryDto> categories, String text);

}
