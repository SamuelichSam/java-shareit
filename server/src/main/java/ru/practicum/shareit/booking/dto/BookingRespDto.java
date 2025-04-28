package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

public record BookingRespDto(
        Long id,
        LocalDateTime start,
        LocalDateTime end,
        Item item,
        User booker,
        Status status
) {
}
