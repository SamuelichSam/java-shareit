package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

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
        Long requestId
) {
}
