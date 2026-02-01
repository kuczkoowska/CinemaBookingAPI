package com.projekt.cinemabooking.dto.output;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    
    @JsonProperty("isActive")
    private boolean isActive;
    
    private Set<String> roles;
}