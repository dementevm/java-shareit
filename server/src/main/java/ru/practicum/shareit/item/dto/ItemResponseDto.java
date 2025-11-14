package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.user.dto.UserResponseDto;

import java.util.List;

public record ItemResponseDto(
        long id,
        String name,
        String description,
        boolean available,
        UserResponseDto owner,
        ItemRequestResponseDto request,
        BookingShortDto lastBooking,
        BookingShortDto nextBooking,
        List<CommentResponseDto> comments
) {
}