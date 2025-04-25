package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestRespDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestRespDto createItemRequest(ItemRequestDto itemRequestDto, Long userId);

    List<ItemRequestRespDto> getAllUserRequests(Long userId);

    List<ItemRequestRespDto> getAllRequests(Long userId);

    ItemRequestRespDto getRequestById(Long requestId);
}
