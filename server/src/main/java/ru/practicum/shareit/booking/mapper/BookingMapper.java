package ru.practicum.shareit.booking.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRespDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@UtilityClass
public class BookingMapper {
    public static Booking toBooking(BookingDto bookingDto, User user, Item item) {
        var booking = new Booking();
        booking.setId(bookingDto.itemId());
        booking.setStart(bookingDto.start());
        booking.setEnd(bookingDto.end());
        booking.setItem(item);
        booking.setUser(user);
        booking.setStatus(Status.WAITING);
        return booking;
    }

    public static BookingRespDto toDto(Booking booking) {
        return new BookingRespDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItem(),
                booking.getUser(),
                booking.getStatus()
        );
    }
}
