package org.hwmoodle.ui;

import org.hwmoodle.service.UserService;

import java.util.Scanner;

public class ConsoleUI {
    private final UserService userService = new UserService();
    private final Scanner scanner = new Scanner(System.in);

    public void start() {
        while (true) {
            System.out.println("\n--- USER SERVICE MENU ---");
            System.out.println("1. Создать пользователя");
            System.out.println("2. Найти по ID");
            System.out.println("3. Обновить имя");
            System.out.println("4. Вывести всех пользователей");
            System.out.println("5. Удалить");
            System.out.println("6. Выход");
            System.out.print("Выберите действие: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // очистка буфера

            switch (choice) {
                case 1 -> {
                    System.out.print("Имя: "); String name = scanner.nextLine();
                    System.out.print("Email: "); String email = scanner.nextLine();
                    System.out.print("Возраст: "); int age = scanner.nextInt();
                    userService.createNewUser(name, email, age);
                }
                case 2 -> {
                    System.out.print("Введите ID: ");
                    userService.printUserInfo(scanner.nextLong());
                }
                case 3 -> {
                    System.out.print("Введите ID: "); Long id = scanner.nextLong();
                    scanner.nextLine();
                    System.out.print("Новое имя: "); String newName = scanner.nextLine();
                    userService.updateUserName(id, newName);
                }
                case 4 -> {
                    userService.findAllUsers().forEach(System.out::println);
                }
                case 5 -> {
                    System.out.print("Введите ID для удаления: ");
                    userService.removeUser(scanner.nextLong());
                }
                case 6 -> {
                    System.out.println("Завершение работы...");
                    return;
                }
                default -> System.out.println("Неверный ввод!");
            }
        }
    }
}
