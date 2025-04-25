package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRespDto;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class BookingServiceTest {
    @Autowired
    ItemService itemService;
    @Autowired
    UserService userService;
    @Autowired
    BookingService bookingService;
    static ItemDto itemDtoInit1;
    static ItemDto itemDtoInit2;
    static UserDto userDtoInit1;
    static UserDto userDtoInit2;

    @BeforeAll
    static void init() {
        itemDtoInit1 = new ItemDto(null, "name1", "description1", true, null, null,
                null, null, null);
        itemDtoInit2 = new ItemDto(null, "name2", "description2", true, null, null,
                null, null, null);
        userDtoInit1 = new UserDto(null, "user1@email.com", "user1");
        userDtoInit2 = new UserDto(null, "user2@email.com", "user2");
    }

    @Test
    void createBooking() {
        UserDto userDto1 = userService.createUser(userDtoInit1);
        UserDto userDto2 = userService.createUser(userDtoInit2);
        ItemDto itemDto = itemService.createItem(userDto1.id(), itemDtoInit1);
        BookingDto bookingDto = new BookingDto(itemDto.id(), LocalDateTime.now().minusHours(1), LocalDateTime.now());
        BookingRespDto bookingRespDto = bookingService.createBooking(bookingDto, userDto2.id());

        assertThat(bookingRespDto.item().getId()).isEqualTo(itemDto.id());
        assertThat(bookingRespDto.booker().getId()).isEqualTo(userDto2.id());
        assertThat(bookingRespDto.start()).isEqualTo(bookingDto.start());
        assertThat(bookingRespDto.end()).isEqualTo(bookingDto.end());
    }

    @Test
    void approveBooking() {
        UserDto userDto1 = userService.createUser(userDtoInit1);
        UserDto userDto2 = userService.createUser(userDtoInit2);
        ItemDto itemDto = itemService.createItem(userDto1.id(), itemDtoInit1);
        BookingDto bookingDto = new BookingDto(itemDto.id(), LocalDateTime.now().minusHours(1), LocalDateTime.now());
        BookingRespDto bookingRespDto = bookingService.createBooking(bookingDto, userDto2.id());

        BookingRespDto approvedBooking = bookingService.approveBooking(bookingRespDto.id(), userDto1.id(), true);

        assertThat(approvedBooking.status()).isEqualTo(Status.APPROVED);
    }

    @Test
    void rejectBooking() {
        UserDto userDto1 = userService.createUser(userDtoInit1);
        UserDto userDto2 = userService.createUser(userDtoInit2);
        ItemDto itemDto = itemService.createItem(userDto1.id(), itemDtoInit1);
        BookingDto bookingDto = new BookingDto(itemDto.id(), LocalDateTime.now().minusHours(1), LocalDateTime.now());
        BookingRespDto bookingRespDto = bookingService.createBooking(bookingDto, userDto2.id());

        BookingRespDto approvedBooking = bookingService.approveBooking(bookingRespDto.id(), userDto1.id(), false);

        assertThat(approvedBooking.status()).isEqualTo(Status.REJECTED);
    }

    @Test
    void getBookingById() {
        UserDto userDto1 = userService.createUser(userDtoInit1);
        UserDto userDto2 = userService.createUser(userDtoInit2);
        ItemDto itemDto = itemService.createItem(userDto1.id(), itemDtoInit1);
        BookingDto bookingDto = new BookingDto(itemDto.id(), LocalDateTime.now().minusHours(1), LocalDateTime.now());
        BookingRespDto bookingRespDto = bookingService.createBooking(bookingDto, userDto2.id());

        BookingRespDto findedBooking = bookingService.getBookingById(bookingRespDto.id(), userDto2.id());

        assertThat(bookingRespDto.item().getId()).isEqualTo(findedBooking.item().getId());
        assertThat(bookingRespDto.start()).isEqualTo(findedBooking.start());
        assertThat(bookingRespDto.end()).isEqualTo(findedBooking.end());
    }

    @Test
    void getAllBookingsByUser() {
        UserDto userDto1 = userService.createUser(userDtoInit1);
        UserDto userDto2 = userService.createUser(userDtoInit2);
        ItemDto itemDto1 = itemService.createItem(userDto1.id(), itemDtoInit1);
        ItemDto itemDto2 = itemService.createItem(userDto1.id(), itemDtoInit2);
        BookingDto bookingDto1 = new BookingDto(itemDto1.id(), LocalDateTime.now().minusHours(1),
                LocalDateTime.now().minusMinutes(30));
        BookingDto bookingDto2 = new BookingDto(itemDto2.id(), LocalDateTime.now().minusHours(2),
                LocalDateTime.now().minusHours(1));
        BookingRespDto bookingRespDto1 = bookingService.createBooking(bookingDto1, userDto2.id());
        BookingRespDto bookingRespDto2 = bookingService.createBooking(bookingDto2, userDto2.id());

        BookingRespDto approvedBooking1 = bookingService.approveBooking(bookingRespDto1.id(), userDto1.id(), false);
        BookingRespDto approvedBooking2 = bookingService.approveBooking(bookingRespDto2.id(), userDto1.id(), false);

        List<BookingRespDto> findedBookings = bookingService.getAllBookingsByUser(userDto2.id(), State.REJECTED);

        assertThat(findedBookings.size()).isEqualTo(2);
    }

    @Test
    void getAllBookingsByItems() {
        UserDto userDto1 = userService.createUser(userDtoInit1);
        UserDto userDto2 = userService.createUser(userDtoInit2);
        ItemDto itemDto1 = itemService.createItem(userDto1.id(), itemDtoInit1);
        ItemDto itemDto2 = itemService.createItem(userDto1.id(), itemDtoInit2);
        BookingDto bookingDto1 = new BookingDto(itemDto1.id(), LocalDateTime.now().minusHours(1),
                LocalDateTime.now().minusMinutes(30));
        BookingDto bookingDto2 = new BookingDto(itemDto2.id(), LocalDateTime.now().minusHours(2),
                LocalDateTime.now().minusHours(1));
        BookingRespDto bookingRespDto1 = bookingService.createBooking(bookingDto1, userDto2.id());
        BookingRespDto bookingRespDto2 = bookingService.createBooking(bookingDto2, userDto2.id());

        List<BookingRespDto> findedBookings = bookingService.getAllBookingsByItems(userDto1.id(), State.ALL);

        assertThat(findedBookings.size()).isEqualTo(2);
    }
}
