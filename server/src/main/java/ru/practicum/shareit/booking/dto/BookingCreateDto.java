package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.Instant;

public record BookingCreateDto(
        @JsonFormat(shape = JsonFormat.Shape.STRING,
                pattern = "yyyy-MM-dd'T'HH:mm:ss",
                timezone = "UTC")
        Instant start,
        @JsonFormat(shape = JsonFormat.Shape.STRING,
                pattern = "yyyy-MM-dd'T'HH:mm:ss",
                timezone = "UTC")
        Instant end,
        Long itemId
) {
}
