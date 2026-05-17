package org.hwmoodle.service;

import org.hwmoodle.model.User;
import org.hwmoodle.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceTest {
    private UserRepository userRepository;
    private Supplier<Boolean> dbAvailable;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        dbAvailable = () -> true;
        userService = new UserService(userRepository, dbAvailable);
    }

    @Test
    void createNewUserReturnsFalseWhenDbUnavailable() {
        userService = new UserService(userRepository, () -> false);

        boolean result = userService.createNewUser("Alice", "alice@example.com", 25);

        assertFalse(result);
        verifyNoInteractions(userRepository);
    }

    @Test
    void createNewUserReturnsFalseForInvalidEmail() {
        boolean result = userService.createNewUser("Alice", "invalid", 25);

        assertFalse(result);
        verifyNoInteractions(userRepository);
    }

    @Test
    void createNewUserSavesValidUser() {
        boolean result = userService.createNewUser("Alice", "alice@example.com", 25);

        assertTrue(result);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateUserNameDoesNothingWhenDbUnavailable() {
        userService = new UserService(userRepository, () -> false);

        userService.updateUserName(1L, "NewName");

        verifyNoInteractions(userRepository);
    }

    @Test
    void updateUserNameDoesNothingWhenUserMissing() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        userService.updateUserName(1L, "NewName");

        verify(userRepository).findById(1L);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUserNameUpdatesWhenUserFound() {
        User user = new User("Bob", "bob@example.com", 30);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.updateUserName(1L, "Bobby");

        assertEquals("Bobby", user.getName());
        verify(userRepository).save(user);
    }

    @Test
    void removeUserDoesNothingWhenDbUnavailable() {
        userService = new UserService(userRepository, () -> false);

        userService.removeUser(1L);

        verifyNoInteractions(userRepository);
    }

    @Test
    void removeUserDeletesWhenDbAvailable() {
        userService.removeUser(5L);

        verify(userRepository).deleteById(5L);
    }

    @Test
    void findAllUsersReturnsEmptyWhenDbUnavailable() {
        userService = new UserService(userRepository, () -> false);

        List<User> users = userService.findAllUsers();

        assertNotNull(users);
        assertTrue(users.isEmpty());
        verifyNoInteractions(userRepository);
    }

    @Test
    void findAllUsersReturnsDaoResult() {
        List<User> expected = List.of(new User("Dan", "dan@example.com", 21));
        when(userRepository.findAll()).thenReturn(expected);

        List<User> users = userService.findAllUsers();

        assertSame(expected, users);
        verify(userRepository).findAll();
    }

    @Test
    void printUserInfoDoesNothingWhenDbUnavailable() {
        userService = new UserService(userRepository, () -> false);

        userService.printUserInfo(1L);

        verifyNoInteractions(userRepository);
    }

    @Test
    void printUserInfoLoadsUserWhenDbAvailable() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(new User("Eve", "eve@example.com", 22)));

        userService.printUserInfo(2L);

        verify(userRepository).findById(2L);
    }
}
