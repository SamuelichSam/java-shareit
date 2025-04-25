package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repo.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestRespDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repo.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repo.UserRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Transactional
    @Override
    public ItemRequestRespDto createItemRequest(ItemRequestDto itemRequestDto, Long userId) {
        log.info("Создание запроса - {} вещи пользователем с id - {}", itemRequestDto, userId);
        User user = userRepository.findById(userId).orElseThrow(()
                -> new NotFoundException("Пользователь не найден"));
        ItemRequest itemRequest = itemRequestRepository.save(ItemRequestMapper.toItemRequest(itemRequestDto, user));
        return ItemRequestMapper.toDto(itemRequest);
    }

    @Override
    public List<ItemRequestRespDto> getAllUserRequests(Long userId) {
        log.info("Получение всех запросов пользователя с id - {}", userId);
        User user = userRepository.findById(userId).orElseThrow(()
                -> new NotFoundException("Пользователь не найден"));
        List<ItemRequest> requests = itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(userId);
        List<Long> requestIds = requests.stream().map(ItemRequest::getId).toList();
        List<ItemDto> items = itemRepository.findAllByRequestId(requestIds)
                .stream()
                .map(ItemMapper::toDto)
                .toList();
        return requests.stream()
                .map(request -> ItemRequestMapper.toDto(request, items))
                .toList();
    }

    @Override
    public List<ItemRequestRespDto> getAllRequests(Long userId) {
        log.info("Получение всех запросов пользователем с id - {}", userId);
        User user = userRepository.findById(userId).orElseThrow(()
                -> new NotFoundException("Пользователь не найден"));
        List<ItemRequest> requests = itemRequestRepository.findAll();
        return requests.stream()
                .map(ItemRequestMapper::toDto)
                .toList();
    }

    @Override
    public ItemRequestRespDto getRequestById(Long requestId) {
        log.info("Получение запроса с id - {}", requestId);
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(()
                        -> new NotFoundException("Запрос не найден"));
        List<ItemDto> items = itemRepository.findByRequestId(requestId)
                .stream()
                .map(ItemMapper::toDto)
                .toList();
        return ItemRequestMapper.toDto(itemRequest, items);
    }
}
