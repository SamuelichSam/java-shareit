package ru.practicum.shareit.item.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ItemStorageImpl implements ItemStorage {
    private final Map<Long, Item> items = new HashMap<>();
    private final UserService userService;

    public ItemStorageImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Item createItem(Long userId, Item item) {
        log.info("Создание вещи - {} пользователя с id - {}", item, userId);
        User user = UserMapper.toUser(userService.getUserById(userId));
        item.setId(generateId());
        item.setOwner(user);
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Optional<Item> getItemById(Long itemId) {
        log.info("Получение вещи с id - {}", itemId);
        return Optional.ofNullable(items.get(itemId));
    }

    @Override
    public List<Item> getAllUserItems(Long userId) {
        log.info("Получение списка вещей пользователя с id - {}", userId);
        return items.values()
                .stream()
                .filter(item -> item.getOwner().getId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> getItemByText(String text, String editedText) {
        log.info("Получение списка вещей содержащие текст - {} в названии или описании", text);
        return items.values().stream()
                .filter(Item::getAvailable)
                .filter(item ->
                        (item.getName() != null && item.getName().toLowerCase().contains(editedText)) ||
                                (item.getDescription() != null && item.getDescription().toLowerCase().contains(editedText))
                )
                .collect(Collectors.toList());
    }

    @Override
    public Item updateItem(Long itemId, Item item) {
        log.info("Обновление вещи с id - {}", itemId);
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public void deleteItem(Long itemId) {
        log.info("Удаление вещи с id - {}", itemId);
        items.remove(itemId);
    }

    private Long generateId() {
        Long lastId = items.values().stream()
                .mapToLong(Item::getId)
                .max()
                .orElse(0L);
        return lastId + 1;
    }
}
