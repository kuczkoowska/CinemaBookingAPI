package com.projekt.cinemabooking.security;

import com.projekt.cinemabooking.entity.User;
import com.projekt.cinemabooking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Nie znaleziono użytkownika o emailu: " + email));

        // Zablokowany użytkownik może się zalogować, ale ma ograniczony dostęp
        // Sprawdzenie isActive będzie wykonywane przez guard na froncie
        return new CustomUserDetails(user);
    }
}