package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;

public record CommentResponseDto(
        Long id,
        String text,
        Item item,
        String authorName,
        LocalDateTime created
) {
}
