package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemClient itemClient;
    private static final String USER_HEADER = "X-Sharer-User-Id";

    public ItemController(ItemClient itemClient) {
        this.itemClient = itemClient;
    }

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader(USER_HEADER) Long userId, @Valid @RequestBody ItemDto itemDto) {
        return itemClient.createItem(userId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@PathVariable Long itemId,
                               @RequestHeader(USER_HEADER) Long userId) {
        return itemClient.getItemById(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUserItems(@RequestHeader(USER_HEADER) Long userId) {
        return itemClient.getAllUserItems(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getItemByText(@RequestParam("text") String text) {
        return itemClient.getItemByText(text);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@PathVariable Long itemId,
                              @RequestHeader(USER_HEADER) Long userId,
                              @RequestBody ItemDto itemDto) {
        return itemClient.updateItem(itemId, userId, itemDto);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@PathVariable Long itemId) {
        itemClient.deleteItem(itemId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@PathVariable Long itemId,
                                         @RequestHeader(USER_HEADER) Long userId,
                                         @RequestBody CommentDto commentDto) {
        return itemClient.addComment(itemId, userId, commentDto);
    }
}
