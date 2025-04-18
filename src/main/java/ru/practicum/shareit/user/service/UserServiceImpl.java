package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repo.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Transactional
    @Override
    public UserDto createUser(UserDto userDto) {
        log.info("Создание пользователя - {}", userDto);
        User user = userRepository.save(UserMapper.toUser(userDto));
        return UserMapper.toDto(user);
    }

    @Override
    public List<UserDto> getAllUsers() {
        log.info("Получение списка всех пользователей");
        return userRepository.findAll()
                .stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUserById(Long id) {
        log.info("Получение пользователя с id - {}", id);
        User user = userRepository.findById(id).orElseThrow();
        return UserMapper.toDto(user);
    }

    @Transactional
    @Override
    public UserDto updateUser(Long id, UserDto userDto) {
        log.info("Обновление пользователя с id - {}", id);
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        if (userDto.name() != null) {
            user.setName(userDto.name());
        }
        if (userDto.email() != null) {
            user.setEmail(userDto.email());
        }
        userRepository.save(user);
        return UserMapper.toDto(user);
    }

    @Transactional
    @Override
    public void deleteUserById(Long id) {
        log.info("Удаление пользователя с id - {}", id);
        userRepository.deleteById(id);
    }
}
