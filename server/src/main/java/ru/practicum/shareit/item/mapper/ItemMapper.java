package ru.practicum.shareit.item.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingRespDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@UtilityClass
public class ItemMapper {
    public static Item toItem(ItemDto itemDto) {
        var item = new Item();
        item.setId(itemDto.id());
        item.setName(itemDto.name());
        item.setDescription(itemDto.description());
        item.setAvailable(itemDto.available());
        item.setOwner(itemDto.owner());
        return item;
    }

    public static ItemDto toDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwner(),
                null,
                null,
                null,
                null
        );
    }

    public static ItemDto toDto(Item item, Long requestId) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwner(),
                requestId != null ? requestId : (item.getRequest() != null ? item.getRequest().getId() : null),
                null,
                null,
                null
        );
    }

    public static ItemDto toDto(Item item, BookingRespDto lastBooking,
                                BookingRespDto nextBooking,
                                List<CommentResponseDto> comments) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwner(),
                null,
                lastBooking,
                nextBooking,
                comments
        );
    }
}
