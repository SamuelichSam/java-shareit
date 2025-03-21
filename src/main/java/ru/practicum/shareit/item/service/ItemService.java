package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto createItem(Long userId, ItemDto itemDto);

    ItemDto getItemById(Long itemId);

    List<ItemDto> getAllUserItems(Long userId);

    List<ItemDto> getItemByText(String text);

    ItemDto updateItem(Long itemId, Long userId, ItemDto itemDto);

    void deleteItem(Long itemId);
}
