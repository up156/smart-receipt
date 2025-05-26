package com.smart_receipt.mapper;

import com.smart_receipt.dto.UserDto;
import com.smart_receipt.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto mapToUserDto(User user);
}
