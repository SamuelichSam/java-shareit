package ru.practicum.shareit.user.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
public class UserStorageImpl implements UserStorage {
    private Map<Long, User> users = new HashMap<>();

    @Override
    public User createUser(User user) {
        log.info("Создание пользователя - {}", user);
        user.setId(generateId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public List<User> getAllUsers() {
        log.info("Получение списка всех пользователей");
        return users.values().stream().toList();
    }

    @Override
    public Optional<User> getUserById(Long id) {
        log.info("Получение пользователя с id - {}", id);
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public User updateUser(Long id, User user) {
        log.info("Обновление пользователя с id - {}", id);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public void deleteUserById(Long id) {
        log.info("Удаление пользователя с id - {}", id);
        users.remove(id);
    }

    private Long generateId() {
        Long lastId = users.values().stream()
                .mapToLong(User::getId)
                .max()
                .orElse(0L);
        return lastId + 1;
    }
}
