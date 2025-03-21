package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemStorage {
    Item createItem(Long userId, Item item);

    Optional<Item> getItemById(Long itemId);

    List<Item> getAllUserItems(Long userId);

    List<Item> getItemByText(String text, String editedText);

    Item updateItem(Long itemId, Item item);

    void deleteItem(Long itemId);
}
