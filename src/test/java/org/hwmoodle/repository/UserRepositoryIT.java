package org.hwmoodle.repository;

import org.hwmoodle.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
class UserRepositoryIT {

    @Container
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("test_db")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void clean() {
        userRepository.deleteAll();
    }

    @Test
    void saveAndFindById() {
        User user = new User("Alice", "alice@example.com", 25);

        User saved = userRepository.save(user);

        Optional<User> loaded = userRepository.findById(saved.getId());
        assertTrue(loaded.isPresent());
        assertEquals("alice@example.com", loaded.get().getEmail());
    }

    @Test
    void deleteByIdRemovesEntity() {
        User user = new User("Bob", "bob@example.com", 30);
        User saved = userRepository.save(user);

        userRepository.deleteById(saved.getId());

        assertTrue(userRepository.findById(saved.getId()).isEmpty());
    }
}
