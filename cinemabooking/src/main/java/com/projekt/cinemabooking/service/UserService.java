package com.projekt.cinemabooking.service;

import com.projekt.cinemabooking.dto.input.UpdateUserDto;
import com.projekt.cinemabooking.dto.output.UserAdminDto;
import com.projekt.cinemabooking.dto.output.UserDto;
import com.projekt.cinemabooking.entity.Role;
import com.projekt.cinemabooking.entity.User;
import com.projekt.cinemabooking.exception.ResourceNotFoundException;
import com.projekt.cinemabooking.mapper.UserMapper;
import com.projekt.cinemabooking.repository.RoleRepository;
import com.projekt.cinemabooking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;


    public List<UserAdminDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::mapToAdminDto)
                .toList();
    }

    public UserAdminDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Użytkownik", id));
        return userMapper.mapToAdminDto(user);
    }

    @Transactional
    public UserAdminDto updateUserAsAdmin(Long id, UpdateUserDto dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Użytkownik", id));

        applyUpdates(user, dto);
        User savedUser = userRepository.save(user);

        return userMapper.mapToAdminDto(savedUser);
    }

    @Transactional
    public void toggleBlockUser(Long targetId, Long currentAdminId) {
        if (targetId.equals(currentAdminId)) {
            throw new IllegalArgumentException("Nie możesz zablokować własnego konta!");
        }

        User user = userRepository.findById(targetId)
                .orElseThrow(() -> new ResourceNotFoundException("Użytkownik", targetId));

        user.setActive(!user.isActive());
        userRepository.save(user);
    }

    @Transactional
    public void promoteToAdmin(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Użytkownik", id));

        Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                .orElseThrow(() -> new RuntimeException("Rola ROLE_ADMIN nie została znaleziona w bazie"));

        if (!user.getRoles().contains(adminRole)) {
            user.getRoles().add(adminRole);
            userRepository.save(user);
        }
    }


    public UserDto getUserDtoById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Użytkownik", id));
        return userMapper.mapToDto(user);
    }

    @Transactional
    public UserDto updateUser(Long id, UpdateUserDto dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Użytkownik", id));

        applyUpdates(user, dto);
        return userMapper.mapToDto(userRepository.save(user));
    }


    private void applyUpdates(User user, UpdateUserDto dto) {
        if (dto.getFirstName() != null && !dto.getFirstName().isBlank()) {
            user.setFirstName(dto.getFirstName());
        }
        if (dto.getLastName() != null && !dto.getLastName().isBlank()) {
            user.setLastName(dto.getLastName());
        }
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
    }
}