package com.projekt.cinemabooking.repository;

import com.projekt.cinemabooking.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldFindUserByEmail() {
        User user = new User();
        user.setEmail("jan@example.com");
        user.setPassword("pass");
        user.setFirstName("Jan");
        user.setLastName("Kowalski");
        userRepository.save(user);

        var result = userRepository.findByEmail("jan@example.com");

        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("jan@example.com");
    }

    @Test
    void shouldReturnEmptyWhenUserNotFoundByEmail() {
        var result = userRepository.findByEmail("nieistnieje@example.com");

        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnTrueIfEmailExists() {
        User user = new User();
        user.setEmail("exists@example.com");
        user.setPassword("pass");
        userRepository.save(user);

        boolean exists = userRepository.existsByEmail("exists@example.com");

        assertTrue(exists);
    }

    @Test
    void shouldReturnFalseIfEmailDoesNotExist() {
        boolean exists = userRepository.existsByEmail("nobody@example.com");

        assertFalse(exists);
    }
}