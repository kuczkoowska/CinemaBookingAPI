package com.projekt.cinemabooking.controller;

import com.projekt.cinemabooking.config.SecurityConfig;
import com.projekt.cinemabooking.controller.api.UserController;
import com.projekt.cinemabooking.dto.output.UserAdminDto;
import com.projekt.cinemabooking.entity.Role;
import com.projekt.cinemabooking.entity.User;
import com.projekt.cinemabooking.security.CustomUserDetails;
import com.projekt.cinemabooking.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /api/users - Admin powinien pobrać wszystkich userów")
    void shouldGetAllUsersAsAdmin() throws Exception {
        UserAdminDto adminDto = UserAdminDto.builder()
                .email("admin@test.pl")
                .firstName("Admin")
                .lastName("Systemowy")
                .isActive(true)
                .build();

        when(userService.getAllUsers()).thenReturn(List.of(adminDto));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("admin@test.pl"));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("GET /api/users - User powinien dostać 403 Forbidden")
    void shouldForbidGetAllUsersForNormalUser() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("PATCH /api/users/{id}/block - Admin blokuje usera")
    void shouldBlockUser() throws Exception {
        User adminUser = new User();
        adminUser.setId(99L); // ID admina
        adminUser.setEmail("admin@test.pl");
        adminUser.setPassword("pass");
        adminUser.setActive(true);
        adminUser.setRoles(Set.of(new Role(1L, "ROLE_ADMIN")));

        CustomUserDetails adminDetails = new CustomUserDetails(adminUser);

        doNothing().when(userService).toggleBlockUser(eq(1L), eq(99L));

        mockMvc.perform(patch("/api/users/1/block")
                        .with(csrf())
                        .with(user(adminDetails)))
                .andExpect(status().isOk());
    }
}