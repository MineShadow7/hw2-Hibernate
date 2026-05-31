package org.hwmoodle.core.dto;

import org.springframework.hateoas.server.core.Relation;

import java.time.LocalDateTime;

@Relation(collectionRelation = "users", itemRelation = "user")
public record UserResponseDto(
        Long id,
        String name,
        String email,
        Integer age,
        LocalDateTime createdAt
) {
}

