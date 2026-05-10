package org.hwmoodle.service;

import org.hwmoodle.dao.UserDAO;
import org.hwmoodle.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class UserServiceTest {
    private UserDAO userDAO;
    private Supplier<Boolean> dbAvailable;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userDAO = mock(UserDAO.class);
        dbAvailable = () -> true;
        userService = new UserService(userDAO, dbAvailable);
    }

    @Test
    void createNewUserReturnsFalseWhenDbUnavailable() {
        userService = new UserService(userDAO, () -> false);

        boolean result = userService.createNewUser("Alice", "alice@example.com", 25);

        assertFalse(result);
        verifyNoInteractions(userDAO);
    }

    @Test
    void createNewUserReturnsFalseForInvalidEmail() {
        boolean result = userService.createNewUser("Alice", "invalid", 25);

        assertFalse(result);
        verifyNoInteractions(userDAO);
    }

    @Test
    void createNewUserSavesValidUser() {
        boolean result = userService.createNewUser("Alice", "alice@example.com", 25);

        assertTrue(result);
        verify(userDAO).saveUser(any(User.class));
    }

    @Test
    void updateUserNameDoesNothingWhenDbUnavailable() {
        userService = new UserService(userDAO, () -> false);

        userService.updateUserName(1L, "NewName");

        verifyNoInteractions(userDAO);
    }

    @Test
    void updateUserNameDoesNothingWhenUserMissing() {
        when(userDAO.getUserById(1L)).thenReturn(null);

        userService.updateUserName(1L, "NewName");

        verify(userDAO).getUserById(1L);
        verify(userDAO, never()).updateUser(any(User.class));
    }

    @Test
    void updateUserNameUpdatesWhenUserFound() {
        User user = new User("Bob", "bob@example.com", 30);
        when(userDAO.getUserById(1L)).thenReturn(user);

        userService.updateUserName(1L, "Bobby");

        assertEquals("Bobby", user.getName());
        verify(userDAO).updateUser(user);
    }

    @Test
    void removeUserDoesNothingWhenDbUnavailable() {
        userService = new UserService(userDAO, () -> false);

        userService.removeUser(1L);

        verifyNoInteractions(userDAO);
    }

    @Test
    void removeUserDeletesWhenDbAvailable() {
        userService.removeUser(5L);

        verify(userDAO).deleteUser(5L);
    }

    @Test
    void findAllUsersReturnsEmptyWhenDbUnavailable() {
        userService = new UserService(userDAO, () -> false);

        List<User> users = userService.findAllUsers();

        assertNotNull(users);
        assertTrue(users.isEmpty());
        verifyNoInteractions(userDAO);
    }

    @Test
    void findAllUsersReturnsDaoResult() {
        List<User> expected = List.of(new User("Dan", "dan@example.com", 21));
        when(userDAO.listAllUsers()).thenReturn(expected);

        List<User> users = userService.findAllUsers();

        assertSame(expected, users);
        verify(userDAO).listAllUsers();
    }

    @Test
    void printUserInfoDoesNothingWhenDbUnavailable() {
        userService = new UserService(userDAO, () -> false);

        userService.printUserInfo(1L);

        verifyNoInteractions(userDAO);
    }

    @Test
    void printUserInfoLoadsUserWhenDbAvailable() {
        when(userDAO.getUserById(2L)).thenReturn(new User("Eve", "eve@example.com", 22));

        userService.printUserInfo(2L);

        verify(userDAO).getUserById(2L);
    }
}

