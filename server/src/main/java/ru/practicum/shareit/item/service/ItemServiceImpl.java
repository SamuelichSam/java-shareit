package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingRespDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repo.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repo.CommentRepository;
import ru.practicum.shareit.item.repo.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repo.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repo.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Transactional
    @Override
    public ItemDto createItem(Long userId, ItemDto itemDto) {
        log.info("Создание вещи - {} пользователя с id - {}", itemDto, userId);
        User owner = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        ItemRequest request = null;
        Long requestId = null;
        if (itemDto.requestId() != null) {
            request = itemRequestRepository.findById(itemDto.requestId())
                    .orElseThrow(() -> new NotFoundException("Запрос не найден"));
            requestId = itemDto.requestId();
        }
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(owner);
        item.setRequest(request);
        Item savedItem = itemRepository.save(item);
        return ItemMapper.toDto(savedItem, requestId);
    }

    @Override
    public ItemDto getItemById(Long itemId, Long userId) {
        log.info("Получение вещи с id - {}", itemId);
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));
        List<CommentResponseDto> comments = commentRepository.findAllByItemId(itemId).stream()
                .map(CommentMapper::toDto)
                .collect(Collectors.toList());
        BookingRespDto lastBooking = null;
        BookingRespDto nextBooking = null;
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
        return ItemMapper.toDto(item, lastBooking, nextBooking, comments);
    }

    @Override
    public List<ItemDto> getAllUserItems(Long userId) {
        log.info("Получение списка вещей пользователя с id - {}", userId);
        User user = userRepository.findById(userId).orElseThrow(()
                -> new NotFoundException("Пользователь не найден"));
        List<Item> items = itemRepository.findByOwnerId(userId);
        List<Long> itemIds = items.stream().map(Item::getId).toList();
        Map<Long, List<Booking>> bookingsByItem = bookingRepository.findByItemIdIn(itemIds)
                .stream()
                .collect(Collectors.groupingBy(booking -> booking.getItem().getId()));
        Map<Long, List<Comment>> commentsByItem = commentRepository.findByItemIdIn(itemIds)
                .stream()
                .collect(Collectors.groupingBy(comment -> comment.getItem().getId()));
        LocalDateTime now = LocalDateTime.now();
        return items.stream()
                .map(item -> {
                    List<Booking> itemBookings = bookingsByItem.getOrDefault(item.getId(), List.of());
                    BookingRespDto lastBooking = itemBookings.stream()
                            .filter(b -> b.getStart().isBefore(now) &&
                                    b.getStatus() == Status.APPROVED)
                            .max(Comparator.comparing(Booking::getStart))
                            .map(BookingMapper::toDto)
                            .orElse(null);
                    BookingRespDto nextBooking = itemBookings.stream()
                            .filter(b -> b.getStart().isAfter(now) &&
                                    b.getStatus() == Status.APPROVED)
                            .min(Comparator.comparing(Booking::getStart))
                            .map(BookingMapper::toDto)
                            .orElse(null);
                    List<CommentResponseDto> comments = commentsByItem.getOrDefault(item.getId(), List.of())
                            .stream()
                            .map(CommentMapper::toDto)
                            .collect(Collectors.toList());
                    Long requestId = item.getRequest() != null ? item.getRequest().getId() : null;
                    return new ItemDto(
                            item.getId(),
                            item.getName(),
                            item.getDescription(),
                            item.getAvailable(),
                            user,
                            requestId,
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
        log.info("Обновление вещи с id - {}", itemId);
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
        log.info("Удаление вещи с id - {}", itemId);
        itemRepository.deleteById(itemId);
    }

    @Transactional
    @Override
    public CommentResponseDto addComment(Long itemId, Long userId, CommentDto commentDto) {
        log.info("Добавление отзыва к вещи с id - {}", itemId);
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

    public BookingRespDto getLastBooking(Long itemId) {
        List<Booking> bookings = bookingRepository.findByItemIdAndEndBeforeOrderByEndDesc(itemId,
                LocalDateTime.now());
        if (!bookings.isEmpty()) {
            return BookingMapper.toDto(bookings.getFirst());
        }
        return null;
    }

    public BookingRespDto getNextBooking(Long itemId) {
        List<Booking> bookings = bookingRepository.findAllByItemIdAndStartAfterOrderByStartAsc(itemId,
                LocalDateTime.now());
        if (!bookings.isEmpty()) {
            return BookingMapper.toDto(bookings.getFirst());
        }
        return null;
    }

    public List<CommentResponseDto> getCommentsByItemId(Long itemId) {
        List<Comment> comments = commentRepository.findAllByItemId(itemId);
        return comments.stream()
                .map(CommentMapper::toDto)
                .collect(Collectors.toList());
    }
}
