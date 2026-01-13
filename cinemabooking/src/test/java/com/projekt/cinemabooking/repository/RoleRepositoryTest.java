package com.projekt.cinemabooking.repository;

import com.projekt.cinemabooking.entity.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
@TestPropertySource(properties = "spring.sql.init.mode=never")
class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;

    @Test
    void shouldFindRoleByName() {
        Role role = new Role();
        role.setName("ROLE_ADMIN");
        roleRepository.save(role);

        var result = roleRepository.findByName("ROLE_ADMIN");

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("ROLE_ADMIN");
    }

    @Test
    void shouldReturnEmptyIfRoleNotFound() {
        var result = roleRepository.findByName("ROLE_UNKNOWN");

        assertThat(result).isEmpty();
    }
}

