package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.user.model.User;

public record ItemResponseDto(
        long id,
        String name,
        String description,
        boolean available,
        User owner,
        String request
) {
}
