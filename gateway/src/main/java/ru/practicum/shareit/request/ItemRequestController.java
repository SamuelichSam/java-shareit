package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

@RequiredArgsConstructor
@RestController
@RequestMapping("/requests")
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;
    private static final String USER_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> createItemRequest(@RequestHeader(USER_HEADER) Long userId,
                                                    @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestClient.createItemRequest(itemRequestDto, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUserRequests(@RequestHeader(USER_HEADER) Long userId) {
        return itemRequestClient.getAllUserRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(@RequestHeader(USER_HEADER) Long userId) {
        return itemRequestClient.getAllRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@PathVariable Long requestId) {
        return itemRequestClient.getRequestById(requestId);
    }
}
