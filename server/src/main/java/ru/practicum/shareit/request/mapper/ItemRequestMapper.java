package ru.practicum.shareit.request.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestRespDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@UtilityClass
public class ItemRequestMapper {
    public static ItemRequest toItemRequest(ItemRequestDto itemRequestDto, User user) {
        var itemRequest = new ItemRequest();
        itemRequest.setDescription(itemRequestDto.description());
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setRequester(user);
        return itemRequest;
    }

    public static ItemRequestRespDto toDto(ItemRequest itemRequest) {
        return new ItemRequestRespDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                UserMapper.toDto(itemRequest.getRequester()),
                itemRequest.getCreated(),
                null
        );
    }

    public static ItemRequestRespDto toDto(ItemRequest itemRequest, List<ItemDto> items) {
        return new ItemRequestRespDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                UserMapper.toDto(itemRequest.getRequester()),
                itemRequest.getCreated(),
                items
        );
    }
}
