package org.hwmoodle.config;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import org.hwmoodle.model.User;

public class HibernateUtil {
    private static SessionFactory sessionFactory;

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                Configuration configuration = new Configuration();
                configuration.configure();
                configuration.addAnnotatedClass(User.class);

                sessionFactory = configuration.buildSessionFactory();
            } catch (Throwable ex) {
                // Make sure you log the exception, as it might be swallowed
                System.err.println("Ошибка инициализации SessionFactory: " + ex.getMessage());
                throw new ExceptionInInitializerError(ex);
            }
        }
        return sessionFactory;
    }

    public static void shutdown(){
        if(sessionFactory != null){
            sessionFactory.close();
        }
    }

}
