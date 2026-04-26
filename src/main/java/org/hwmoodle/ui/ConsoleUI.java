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

            int choice = readInt();

            switch (choice) {
                case 1 -> {
                    System.out.print("Имя: "); String name = scanner.nextLine();
                    System.out.print("Email: "); String email = scanner.nextLine();
                    System.out.print("Возраст: "); int age = readInt();
                    userService.createNewUser(name, email, age);
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
                    userService.findAllUsers().forEach(System.out::println);
                }
                case 5 -> {
                    System.out.print("Введите ID для удаления: ");
                    userService.removeUser(readLong());
                }
                case 6 -> {
                    System.out.println("Завершение работы...");
                    return;
                }
                default -> System.out.println("Неверный ввод. Ввод быть в виде целочисленного значения от 1 до 6.");
            }
        }
    }

    private int readInt(){
        while(true){
            try{
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Ошибка значения. Введите целое число.");
            }
        }
    }

    private Long readLong(){
        while(true){
            try{
                return Long.parseLong(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Ошибка значения. ID должен быть числом.");
            }
        }
    }
}
