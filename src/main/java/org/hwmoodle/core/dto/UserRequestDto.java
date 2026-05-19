package org.hwmoodle.core.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserRequestDto(
        @NotBlank String name,
        @NotBlank @Email String email,
        @NotNull @Min(0) Integer age
) {
}

