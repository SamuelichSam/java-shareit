package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.State;

import java.util.List;

public interface BookingService {
    BookingResponseDto createBooking(BookingDto bookingDto, Long userId);

    BookingResponseDto approveBooking(Long bookingId, Long userId, Boolean approved);

    BookingResponseDto findBookingById(Long bookingId, Long userId);

    List<BookingResponseDto> findAllBookingsByUser(Long userId, State state);

    List<BookingResponseDto> findAllBookingsByItems(Long userId, State state);
}
