package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithCommentsDto;

import java.util.List;

public interface ItemService {
    ItemDto createItem(Long userId, ItemDto itemDto);

    ItemWithCommentsDto getItemById(Long itemId, Long userId);

    List<ItemWithCommentsDto> getAllUserItems(Long userId);

    List<ItemDto> getItemByText(String text);

    ItemDto updateItem(Long itemId, Long userId, ItemDto itemDto);

    void deleteItem(Long itemId);

    CommentResponseDto addComment(Long itemId, Long userId, CommentDto commentDto);
}
