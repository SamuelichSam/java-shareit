package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import ru.practicum.shareit.user.model.User;

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
        String request
) {
}
