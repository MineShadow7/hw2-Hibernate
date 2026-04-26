package org.hwmoodle;


import org.hwmoodle.config.HibernateUtil;
import org.hwmoodle.ui.ConsoleUI;

public class Main {
    public static void main(String[] args) {
        try {
            if (!HibernateUtil.isDatabaseAvailable()) {
                System.err.println("КРИТИЧЕСКАЯ ОШИБКА: База данных недоступна!");
                System.err.println("Пожалуйста, проверьте:");
                System.err.println("1. Запущен ли Docker контейнер с базой данных?");
                System.err.println("2. Являются ли параметры подключения в hibernate.cfg.xml корректными?");
                if (HibernateUtil.getInitializationError() != null) {
                    System.err.println("3. Детали ошибки: " + HibernateUtil.getInitializationError());
                }
                System.exit(1);
            }

            ConsoleUI ui = new ConsoleUI();
            ui.start();
        } catch (Exception e) {
            System.err.println("Неожиданная ошибка при запуске приложения: " + e.getMessage());
            System.err.println("Детали: " + e);
            System.exit(1);
        } finally {
            HibernateUtil.shutdown();
        }
    }
}
