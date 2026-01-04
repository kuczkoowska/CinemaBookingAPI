package com.projekt.cinemabooking.dto.user;

import lombok.Data;

@Data
public class UpdateUserDto {
    private String firstName;
    private String lastName;
    private String password;
}
