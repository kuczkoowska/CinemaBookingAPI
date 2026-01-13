package com.projekt.cinemabooking.service;

import com.projekt.cinemabooking.dto.user.UpdateUserDto;
import com.projekt.cinemabooking.dto.user.UserDto;
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
        String email = "test@test.pl";
        User user = new User();
        user.setEmail(email);

        UpdateUserDto dto = new UpdateUserDto();
        dto.setFirstName("Jan");
        dto.setPassword("newPass");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("newPass")).thenReturn("encodedPass");
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.mapToDto(user)).thenReturn(new UserDto());

        userService.updateUser(email, dto);

        assertThat(user.getFirstName()).isEqualTo("Jan");
        assertThat(user.getPassword()).isEqualTo("encodedPass");
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Powinien zablokować i odblokować użytkownika")
    void shouldToggleBlockUser() {
        User user = new User();
        user.setId(1L);
        user.setActive(true);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.toggleBlockUser(1L);

        assertThat(user.isActive()).isFalse();

        userService.toggleBlockUser(1L);

        assertThat(user.isActive()).isTrue();
    }

    @Test
    @DisplayName("Powinien promować użytkownika do admina")
    void shouldPromoteToAdmin() {
        User user = new User();
        user.setId(1L);
        user.setRoles(new HashSet<>());

        Role adminRole = new Role();
        adminRole.setName("ADMIN");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(roleRepository.findByName("ADMIN")).thenReturn(Optional.of(adminRole));

        userService.promoteToAdmin(1L);

        assertThat(user.getRoles()).contains(adminRole);
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Powinien rzucić wyjątek, gdy rola ADMIN nie istnieje")
    void shouldThrowExceptionWhenAdminRoleMissing() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
        when(roleRepository.findByName("ADMIN")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.promoteToAdmin(1L));
    }

    @Test
    @DisplayName("Powinien pobrać użytkownika po emailu")
    void shouldGetUserByEmail() {
        String email = "test@test.pl";
        User user = new User();
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(userMapper.mapToDto(user)).thenReturn(new UserDto());

        userService.getUserByEmail(email);

        verify(userRepository).findByEmail(email);
    }
}