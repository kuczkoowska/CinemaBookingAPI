package com.projekt.cinemabooking.service;

import com.projekt.cinemabooking.dto.user.UpdateUserDto;
import com.projekt.cinemabooking.dto.user.UserAdminDto;
import com.projekt.cinemabooking.dto.user.UserDto;
import com.projekt.cinemabooking.entity.Role;
import com.projekt.cinemabooking.entity.User;
import com.projekt.cinemabooking.exception.ResourceNotFoundException;
import com.projekt.cinemabooking.mapper.UserMapper;
import com.projekt.cinemabooking.repository.RoleRepository;
import com.projekt.cinemabooking.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

    public UserDto getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Użytkownik nie istnieje"));
        return userMapper.mapToDto(user);
    }

    public UserAdminDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Użytkownik", id));
        return userMapper.mapToAdminDto(user);
    }

    @Transactional
    public UserDto updateUser(String email, UpdateUserDto dto) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Użytkownik nie istnieje"));

        if (dto.getFirstName() != null) user.setFirstName(dto.getFirstName());
        if (dto.getLastName() != null) user.setLastName(dto.getLastName());
        if (dto.getPassword() != null) user.setPassword(passwordEncoder.encode(dto.getPassword()));

        return userMapper.mapToDto(userRepository.save(user));
    }

    @Transactional
    public UserAdminDto updateUserAsAdmin(Long id, UpdateUserDto dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Użytkownik", id));

        if (dto.getFirstName() != null) user.setFirstName(dto.getFirstName());
        if (dto.getLastName() != null) user.setLastName(dto.getLastName());
        if (dto.getPassword() != null) user.setPassword(passwordEncoder.encode(dto.getPassword()));

        User savedUser = userRepository.save(user);

        return userMapper.mapToAdminDto(savedUser);
    }


    @Transactional
    public void toggleBlockUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Użytkownik", id));

        user.setActive(!user.isActive());
        userRepository.save(user);
    }

    @Transactional
    public void promoteToAdmin(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Użytkownik", id));

        Role adminRole = roleRepository.findByName("ADMIN").orElseThrow(() -> new RuntimeException("Rola ADMIN nie została znaleziona w bazie"));

        user.getRoles().add(adminRole);
        userRepository.save(user);
    }

}
