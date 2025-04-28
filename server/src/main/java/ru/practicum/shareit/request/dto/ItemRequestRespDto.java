package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

public record ItemRequestRespDto(
        Long id,
        String description,
        UserDto requester,
        LocalDateTime created,
        List<ItemDto> items
) {
}
