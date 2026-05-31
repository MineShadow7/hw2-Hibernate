package org.hwmoodle.core.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hwmoodle.core.dto.UserRequestDto;
import org.hwmoodle.core.dto.UserResponseDto;
import org.hwmoodle.core.kafka.UserNotificationPublisher;
import org.hwmoodle.core.mapper.UserMapper;
import org.hwmoodle.core.model.User;
import org.hwmoodle.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserNotificationPublisher notificationPublisher;

    @Transactional
    public UserResponseDto createUser(UserRequestDto request) {
        log.info("Creating new user with email: {}", request.email());
        if (request.email() == null || !request.email().contains("@")) {
            log.warn("Invalid email provided: {}", request.email());
            throw new IllegalArgumentException("Email must contain @");
        }
        User user = UserMapper.toEntity(request);
        User saved = userRepository.save(user);
        log.info("User created successfully with id: {}", saved.getId());

        notificationPublisher.publishUserCreatedEvent(saved.getEmail());

        return UserMapper.toDto(saved);
    }

    @Transactional(readOnly = true)
    public Optional<UserResponseDto> getUser(Long id) {
        log.debug("Fetching user with id: {}", id);
        return userRepository.findById(id).map(user -> {
            log.debug("User found with id: {}", id);
            return UserMapper.toDto(user);
        });
    }

    @Transactional
    public Optional<UserResponseDto> updateUser(Long id, UserRequestDto request) {
        log.info("Updating user with id: {}", id);
        return userRepository.findById(id).map(existing -> {
            UserMapper.apply(existing, request);
            User updated = userRepository.save(existing);
            log.info("User updated successfully with id: {}", id);
            return UserMapper.toDto(updated);
        }).or(() -> {
            log.warn("User not found for update with id: {}", id);
            return Optional.empty();
        });
    }

    @Transactional
    public void deleteUser(Long id) {
        log.info("Deleting user with id: {}", id);
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            String email = user.getEmail();
            userRepository.deleteById(id);
            log.info("User deleted successfully with id: {}", id);
            notificationPublisher.publishUserDeletedEvent(email);
        } else {
            log.warn("User not found for deletion with id: {}", id);
        }
    }

    @Transactional(readOnly = true)
    public List<UserResponseDto> listUsers() {
        log.debug("Fetching all users");
        List<UserResponseDto> users = userRepository.findAll().stream().map(UserMapper::toDto).toList();
        log.debug("Found {} users", users.size());
        return users;
    }
}
