package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestRespDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;
    private static final String USER_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ItemRequestRespDto createItemRequest(@RequestHeader(USER_HEADER) Long userId,
                                                @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestService.createItemRequest(itemRequestDto, userId);
    }

    @GetMapping
    public List<ItemRequestRespDto> getAllUserRequests(@RequestHeader(USER_HEADER) Long userId) {
        return itemRequestService.getAllUserRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestRespDto> getAllRequests(@RequestHeader(USER_HEADER) Long userId) {
        return itemRequestService.getAllRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestRespDto getRequestById(@PathVariable Long requestId) {
        return itemRequestService.getRequestById(requestId);
    }
}
