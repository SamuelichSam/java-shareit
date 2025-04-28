package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRespDto;
import ru.practicum.shareit.booking.model.State;

import java.util.List;

public interface BookingService {
    BookingRespDto createBooking(BookingDto bookingDto, Long userId);

    BookingRespDto approveBooking(Long bookingId, Long userId, Boolean approved);

    BookingRespDto getBookingById(Long bookingId, Long userId);

    List<BookingRespDto> getAllBookingsByUser(Long userId, State state);

    List<BookingRespDto> getAllBookingsByItems(Long userId, State state);
}
