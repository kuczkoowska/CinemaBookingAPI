package com.projekt.cinemabooking.dto.user;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class UserAdminDto {
    private String email;
    private String firstName;
    private String lastName;
    private boolean isActive;
    private Set<String> roles;
}