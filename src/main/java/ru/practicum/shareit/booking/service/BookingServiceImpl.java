package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repo.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repo.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repo.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Transactional
    @Override
    public BookingResponseDto createBooking(BookingDto bookingDto, Long userId) {
        log.info("Создание бронирования - {} вещи с id - {}", bookingDto, userId);
        User user = userRepository.findById(userId).orElseThrow(()
                -> new NotFoundException("Пользователь не найден"));
        Item item = itemRepository.findById(bookingDto.itemId()).orElseThrow(()
                -> new NotFoundException("Вещь не найдена"));
        if (!item.getAvailable()) {
            throw new ValidationException("Вещь не доступна для бронирования");
        }
        Booking booking = bookingRepository.save(BookingMapper.toBooking(bookingDto, user, item));
        return BookingMapper.toDto(booking);
    }

    @Override
    public BookingResponseDto approveBooking(Long bookingId, Long userId, Boolean approved) {
        log.info("Подтверждение/отклонение бронирования с id - {} пользователем с id - {}", bookingId, userId);
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(()
                -> new NotFoundException("Бронирование не найдено"));
        Long itemUserId = booking.getItem().getOwner().getId();
        if (!userId.equals(itemUserId)) {
            throw new ValidationException("Пользователь не является владельцем вещи");
        }
        if (!booking.getStatus().equals(Status.WAITING)) {
            throw new ValidationException("Бронирование уже подтверждено или отклонено");
        }
        if (approved) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        Booking savedBooking = bookingRepository.save(booking);
        return BookingMapper.toDto(savedBooking);
    }

    @Override
    public BookingResponseDto findBookingById(Long bookingId, Long userId) {
        log.info("Получение бронирования с id - {} пользователем с id - {}", bookingId, userId);
        User user = userRepository.findById(userId).orElseThrow(()
                -> new NotFoundException("Пользователь с id " + userId + " не найден"));
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(()
                -> new NotFoundException("Бронирование не найдено"));
        if (!userId.equals(booking.getUser().getId())) {
            throw new ValidationException("Этот пользователь не является автором бронирования");
        }
        return BookingMapper.toDto(booking);
    }

    @Override
    public List<BookingResponseDto> findAllBookingsByUser(Long userId, State state) {
        log.info("Получение всех бронирований пользователя с id - {}", userId);
        LocalDateTime now = LocalDateTime.now();
        userRepository.findById(userId).orElseThrow(()
                -> new NotFoundException("Пользователь с id " + userId + " не найден"));
        List<Booking> bookings = switch (state) {
            case PAST -> bookingRepository.findByUserIdAndEndBeforeOrderByStartDesc(userId, now);
            case FUTURE -> bookingRepository.findByUserIdAndStartAfterOrderByStartDesc(userId, now);
            case WAITING -> bookingRepository.findByUserIdAndStatusOrderByStartDesc(userId, Status.WAITING);
            case REJECTED -> bookingRepository.findByUserIdAndStatusOrderByStartDesc(userId, Status.REJECTED);
            case CURRENT -> bookingRepository.findCurrentBookings(userId, now);
            default -> bookingRepository.findByUserIdOrderByStartDesc(userId);
        };
        return bookings.stream()
                .map(BookingMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingResponseDto> findAllBookingsByItems(Long userId, State state) {
        log.info("Получение всех бронирований всех вещей пользователя с id - {}", userId);
        userRepository.findById(userId).orElseThrow(()
                -> new NotFoundException("Пользователь с id " + userId + " не найден"));
        List<Booking> bookings = switch (state) {
            case PAST, REJECTED, FUTURE, WAITING, CURRENT ->
                    bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, state);
            default -> bookingRepository.findAllByItemOwnerIdOrderByStartDesc(userId);
        };
        return bookings.stream()
                .map(BookingMapper::toDto)
                .collect(Collectors.toList());
    }
}
