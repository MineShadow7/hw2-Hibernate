package org.hwmoodle.dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hwmoodle.config.HibernateUtil;
import org.hwmoodle.model.User;

import java.util.List;

public class UserDAO {

    // CREATE
    public void saveUser(User user){
        Transaction transaction = null;
        try(Session session = HibernateUtil.getSessionFactory().openSession()){
            transaction = session.beginTransaction();
            session.persist(user); // save() вроде как уже deprecated начиная с 6.x
            transaction.commit();
        } catch (Exception e){
            if(transaction != null) transaction.rollback();
            System.err.println("Ошибка сохренения пользователя: " + e.getMessage());
        }
    }

    // READ
    public User getUserById(Long id){
        try(Session session = HibernateUtil.getSessionFactory().openSession()){
            return session.get(User.class, id);
        } catch (Exception e){
            System.err.println("Ошибка получения пользователя: " + e.getMessage());
            return null;
        }
    }

    // UPDATE
    public void updateUser(User user){
        Transaction transaction = null;
        try(Session session = HibernateUtil.getSessionFactory().openSession()){
            transaction = session.beginTransaction();
            session.merge(user); // update() вроде как уже deprecated начиная с 6.x
            transaction.commit();
        } catch (Exception e){
            if(transaction != null) transaction.rollback();
            System.err.println("Ошибка обновления пользователя: " + e.getMessage());
        }
    }

    // DELETE
    public void deleteUser(Long id){
        Transaction transaction = null;
        try(Session session = HibernateUtil.getSessionFactory().openSession()){
            transaction = session.beginTransaction();
            User user = session.get(User.class, id);
            if(user != null){
                session.remove(user);
                transaction.commit();
            } else {
                System.err.println("Пользователь с id " + id + " не найден.");
            }
        } catch (Exception e){
            if(transaction != null) transaction.rollback();
            System.err.println("Ошибка удаления пользователя: " + e.getMessage());
        }
    }

    // READ all users
    public List<User> listAllUsers(){
        try (Session session = HibernateUtil.getSessionFactory().openSession()){
            return session.createQuery("from User", User.class).list();
        } catch (Exception e){
            System.err.println("Ошибка получения списка пользователей: " + e.getMessage());
            return null;
        }
    }
}
