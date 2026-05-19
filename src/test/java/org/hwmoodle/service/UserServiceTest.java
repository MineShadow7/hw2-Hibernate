package org.hwmoodle.service;

import org.hwmoodle.core.dto.UserRequestDto;
import org.hwmoodle.core.dto.UserResponseDto;
import org.hwmoodle.core.model.User;
import org.hwmoodle.core.service.UserService;
import org.hwmoodle.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private UserRequestDto userRequestDto;

    @BeforeEach
    void setUp() {
        testUser = new User(1L, "Alice", "alice@example.com", 25, LocalDateTime.now());
        userRequestDto = new UserRequestDto("Alice", "alice@example.com", 25);
    }

    @Test
    void createUserSuccessfully() {
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        UserResponseDto result = userService.createUser(userRequestDto);

        assertNotNull(result);
        assertEquals("alice@example.com", result.email());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUserThrowsExceptionForInvalidEmail() {
        UserRequestDto invalidDto = new UserRequestDto("Alice", "invalid", 25);

        assertThrows(IllegalArgumentException.class, () -> userService.createUser(invalidDto));
    }

    @Test
    void getUserReturnsUserWhenFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        Optional<UserResponseDto> result = userService.getUser(1L);

        assertTrue(result.isPresent());
        assertEquals("alice@example.com", result.get().email());
    }

    @Test
    void getUserReturnsEmptyWhenNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<UserResponseDto> result = userService.getUser(999L);

        assertTrue(result.isEmpty());
    }

    @Test
    void updateUserSuccessfully() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        Optional<UserResponseDto> result = userService.updateUser(1L, userRequestDto);

        assertTrue(result.isPresent());
        verify(userRepository).save(testUser);
    }

    @Test
    void updateUserReturnsEmptyWhenNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<UserResponseDto> result = userService.updateUser(999L, userRequestDto);

        assertTrue(result.isEmpty());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void deleteUserSuccessfully() {
        when(userRepository.existsById(1L)).thenReturn(true);

        userService.deleteUser(1L);

        verify(userRepository).deleteById(1L);
    }

    @Test
    void deleteUserDoesNothingWhenNotFound() {
        when(userRepository.existsById(999L)).thenReturn(false);

        userService.deleteUser(999L);

        verify(userRepository, never()).deleteById(any());
    }

    @Test
    void listUsersReturnsAllUsers() {
        List<User> users = List.of(testUser);
        when(userRepository.findAll()).thenReturn(users);

        List<UserResponseDto> result = userService.listUsers();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(userRepository).findAll();
    }
}
