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
import ru.practicum.shareit.item.dto.CommentDto;
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
public class ItemServiceTest {
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
    void createItem() {
        UserDto userDto = userService.createUser(userDtoInit1);
        ItemDto itemDto = itemService.createItem(userDto.id(), itemDtoInit1);

        assertThat(itemDto.name()).isEqualTo(itemDtoInit1.name());
        assertThat(itemDto.description()).isEqualTo(itemDtoInit1.description());
        assertThat(itemDto.available()).isEqualTo(itemDtoInit1.available());
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
}
