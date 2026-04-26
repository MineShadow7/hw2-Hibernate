package org.hwmoodle.service;

import org.hwmoodle.dao.UserDAO;
import org.hwmoodle.model.User;

import java.util.List;

public class UserService {
    private final UserDAO userDAO = new UserDAO();

    public void createNewUser(String name, String email, int age){
        if (email == null || !email.contains("@")) {
            System.err.println("Некорректный email: " + email);
            return;
        }
        User user = new User(name, email, age);
        userDAO.saveUser(user);
    }

    public void printUserInfo(Long id){
        User user = userDAO.getUserById(id);
        if (user != null){
            System.out.println("Найдено: " + user);
        } else {
            System.out.println("Пользователь с id " + id + " не найден.");
        }
    }

    public void updateUserName(Long id, String newName){
        User user = userDAO.getUserById(id);
        if (user != null){
            user.setName(newName);
            userDAO.updateUser(user);
            System.out.println("Имя успешно обновлено");
        } else {
            System.out.println("Пользователь с id " + id + " не найден.");
        }
    }

    public void removeUser(Long id){
        userDAO.deleteUser(id);
    }

    public List<User> findAllUsers() {
        return userDAO.listAllUsers();
    }
}
