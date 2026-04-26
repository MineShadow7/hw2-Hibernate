package org.hwmoodle.ui;

import org.hwmoodle.config.HibernateUtil;
import org.hwmoodle.service.UserService;

import java.util.Scanner;

public class ConsoleUI {
    private final UserService userService = new UserService();
    private final Scanner scanner = new Scanner(System.in);

    public void start() {

        if (!HibernateUtil.isDatabaseAvailable()) {
            System.err.println("ОШИБКА: Не удалось подключиться к базе данных!");
            System.err.println("Приложение будет завершено.");
            return;
        }
        System.out.println("Успешное подключение к базе данных!");

        while (true) {
            try {
                System.out.println("\n--- USER SERVICE MENU ---");
                System.out.println("1. Создать пользователя");
                System.out.println("2. Найти по ID");
                System.out.println("3. Обновить имя");
                System.out.println("4. Вывести всех пользователей");
                System.out.println("5. Удалить");
                System.out.println("6. Выход");
                System.out.print("Выберите действие: ");

                int choice = readInt();

                if (!HibernateUtil.isDatabaseAvailable()) {
                    System.err.println("ОШИБКА: Соединение с базой данных потеряно!");
                    System.err.println("Пожалуйста, проверьте статус Docker контейнера.");
                    System.out.print("Хотите повторить попытку? (y/n): ");
                    if (scanner.nextLine().equalsIgnoreCase("y")) {
                        continue;
                    } else {
                        break;
                    }
                }

                switch (choice) {
                    case 1 -> {
                        System.out.print("Имя: "); String name = scanner.nextLine();
                        System.out.print("Email: "); String email = scanner.nextLine();
                        System.out.print("Возраст: "); int age = readInt();
                        if(userService.createNewUser(name, email, age)) {
                            System.out.println("Пользователь успешно создан");
                        }else{
                            System.err.println("Ошибка при создании пользователя. Проверьте введенные данные.");
                        };

                    }
                    case 2 -> {
                        System.out.print("Введите ID: ");
                        userService.printUserInfo(readLong());
                    }
                    case 3 -> {
                        System.out.print("Введите ID: ");
                        Long id = readLong();
                        System.out.print("Новое имя: ");
                        String newName = scanner.nextLine();
                        userService.updateUserName(id, newName);
                    }
                    case 4 -> {
                        var users = userService.findAllUsers();
                        if (users != null && !users.isEmpty()) {
                            users.forEach(System.out::println);
                        } else if (users == null) {
                            System.err.println("Ошибка при получении списка пользователей");
                        } else {
                            System.out.println("Нет пользователей в базе данных");
                        }
                    }
                    case 5 -> {
                        System.out.print("Введите ID для удаления: ");
                        userService.removeUser(readLong());
                    }
                    case 6 -> {
                        System.out.println("Завершение работы...");
                        return;
                    }
                    default -> System.out.println("Неверный ввод. Ввод должен быть в виде целочисленного значения от 1 до 6.");
                }
            } catch (Exception e) {
                System.err.println("Неожиданная ошибка: " + e.getMessage());
            }
        }
    }

    private int readInt(){
        while(true){
            try{
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Ошибка значения - необходимо ввести целое число. Введите значение: ");
            }
        }
    }

    private Long readLong(){
        while(true){
            try{
                return Long.parseLong(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Ошибка значения - ID должен быть числом. Введите значение: ");
            }
        }
    }


}
