package com.projekt.cinemabooking.dto.output;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAdminDto {
    private String email;
    private String firstName;
    private String lastName;
    private boolean isActive;
    private Set<String> roles;
}