package com.projekt.cinemabooking.dto.user;

import com.projekt.cinemabooking.entity.Role;
import lombok.Data;

import java.util.Set;

@Data
public class UserAdminDto {
    private String email;
    private String firstName;
    private String lastName;
    private boolean isActive;
    private Set<Role> roles;
}
