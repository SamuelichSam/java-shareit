package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repo.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithCommentsDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repo.CommentRepository;
import ru.practicum.shareit.item.repo.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repo.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Transactional
    @Override
    public ItemDto createItem(Long userId, ItemDto itemDto) {
        User owner = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(owner);
        itemRepository.save(item);
        return ItemMapper.toDto(item);
    }

    @Override
    public ItemWithCommentsDto getItemById(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));
        List<CommentResponseDto> comments = commentRepository.findByItemIdOrderByCreatedDesc(itemId).stream()
                .map(CommentMapper::toDto)
                .collect(Collectors.toList());
        BookingResponseDto lastBooking = null;
        BookingResponseDto nextBooking = null;
        List<Booking> lastBookings = bookingRepository.findAllByItemIdAndEndBeforeAndStatusOrderByEndDesc(itemId,
                LocalDateTime.now(), Status.APPROVED);
        if (!lastBookings.isEmpty() && userId.equals(item.getOwner().getId())) {
            lastBooking = BookingMapper.toDto(lastBookings.getFirst());
        }
        List<Booking> nextBookings = bookingRepository.findAllByItemIdAndStartAfterOrderByStartAsc(itemId,
                LocalDateTime.now());
        if (!nextBookings.isEmpty() && userId.equals(item.getOwner().getId())) {
            nextBooking = BookingMapper.toDto(nextBookings.getFirst());
        }
        return ItemMapper.toDtoWithComments(item, lastBooking, nextBooking, comments);
    }

    @Override
    public List<ItemWithCommentsDto> getAllUserItems(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(()
                -> new NotFoundException("Пользователь не найден"));
        List<Item> items = itemRepository.findByOwnerId(userId);
        return items.stream()
                .map(item -> {
                    BookingResponseDto lastBooking = getLastBooking(item.getId());
                    BookingResponseDto nextBooking = getNextBooking(item.getId());
                    List<CommentResponseDto> comments = getCommentsByItemId(item.getId());
                    return new ItemWithCommentsDto(
                            item.getId(),
                            item.getName(),
                            item.getDescription(),
                            item.getAvailable(),
                            user,
                            item.getRequest(),
                            lastBooking,
                            nextBooking,
                            comments
                    );
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getItemByText(String text) {
        log.info("Получение списка вещей содержащие текст - {} в названии или описании", text);
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        String editedText = text.toLowerCase();
        return itemRepository.findAll().stream()
                .filter(Item::getAvailable)
                .filter(item ->
                        (item.getName() != null && item.getName().toLowerCase().contains(editedText)) ||
                                (item.getDescription() != null && item.getDescription().toLowerCase().contains(editedText))
                )
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public ItemDto updateItem(Long itemId, Long userId, ItemDto itemDto) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));
        if (!item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("У этой вещи другой пользователь");
        }
        if (itemDto.name() != null) {
            item.setName(itemDto.name());
        }
        if (itemDto.description() != null) {
            item.setDescription(itemDto.description());
        }
        if (itemDto.available() != null) {
            item.setAvailable(itemDto.available());
        }
        return ItemMapper.toDto(itemRepository.save(item));
    }

    @Transactional
    @Override
    public void deleteItem(Long itemId) {
        itemRepository.deleteById(itemId);
    }

    @Transactional
    @Override
    public CommentResponseDto addComment(Long itemId, Long userId, CommentDto commentDto) {
        User user = userRepository.findById(userId).orElseThrow(()
                -> new NotFoundException("Пользователь не найден"));
        Item item = itemRepository.findById(itemId).orElseThrow(()
                -> new NotFoundException("Вещь не найдена"));
        List<Booking> bookings = bookingRepository.findByItemIdAndUserIdAndStatusAndEndBefore(itemId, userId,
                Status.APPROVED, LocalDateTime.now());
        if (bookings.isEmpty()) {
            throw new ValidationException("Пользователь не брал в аренду эту вещь");
        }
        Comment comment = CommentMapper.toComment(commentDto, user, item);
        comment.setCreated(LocalDateTime.now());
        Comment savedComment = commentRepository.save(comment);
        return CommentMapper.toDto(savedComment);
    }

    public BookingResponseDto getLastBooking(Long itemId) {
        List<Booking> bookings = bookingRepository.findByItemIdAndEndBeforeOrderByEndDesc(itemId,
                LocalDateTime.now());
        if (!bookings.isEmpty()) {
            return BookingMapper.toDto(bookings.getFirst());
        }
        return null;
    }

    public BookingResponseDto getNextBooking(Long itemId) {
        List<Booking> bookings = bookingRepository.findAllByItemIdAndStartAfterOrderByStartAsc(itemId,
                LocalDateTime.now());
        if (!bookings.isEmpty()) {
            return BookingMapper.toDto(bookings.getFirst());
        }
        return null;
    }

    public List<CommentResponseDto> getCommentsByItemId(Long itemId) {
        List<Comment> comments = commentRepository.findByItemIdOrderByCreatedDesc(itemId);
        return comments.stream()
                .map(CommentMapper::toDto)
                .collect(Collectors.toList());
    }
}
