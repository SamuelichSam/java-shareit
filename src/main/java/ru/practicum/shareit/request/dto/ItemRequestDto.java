package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.user.model.User;

import java.time.LocalDate;

/**
 * TODO Sprint add-item-requests.
 */
public record ItemRequestDto(
        Long id,
        String description,
        User requester,
        LocalDate created
) {
}
