package ru.practicum.shareit.booking;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.State;

@RestController
@RequestMapping("/bookings")
public class BookingController {
    private final BookingClient bookingClient;
    private static final String USER_HEADER = "X-Sharer-User-Id";

    public BookingController(BookingClient bookingClient) {
        this.bookingClient = bookingClient;
    }

    @PostMapping
    public ResponseEntity<Object> createBooking(@RequestBody BookingDto bookingDto,
                                                @RequestHeader(USER_HEADER) Long userId) {
        return bookingClient.createBooking(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveBooking(@PathVariable Long bookingId,
                                         @RequestHeader(USER_HEADER) Long userId,
                                         @RequestParam Boolean approved) {
        return bookingClient.approveBooking(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> findBookingById(@PathVariable Long bookingId,
                                          @RequestHeader(USER_HEADER) Long userId) {
        return bookingClient.findBookingById(bookingId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> findAllBookingsByUser(@RequestHeader(USER_HEADER) Long userId,
                                                      @RequestParam(defaultValue = "ALL") State state) {
        return bookingClient.findAllBookingsByUser(userId, state);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> findAllBookingsByItems(@RequestHeader(USER_HEADER) Long userid,
                                                       @RequestParam(defaultValue = "ALL") State state) {
        return bookingClient.findAllBookingsByItems(userid, state);
    }
}
