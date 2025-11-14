package ru.practicum.shareit.user.dto;

public record UserUpdateDto(
        Long id,
        String name,
        String email
) {
}
