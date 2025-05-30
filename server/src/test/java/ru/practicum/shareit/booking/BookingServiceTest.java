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
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
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
    static ItemDto itemDtoInit3;
    static UserDto userDtoInit1;
    static UserDto userDtoInit2;

    @BeforeAll
    static void init() {
        itemDtoInit1 = new ItemDto(null, "name1", "description1", true, null, null,
                null, null, null);
        itemDtoInit2 = new ItemDto(null, "name2", "description2", true, null, null,
                null, null, null);
        itemDtoInit3 = new ItemDto(null, "name3", "description3", false, null, null,
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
    void createBookingWrongUser() {
        UserDto userDto1 = userService.createUser(userDtoInit1);
        UserDto userDto2 = userService.createUser(userDtoInit2);
        ItemDto itemDto = itemService.createItem(userDto1.id(), itemDtoInit1);
        BookingDto bookingDto = new BookingDto(itemDto.id(), LocalDateTime.now().minusHours(1), LocalDateTime.now());

        assertThatThrownBy(() ->
                bookingService.createBooking(bookingDto, 10L)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void createBookingWrongItem() {
        UserDto userDto1 = userService.createUser(userDtoInit1);
        UserDto userDto2 = userService.createUser(userDtoInit2);
        ItemDto itemDto = itemService.createItem(userDto1.id(), itemDtoInit1);
        BookingDto bookingDto = new BookingDto(10L, LocalDateTime.now().minusHours(1), LocalDateTime.now());

        assertThatThrownBy(() ->
                bookingService.createBooking(bookingDto, userDto2.id())).isInstanceOf(NotFoundException.class);
    }

    @Test
    void createBookingWithNotAvailableItem() {
        UserDto userDto1 = userService.createUser(userDtoInit1);
        UserDto userDto2 = userService.createUser(userDtoInit2);
        ItemDto itemDto = itemService.createItem(userDto1.id(), itemDtoInit3);
        BookingDto bookingDto = new BookingDto(itemDto.id(), LocalDateTime.now().minusHours(1), LocalDateTime.now());

        assertThatThrownBy(() ->
                bookingService.createBooking(bookingDto, userDto2.id()))
                .isInstanceOf(ValidationException.class);
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
    void approveBookingWrongBooking() {
        UserDto userDto = userService.createUser(userDtoInit1);
        assertThatThrownBy(() ->
                bookingService.approveBooking(10L, userDto.id(), true))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void approveBookingWrongUser() {
        UserDto userDto1 = userService.createUser(userDtoInit1);
        UserDto userDto2 = userService.createUser(userDtoInit2);
        ItemDto itemDto = itemService.createItem(userDto1.id(), itemDtoInit1);
        BookingDto bookingDto = new BookingDto(itemDto.id(), LocalDateTime.now().minusHours(1), LocalDateTime.now());
        BookingRespDto bookingRespDto = bookingService.createBooking(bookingDto, userDto2.id());

        assertThatThrownBy(() ->
                bookingService.approveBooking(bookingRespDto.id(), 10L, true))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void approveBookingWrongStatus() {
        UserDto userDto1 = userService.createUser(userDtoInit1);
        UserDto userDto2 = userService.createUser(userDtoInit2);
        ItemDto itemDto = itemService.createItem(userDto1.id(), itemDtoInit1);
        BookingDto bookingDto = new BookingDto(itemDto.id(), LocalDateTime.now().minusHours(1), LocalDateTime.now());
        BookingRespDto bookingRespDto = bookingService.createBooking(bookingDto, userDto2.id());
        bookingService.approveBooking(bookingRespDto.id(), userDto1.id(), true);

        assertThatThrownBy(() ->
                bookingService.approveBooking(bookingRespDto.id(), userDto1.id(), true))
                .isInstanceOf(ValidationException.class);
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
    void getBookingByIdWrongUser() {
        UserDto userDto1 = userService.createUser(userDtoInit1);
        UserDto userDto2 = userService.createUser(userDtoInit2);
        ItemDto itemDto = itemService.createItem(userDto1.id(), itemDtoInit1);
        BookingDto bookingDto = new BookingDto(itemDto.id(), LocalDateTime.now().minusHours(1), LocalDateTime.now());
        BookingRespDto bookingRespDto = bookingService.createBooking(bookingDto, userDto2.id());

        assertThatThrownBy(() ->
                bookingService.getBookingById(bookingRespDto.id(), 10L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getBookingByIdWrongBooking() {
        UserDto userDto1 = userService.createUser(userDtoInit1);
        UserDto userDto2 = userService.createUser(userDtoInit2);
        ItemDto itemDto = itemService.createItem(userDto1.id(), itemDtoInit1);
        BookingDto bookingDto = new BookingDto(itemDto.id(), LocalDateTime.now().minusHours(1), LocalDateTime.now());
        BookingRespDto bookingRespDto = bookingService.createBooking(bookingDto, userDto2.id());

        assertThatThrownBy(() ->
                bookingService.getBookingById(10L, userDto2.id()))
                .isInstanceOf(NotFoundException.class);
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
    void getAllCurrentBookingsByUser() {
        UserDto userDto1 = userService.createUser(userDtoInit1);
        UserDto userDto2 = userService.createUser(userDtoInit2);
        ItemDto itemDto1 = itemService.createItem(userDto1.id(), itemDtoInit1);
        ItemDto itemDto2 = itemService.createItem(userDto1.id(), itemDtoInit2);
        BookingDto bookingDto1 = new BookingDto(itemDto1.id(), LocalDateTime.now().minusHours(1),
                LocalDateTime.now().plusMinutes(30));
        BookingDto bookingDto2 = new BookingDto(itemDto2.id(), LocalDateTime.now().minusHours(2),
                LocalDateTime.now().plusHours(1));
        bookingService.createBooking(bookingDto1, userDto2.id());
        bookingService.createBooking(bookingDto2, userDto2.id());

        List<BookingRespDto> findedBookings = bookingService.getAllBookingsByUser(userDto2.id(), State.CURRENT);

        assertThat(findedBookings.size()).isEqualTo(2);
    }

    @Test
    void getAllFutureBookingsByUser() {
        UserDto userDto1 = userService.createUser(userDtoInit1);
        UserDto userDto2 = userService.createUser(userDtoInit2);
        ItemDto itemDto1 = itemService.createItem(userDto1.id(), itemDtoInit1);
        ItemDto itemDto2 = itemService.createItem(userDto1.id(), itemDtoInit2);
        BookingDto bookingDto1 = new BookingDto(itemDto1.id(), LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(30));
        BookingDto bookingDto2 = new BookingDto(itemDto2.id(), LocalDateTime.now().plusMinutes(2),
                LocalDateTime.now().plusHours(1));
        bookingService.createBooking(bookingDto1, userDto2.id());
        bookingService.createBooking(bookingDto2, userDto2.id());

        List<BookingRespDto> findedBookings = bookingService.getAllBookingsByUser(userDto2.id(), State.FUTURE);

        assertThat(findedBookings.size()).isEqualTo(2);
    }

    @Test
    void getAllWaitingBookingsByUser() {
        UserDto userDto1 = userService.createUser(userDtoInit1);
        UserDto userDto2 = userService.createUser(userDtoInit2);
        ItemDto itemDto1 = itemService.createItem(userDto1.id(), itemDtoInit1);
        ItemDto itemDto2 = itemService.createItem(userDto1.id(), itemDtoInit2);
        BookingDto bookingDto1 = new BookingDto(itemDto1.id(), LocalDateTime.now().minusHours(1),
                LocalDateTime.now());
        BookingDto bookingDto2 = new BookingDto(itemDto2.id(), LocalDateTime.now().minusHours(2),
                LocalDateTime.now());
        bookingService.createBooking(bookingDto1, userDto2.id());
        bookingService.createBooking(bookingDto2, userDto2.id());

        List<BookingRespDto> findedBookings = bookingService.getAllBookingsByUser(userDto2.id(), State.WAITING);

        assertThat(findedBookings.size()).isEqualTo(2);
    }

    @Test
    void getAllPastBookingsByUser() {
        UserDto userDto1 = userService.createUser(userDtoInit1);
        UserDto userDto2 = userService.createUser(userDtoInit2);
        ItemDto itemDto1 = itemService.createItem(userDto1.id(), itemDtoInit1);
        ItemDto itemDto2 = itemService.createItem(userDto1.id(), itemDtoInit2);
        BookingDto bookingDto1 = new BookingDto(itemDto1.id(), LocalDateTime.now().minusHours(1),
                LocalDateTime.now().minusMinutes(30));
        BookingDto bookingDto2 = new BookingDto(itemDto2.id(), LocalDateTime.now().minusHours(2),
                LocalDateTime.now().minusHours(1));
        bookingService.createBooking(bookingDto1, userDto2.id());
        bookingService.createBooking(bookingDto2, userDto2.id());

        List<BookingRespDto> findedBookings = bookingService.getAllBookingsByUser(userDto2.id(), State.PAST);

        assertThat(findedBookings.size()).isEqualTo(2);
    }

    @Test
    void getAllBookingsByWrongUser() {
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

        bookingService.approveBooking(bookingRespDto1.id(), userDto1.id(), false);
        bookingService.approveBooking(bookingRespDto2.id(), userDto1.id(), false);

        assertThatThrownBy(() ->
                bookingService.getAllBookingsByUser(10L, State.REJECTED))
                .isInstanceOf(NotFoundException.class);
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

    @Test
    void getAllBookingsByItemsWrongUser() {
        UserDto userDto1 = userService.createUser(userDtoInit1);
        UserDto userDto2 = userService.createUser(userDtoInit2);
        ItemDto itemDto1 = itemService.createItem(userDto1.id(), itemDtoInit1);
        ItemDto itemDto2 = itemService.createItem(userDto1.id(), itemDtoInit2);
        BookingDto bookingDto1 = new BookingDto(itemDto1.id(), LocalDateTime.now().minusHours(1),
                LocalDateTime.now().minusMinutes(30));
        BookingDto bookingDto2 = new BookingDto(itemDto2.id(), LocalDateTime.now().minusHours(2),
                LocalDateTime.now().minusHours(1));
        bookingService.createBooking(bookingDto1, userDto2.id());
        bookingService.createBooking(bookingDto2, userDto2.id());

        assertThatThrownBy(() ->
                bookingService.getAllBookingsByItems(10L, State.ALL))
                .isInstanceOf(NotFoundException.class);
    }
}
