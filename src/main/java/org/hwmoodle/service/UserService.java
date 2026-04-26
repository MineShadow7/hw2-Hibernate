package org.hwmoodle.service;

import org.hwmoodle.config.HibernateUtil;
import org.hwmoodle.dao.UserDAO;
import org.hwmoodle.model.User;

import java.util.ArrayList;
import java.util.List;

public class UserService {
    private final UserDAO userDAO = new UserDAO();

    public boolean createNewUser(String name, String email, int age){
        if(!HibernateUtil.isDatabaseAvailable()){
            System.err.println("БД недоступна. Операция отменена.");
            return false;
        }

        if (email == null || !email.contains("@")) {
            System.err.println("Некорректный email: " + email);
            return false;
        }
        User user = new User(name, email, age);
        userDAO.saveUser(user);
        return true;
    }

    public void printUserInfo(Long id){
        if(!HibernateUtil.isDatabaseAvailable()){
            System.err.println("БД недоступна. Операция отменена.");
            return;
        }

        User user = userDAO.getUserById(id);
        if (user != null){
            System.out.println("Найдено: " + user.toString());
        } else {
            System.out.println("Пользователь с id " + id + " не найден.");
        }
    }

    public void updateUserName(Long id, String newName){
        if(!HibernateUtil.isDatabaseAvailable()){
            System.err.println("БД недоступна. Операция отменена.");
            return;
        }

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
        if(!HibernateUtil.isDatabaseAvailable()){
            System.err.println("БД недоступна. Операция отменена.");
            return;
        }

        userDAO.deleteUser(id);
    }

    public List<User> findAllUsers() {
        if(!HibernateUtil.isDatabaseAvailable()){
            System.err.println("БД недоступна. Операция отменена.");
            return new ArrayList<>();
        }

        return userDAO.listAllUsers();
    }
}
