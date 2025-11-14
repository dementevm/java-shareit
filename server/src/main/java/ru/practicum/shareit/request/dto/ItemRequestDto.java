package ru.practicum.shareit.request.dto;

import java.time.Instant;
import java.util.List;

public record ItemRequestDto(
        Long id,
        String description,
        Instant created,
        List<ItemRequestItemDto> items
) {
}
