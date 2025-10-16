package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record UserCreateDto(
        @NotNull String name,
        @Email @NotNull String email
) {
}
