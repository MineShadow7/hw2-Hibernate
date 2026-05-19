package org.hwmoodle.core.mapper;

import org.hwmoodle.core.dto.UserRequestDto;
import org.hwmoodle.core.dto.UserResponseDto;
import org.hwmoodle.core.model.User;

public final class UserMapper {
    private UserMapper() {
    }

    public static User toEntity(UserRequestDto dto) {
        return new User(dto.name(), dto.email(), dto.age());
    }

    public static UserResponseDto toDto(User user) {
        return new UserResponseDto(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getAge(),
                user.getCreatedAt()
        );
    }

    public static void apply(User user, UserRequestDto dto) {
        user.setName(dto.name());
        user.setEmail(dto.email());
        user.setAge(dto.age());
    }
}

