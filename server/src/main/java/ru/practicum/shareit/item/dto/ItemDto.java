package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.booking.dto.BookingRespDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public record ItemDto(
        Long id,
        String name,
        String description,
        Boolean available,
        User owner,
        Long requestId,
        BookingRespDto lastBooking,
        BookingRespDto nextBooking,
        List<CommentResponseDto> comments
) {
}
