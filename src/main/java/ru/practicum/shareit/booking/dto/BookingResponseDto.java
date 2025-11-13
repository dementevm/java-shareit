package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.user.dto.UserResponseDto;

import java.time.LocalDateTime;

public record BookingResponseDto(
        long id,
        LocalDateTime start,
        LocalDateTime end,
        String status,
        ItemShortDto item,
        UserResponseDto booker
) {
}
