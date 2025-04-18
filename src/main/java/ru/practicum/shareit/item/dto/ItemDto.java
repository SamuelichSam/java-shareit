package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public record ItemDto(
        Long id,
        @NotBlank
        @NotNull
        String name,
        @NotBlank
        @NotNull
        String description,
        @NotNull
        Boolean available,
        User owner,
        Long request,
        BookingResponseDto lastBooking,
        BookingResponseDto nextBooking,
        List<CommentResponseDto> comments
) {
}
