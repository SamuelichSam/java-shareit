package ru.practicum.shareit.booking;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRespDto;
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
    public BookingRespDto createBooking(@RequestBody BookingDto bookingDto,
                                        @RequestHeader(USER_HEADER) Long userId) {
        return bookingService.createBooking(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingRespDto approveBooking(@PathVariable Long bookingId,
                                         @RequestHeader(USER_HEADER) Long userId,
                                         @RequestParam Boolean approved) {
        return bookingService.approveBooking(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingRespDto getBookingById(@PathVariable Long bookingId,
                                          @RequestHeader(USER_HEADER) Long userId) {
        return bookingService.getBookingById(bookingId, userId);
    }

    @GetMapping
    public List<BookingRespDto> getAllBookingsByUser(@RequestHeader(USER_HEADER) Long userId,
                                                      @RequestParam(defaultValue = "ALL") State state) {
        return bookingService.getAllBookingsByUser(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingRespDto> getAllBookingsByItems(@RequestHeader(USER_HEADER) Long userid,
                                                       @RequestParam(defaultValue = "ALL") State state) {
        return bookingService.getAllBookingsByItems(userid, state);
    }
}
