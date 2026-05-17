package org.hwmoodle.dto;

import java.time.LocalDateTime;

public record UserResponseDto(
        Long id,
        String name,
        String email,
        Integer age,
        LocalDateTime createdAt
) {
}

