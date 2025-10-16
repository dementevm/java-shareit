package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import ru.practicum.shareit.user.model.User;

public record ItemCreateDto(
        @NotBlank String name,
        @NotBlank String description,
        @NotNull Boolean available,
        User owner,
        String request
) {
}