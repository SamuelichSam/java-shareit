package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    public UserServiceImpl(@Qualifier("userStorageImpl") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        checkEmailUnique(userDto.email());
        User user = UserMapper.toUser(userDto);
        userStorage.createUser(user);
        return UserMapper.toDto(user);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userStorage.getAllUsers()
                .stream()
                .map(UserMapper::toDto)
                .toList();
    }

    @Override
    public UserDto getUserById(Long id) {
        return UserMapper.toDto(userStorage.getUserById(id)
                .orElseThrow(() -> {
                    return new NotFoundException("Пользователь не найден");
                }));
    }

    @Override
    public UserDto updateUser(Long id, UserDto userDto) {
        User user = userStorage.getUserById(id).orElseThrow(() -> {
            return new NotFoundException("Пользователь не найден");
        });
        if (userDto.name() != null) {
            user.setName(userDto.name());
        }
        if (userDto.email() != null) {
            checkEmailUnique(userDto.email());
            user.setEmail(userDto.email());
        }
        userStorage.updateUser(id, user);
        return UserMapper.toDto(user);
    }

    @Override
    public void deleteUserById(Long id) {
        userStorage.deleteUserById(id);
    }

    public void checkEmailUnique(String email) {
        boolean isDuplicate = userStorage.getAllUsers().stream()
                .anyMatch(user -> user.getEmail().equals(email));
        if (isDuplicate) {
            throw new ConflictException("Email уже существует");
        }
    }
}
