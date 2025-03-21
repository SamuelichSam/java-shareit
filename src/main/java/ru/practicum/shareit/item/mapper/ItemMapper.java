package ru.practicum.shareit.item.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

@UtilityClass
public class ItemMapper {
    public static Item toItem(ItemDto itemDto) {
        var item = new Item();
        item.setId(itemDto.id());
        item.setName(itemDto.name());
        item.setDescription(itemDto.description());
        item.setAvailable(itemDto.available());
        item.setOwner(itemDto.owner());
        item.setRequest(itemDto.request());
        return item;
    }

    public static ItemDto toDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwner(),
                item.getRequest()
        );
    }
}
