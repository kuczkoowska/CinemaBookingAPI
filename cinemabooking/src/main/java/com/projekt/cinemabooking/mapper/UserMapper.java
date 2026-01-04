package com.projekt.cinemabooking.mapper;

import com.projekt.cinemabooking.dto.user.UserAdminDto;
import com.projekt.cinemabooking.dto.user.UserDto;
import com.projekt.cinemabooking.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto mapToDto(User user);

    UserAdminDto mapToAdminDto(User user);
}
