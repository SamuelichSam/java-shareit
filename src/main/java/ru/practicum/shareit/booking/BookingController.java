package ru.practicum.shareit.booking;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@RequestMapping("/bookings")
public class BookingController {
    private final BookingService bookingService;
    private static final String USER_HEADER = "X-Sharer-User-Id";

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingResponseDto createBooking(@RequestBody BookingDto bookingDto,
                                            @RequestHeader(USER_HEADER) Long userId) {
        return bookingService.createBooking(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto approveBooking(@PathVariable Long bookingId,
                                             @RequestHeader(USER_HEADER) Long userId,
                                             @RequestParam Boolean approved) {
        return bookingService.approveBooking(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto findBookingById(@PathVariable Long bookingId,
                                              @RequestHeader(USER_HEADER) Long userId) {
        return bookingService.findBookingById(bookingId, userId);
    }

    @GetMapping
    public List<BookingResponseDto> findAllBookingsByUser(@RequestHeader(USER_HEADER) Long userId,
                                                          @RequestParam(defaultValue = "ALL") State state) {
        return bookingService.findAllBookingsByUser(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> findAllBookingsByItems(@RequestHeader(USER_HEADER) Long userid,
                                                           @RequestParam(defaultValue = "ALL") State state) {
        return bookingService.findAllBookingsByItems(userid, state);
    }
}
