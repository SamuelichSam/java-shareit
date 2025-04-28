package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRespDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class ItemServiceTest {
    @Autowired
    ItemService itemService;
    @Autowired
    UserService userService;
    @Autowired
    BookingService bookingService;
    @Autowired
    ItemRequestService itemRequestService;
    static ItemDto itemDtoInit1;
    static ItemDto itemDtoInit2;
    static ItemDto itemDtoInitReq;
    static UserDto userDtoInit1;
    static UserDto userDtoInit2;
    static UserDto userDtoInitBook;

    @BeforeAll
    static void init() {
        itemDtoInit1 = new ItemDto(null, "name1", "description1", true, null, null,
                null, null, null);
        itemDtoInit2 = new ItemDto(null, "name2", "description2", true, null, null,
                null, null, null);
        itemDtoInitReq = new ItemDto(null, "name2", "description2", true, null, 0L,
                null, null, null);
        userDtoInit1 = new UserDto(null, "user1@email.com", "user1");
        userDtoInit2 = new UserDto(null, "user2@email.com", "user2");
        userDtoInitBook = new UserDto(null, "userbook@email.com", "userbook");
    }

    @Test
    void createItem() {
        UserDto userDto = userService.createUser(userDtoInit1);
        ItemDto itemDto = itemService.createItem(userDto.id(), itemDtoInit1);

        assertThat(itemDto.name()).isEqualTo(itemDtoInit1.name());
        assertThat(itemDto.description()).isEqualTo(itemDtoInit1.description());
        assertThat(itemDto.available()).isEqualTo(itemDtoInit1.available());
    }

    @Test
    void createItemWithWrongRequest() {
        UserDto userDto = userService.createUser(userDtoInit1);

        assertThatThrownBy(() ->
                itemService.createItem(userDto.id(), itemDtoInitReq)).isInstanceOf(NotFoundException.class);

    }

    @Test
    void updateItem() {
        UserDto userDto = userService.createUser(userDtoInit1);
        ItemDto itemDto = itemService.createItem(userDto.id(), itemDtoInit1);
        ItemDto updatedItemDto = itemService.updateItem(itemDto.id(), userDto.id(), itemDtoInit2);

        assertThat(updatedItemDto.name()).isEqualTo(itemDtoInit2.name());
        assertThat(updatedItemDto.description()).isEqualTo(itemDtoInit2.description());
        assertThat(updatedItemDto.available()).isEqualTo(itemDtoInit2.available());
    }

    @Test
    void updateItemWrongUser() {
        UserDto userDto = userService.createUser(userDtoInit1);
        ItemDto itemDto = itemService.createItem(userDto.id(), itemDtoInit1);

        assertThatThrownBy(() ->
                itemService.updateItem(itemDto.id(), 10L, itemDto)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void getItemById() {
        UserDto userDto = userService.createUser(userDtoInit1);
        ItemDto itemDto = itemService.createItem(userDto.id(), itemDtoInit1);
        ItemDto findedItemDto = itemService.getItemById(itemDto.id(), userDto.id());

        assertThat(findedItemDto.name()).isEqualTo(itemDto.name());
        assertThat(findedItemDto.description()).isEqualTo(itemDto.description());
        assertThat(findedItemDto.available()).isEqualTo(itemDto.available());
    }

    @Test
    void getAllUserItems() {
        UserDto userDto = userService.createUser(userDtoInit1);
        ItemDto itemDto1 = itemService.createItem(userDto.id(), itemDtoInit1);
        ItemDto itemDto2 = itemService.createItem(userDto.id(), itemDtoInit2);
        List<ItemDto> findedItems = itemService.getAllUserItems(userDto.id());

        assertThat(findedItems.size()).isEqualTo(2);
    }

    @Test
    void getAllWrongUserItems() {
        assertThatThrownBy(() ->
                itemService.createItem(10L, itemDtoInitReq)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void getItemByText() {
        UserDto userDto = userService.createUser(userDtoInit1);
        ItemDto itemDto1 = itemService.createItem(userDto.id(), itemDtoInit1);
        ItemDto itemDto2 = itemService.createItem(userDto.id(), itemDtoInit2);
        List<ItemDto> findedByName = itemService.getItemByText(itemDto1.name());
        List<ItemDto> findedByDesc = itemService.getItemByText(itemDto1.description());

        assertThat(findedByName.size()).isEqualTo(1);
        assertThat(findedByDesc.size()).isEqualTo(1);
    }

    @Test
    void getItemByEmptyText() {
        UserDto userDto = userService.createUser(userDtoInit1);
        ItemDto itemDto1 = itemService.createItem(userDto.id(), itemDtoInit1);
        ItemDto itemDto2 = itemService.createItem(userDto.id(), itemDtoInit2);
        List<ItemDto> findedByName = itemService.getItemByText("");
        List<ItemDto> findedByDesc = itemService.getItemByText("");

        assertThat(findedByName.size()).isEqualTo(0);
        assertThat(findedByDesc.size()).isEqualTo(0);
    }

    @Test
    void deleteItem() {
        UserDto userDto = userService.createUser(userDtoInit1);
        ItemDto itemDto = itemService.createItem(userDto.id(), itemDtoInit1);
        ItemDto findedItemDto = itemService.getItemById(itemDto.id(), userDto.id());

        assertThat(itemDto.id()).isEqualTo(findedItemDto.id());

        itemService.deleteItem(itemDto.id());

        assertThatThrownBy(() -> {
            itemService.getItemById(itemDto.id(), userDto.id());
        }).isInstanceOf(NotFoundException.class);
    }

    @Test
    void addComment() {
        UserDto userDto = userService.createUser(userDtoInit1);
        ItemDto itemDto = itemService.createItem(userDto.id(), itemDtoInit1);
        UserDto bookerDto = userService.createUser(userDtoInit2);
        BookingDto bookingDto = new BookingDto(itemDto.id(), LocalDateTime.now().minusHours(1), LocalDateTime.now());
        BookingRespDto bookingRespDto = bookingService.createBooking(bookingDto, bookerDto.id());
        bookingService.approveBooking(bookingRespDto.id(), userDto.id(), true);
        CommentDto commentDto = new CommentDto("comment");

        itemService.addComment(itemDto.id(), bookerDto.id(), commentDto);
        ItemDto itemDtoWithComment = itemService.getItemById(itemDto.id(), userDto.id());

        assertThat(itemDtoWithComment.comments().size()).isEqualTo(1);
    }

    @Test
    void addCommentWrongUser() {
        UserDto userDto = userService.createUser(userDtoInit1);
        ItemDto itemDto = itemService.createItem(userDto.id(), itemDtoInit1);
        UserDto bookerDto = userService.createUser(userDtoInit2);
        BookingDto bookingDto = new BookingDto(itemDto.id(), LocalDateTime.now().minusHours(1), LocalDateTime.now());
        BookingRespDto bookingRespDto = bookingService.createBooking(bookingDto, bookerDto.id());
        bookingService.approveBooking(bookingRespDto.id(), userDto.id(), true);
        CommentDto commentDto = new CommentDto("comment");

        assertThatThrownBy(() ->
                itemService.addComment(itemDto.id(), 10L, commentDto)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void addCommentWrongItem() {
        UserDto userDto = userService.createUser(userDtoInit1);
        ItemDto itemDto = itemService.createItem(userDto.id(), itemDtoInit1);
        UserDto bookerDto = userService.createUser(userDtoInit2);
        BookingDto bookingDto = new BookingDto(itemDto.id(), LocalDateTime.now().minusHours(1), LocalDateTime.now());
        BookingRespDto bookingRespDto = bookingService.createBooking(bookingDto, bookerDto.id());
        bookingService.approveBooking(bookingRespDto.id(), userDto.id(), true);
        CommentDto commentDto = new CommentDto("comment");

        assertThatThrownBy(() ->
                itemService.addComment(10L, bookerDto.id(), commentDto)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void addCommentWrongBooker() {
        UserDto userDto = userService.createUser(userDtoInit1);
        ItemDto itemDto = itemService.createItem(userDto.id(), itemDtoInit1);
        UserDto bookerDto = userService.createUser(userDtoInit2);
        UserDto wrongBookerDto = userService.createUser(userDtoInitBook);
        BookingDto bookingDto = new BookingDto(itemDto.id(), LocalDateTime.now().minusHours(1), LocalDateTime.now());
        BookingRespDto bookingRespDto = bookingService.createBooking(bookingDto, bookerDto.id());
        bookingService.approveBooking(bookingRespDto.id(), userDto.id(), true);
        CommentDto commentDto = new CommentDto("comment");

        assertThatThrownBy(() ->
                itemService.addComment(itemDto.id(), wrongBookerDto.id(), commentDto)).isInstanceOf(ValidationException.class);
    }
}
