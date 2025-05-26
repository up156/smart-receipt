package com.smart_receipt.mapper;

import com.smart_receipt.dto.ReceiptDto;
import com.smart_receipt.model.Receipt;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ReceiptMapper {

    ReceiptDto toReceiptDto(Receipt receipt);
}
