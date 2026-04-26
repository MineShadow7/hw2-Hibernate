package org.hwmoodle;


import org.hwmoodle.config.HibernateUtil;
import org.hwmoodle.ui.ConsoleUI;

public class Main {
    static void main(String[] args) {
        try {
            ConsoleUI ui = new ConsoleUI();
            ui.start();
        } finally {
            // Важно закрыть фабрику сессий при выходе
            HibernateUtil.shutdown();
        }
    }
}
