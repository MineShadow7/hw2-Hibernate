package org.hwmoodle.dao;

import org.hwmoodle.model.User;
import org.hwmoodle.testutil.IntegrationTestBase;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UserDAOIT extends IntegrationTestBase {

    @Test
    void saveAndGetUserById() {
        UserDAO userDAO = new UserDAO();
        User user = new User("Alice", "alice@example.com", 25);

        userDAO.saveUser(user);

        assertNotNull(user.getId());
        User loaded = userDAO.getUserById(user.getId());
        assertNotNull(loaded);
        assertEquals("Alice", loaded.getName());
        assertEquals("alice@example.com", loaded.getEmail());
        assertEquals(25, loaded.getAge());
    }

    @Test
    void updateUserUpdatesFields() {
        UserDAO userDAO = new UserDAO();
        User user = new User("Bob", "bob@example.com", 30);
        userDAO.saveUser(user);

        user.setName("Bobby");
        userDAO.updateUser(user);

        User updated = userDAO.getUserById(user.getId());
        assertNotNull(updated);
        assertEquals("Bobby", updated.getName());
    }

    @Test
    void deleteUserRemovesRecord() {
        UserDAO userDAO = new UserDAO();
        User user = new User("Carol", "carol@example.com", 28);
        userDAO.saveUser(user);

        userDAO.deleteUser(user.getId());

        User deleted = userDAO.getUserById(user.getId());
        assertNull(deleted);
    }

    @Test
    void listAllUsersReturnsAll() {
        UserDAO userDAO = new UserDAO();
        userDAO.saveUser(new User("Dan", "dan@example.com", 21));
        userDAO.saveUser(new User("Eve", "eve@example.com", 22));

        List<User> users = userDAO.listAllUsers();

        assertNotNull(users);
        assertEquals(2, users.size());
    }

    @Test
    void getUserByIdReturnsNullForMissingUser() {
        UserDAO userDAO = new UserDAO();

        User missing = userDAO.getUserById(9999L);

        assertNull(missing);
    }
}

