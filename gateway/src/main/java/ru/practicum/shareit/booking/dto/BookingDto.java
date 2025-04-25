package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;

public record BookingDto(
        Long itemId,
        LocalDateTime start,
        LocalDateTime end
) {
}
