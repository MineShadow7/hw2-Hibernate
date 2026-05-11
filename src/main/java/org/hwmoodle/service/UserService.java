package org.hwmoodle.service;

import org.hwmoodle.dto.UserRequestDto;
import org.hwmoodle.dto.UserResponseDto;
import org.hwmoodle.mapper.UserMapper;
import org.hwmoodle.model.User;
import org.hwmoodle.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final Supplier<Boolean> dbAvailableSupplier;

    @Autowired
    public UserService(UserRepository userRepository) {
        this(userRepository, () -> true);
    }

    UserService(UserRepository userRepository, Supplier<Boolean> dbAvailableSupplier) {
        this.userRepository = Objects.requireNonNull(userRepository, "userRepository");
        this.dbAvailableSupplier = Objects.requireNonNull(dbAvailableSupplier, "dbAvailableSupplier");
    }

    private boolean isDatabaseAvailable() {
        return dbAvailableSupplier.get();
    }

    public boolean createNewUser(String name, String email, int age){
        if(!isDatabaseAvailable()){
            System.err.println("БД недоступна. Операция отменена.");
            return false;
        }

        if (email == null || !email.contains("@")) {
            System.err.println("Некорректный email: " + email);
            return false;
        }
        User user = new User(name, email, age);
        userRepository.save(user);
        return true;
    }

    public void printUserInfo(Long id){
        if(!isDatabaseAvailable()){
            System.err.println("БД недоступна. Операция отменена.");
            return;
        }

        User user = userRepository.findById(id).orElse(null);
        if (user != null){
            System.out.println("Найдено: " + user.toString());
        } else {
            System.out.println("Пользователь с id " + id + " не найден.");
        }
    }

    public void updateUserName(Long id, String newName){
        if(!isDatabaseAvailable()){
            System.err.println("БД недоступна. Операция отменена.");
            return;
        }

        User user = userRepository.findById(id).orElse(null);
        if (user != null){
            user.setName(newName);
            userRepository.save(user);
            System.out.println("Имя успешно обновлено");
        } else {
            System.out.println("Пользователь с id " + id + " не найден.");
        }
    }

    public void removeUser(Long id){
        if(!isDatabaseAvailable()){
            System.err.println("БД недоступна. Операция отменена.");
            return;
        }

        userRepository.deleteById(id);
    }

    public List<User> findAllUsers() {
        if(!isDatabaseAvailable()){
            System.err.println("БД недоступна. Операция отменена.");
            return new ArrayList<>();
        }

        return userRepository.findAll();
    }

    public UserResponseDto createUser(UserRequestDto request) {
        User user = UserMapper.toEntity(request);
        return UserMapper.toDto(userRepository.save(user));
    }

    public Optional<UserResponseDto> getUser(Long id) {
        return userRepository.findById(id).map(UserMapper::toDto);
    }

    public Optional<UserResponseDto> updateUser(Long id, UserRequestDto request) {
        return userRepository.findById(id).map(existing -> {
            UserMapper.apply(existing, request);
            return UserMapper.toDto(userRepository.save(existing));
        });
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public List<UserResponseDto> listUsers() {
        return userRepository.findAll().stream().map(UserMapper::toDto).toList();
    }
}
