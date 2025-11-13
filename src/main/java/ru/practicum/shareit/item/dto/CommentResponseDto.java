package ru.practicum.shareit.item.dto;

import java.time.Instant;

public record CommentResponseDto(
        long id,
        String text,
        String authorName,
        Instant created
) {
}
