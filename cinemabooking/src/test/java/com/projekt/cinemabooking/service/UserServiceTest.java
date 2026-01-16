package com.projekt.cinemabooking.service;

import com.projekt.cinemabooking.dto.input.UpdateUserDto;
import com.projekt.cinemabooking.dto.output.UserDto;
import com.projekt.cinemabooking.entity.Role;
import com.projekt.cinemabooking.entity.User;
import com.projekt.cinemabooking.mapper.UserMapper;
import com.projekt.cinemabooking.repository.RoleRepository;
import com.projekt.cinemabooking.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("Powinien zaktualizować dane użytkownika i zahaszować hasło")
    void shouldUpdateUserAndPassword() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setPassword("oldPass");

        UpdateUserDto dto = new UpdateUserDto();
        dto.setFirstName("Jan");
        dto.setPassword("newPass");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user)); // ZMIANA: findById
        when(passwordEncoder.encode("newPass")).thenReturn("encodedPass");
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.mapToDto(user)).thenReturn(new UserDto());

        userService.updateUser(userId, dto); // ZMIANA: Przekazujemy ID

        assertThat(user.getFirstName()).isEqualTo("Jan");
        assertThat(user.getPassword()).isEqualTo("encodedPass");
        verify(userRepository).save(user);
    }

    @Test
    void shouldToggleBlockUser() {
        User user = new User();
        user.setId(2L); // target
        user.setActive(true);

        when(userRepository.findById(2L)).thenReturn(Optional.of(user));

        userService.toggleBlockUser(2L, 1L);

        assertThat(user.isActive()).isFalse();
    }

    @Test
    @DisplayName("Powinien promować użytkownika do admina")
    void shouldPromoteToAdmin() {
        User user = new User();
        user.setId(1L);
        user.setRoles(new HashSet<>());

        Role adminRole = new Role();
        adminRole.setName("ROLE_ADMIN");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(roleRepository.findByName("ROLE_ADMIN")).thenReturn(Optional.of(adminRole));

        userService.promoteToAdmin(1L);

        assertThat(user.getRoles()).contains(adminRole);
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Powinien rzucić wyjątek, gdy rola ADMIN nie istnieje")
    void shouldThrowExceptionWhenAdminRoleMissing() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
        when(roleRepository.findByName("ROLE_ADMIN")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.promoteToAdmin(1L));
    }


}