package com.smart_receipt.mapper;

import com.smart_receipt.dto.ProductDto;
import com.smart_receipt.dto.RecognitionProductDto;
import com.smart_receipt.model.Product;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductDto mapToProductDto(Product product);

    Product mapToProduct(RecognitionProductDto recognitionProductDto);
}
