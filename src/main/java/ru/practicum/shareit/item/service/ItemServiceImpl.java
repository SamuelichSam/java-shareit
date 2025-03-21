package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;

    public ItemServiceImpl(@Qualifier("itemStorageImpl") ItemStorage itemStorage) {
        this.itemStorage = itemStorage;
    }

    @Override
    public ItemDto createItem(Long userId, ItemDto itemDto) {
        Item item = ItemMapper.toItem(itemDto);
        itemStorage.createItem(userId, item);
        return ItemMapper.toDto(item);
    }

    @Override
    public ItemDto getItemById(Long itemId) {
        return ItemMapper.toDto(itemStorage.getItemById(itemId)
                .orElseThrow(() -> {
                    return new NotFoundException("Вещь не найдена");
                }));
    }

    @Override
    public List<ItemDto> getAllUserItems(Long userId) {
        List<Item> items = itemStorage.getAllUserItems(userId);
        return items.stream()
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getItemByText(String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        String editedText = text.toLowerCase();
        return itemStorage.getItemByText(text, editedText)
                .stream()
                .map(ItemMapper::toDto)
                .toList();
    }

    @Override
    public ItemDto updateItem(Long itemId, Long userId, ItemDto itemDto) {
        Item item = itemStorage.getItemById(itemId)
                .orElseThrow(() -> {
                    throw new NotFoundException("Вещь не найдена");
                });
        if (!item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("У этой вещи другой пользователь");
        }
        if (itemDto.name() != null) {
            item.setName(itemDto.name());
        }
        if (itemDto.description() != null) {
            item.setDescription(itemDto.description());
        }
        if (itemDto.available() != null) {
            item.setAvailable(itemDto.available());
        }
        return ItemMapper.toDto(itemStorage.updateItem(itemId, item));
    }

    @Override
    public void deleteItem(Long itemId) {
        itemStorage.deleteItem(itemId);
    }
}
