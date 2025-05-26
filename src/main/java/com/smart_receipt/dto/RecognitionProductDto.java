package com.smart_receipt.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Дто для распознавания продуктов")
public class RecognitionProductDto {

    private String name;
    private BigDecimal price;
    private String categoryName;

}
