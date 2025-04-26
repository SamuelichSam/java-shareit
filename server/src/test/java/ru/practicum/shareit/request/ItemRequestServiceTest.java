package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestRespDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class ItemRequestServiceTest {
    @Autowired
    UserService userService;
    @Autowired
    ItemRequestService itemRequestService;
    static UserDto userDtoInit1;
    static UserDto userDtoInit2;
    static ItemRequestDto itemRequestDto;

    @BeforeAll
    static void init() {
        userDtoInit1 = new UserDto(null, "user1@email.com", "user1");
        userDtoInit2 = new UserDto(null, "user2@email.com", "user2");
        itemRequestDto = new ItemRequestDto("requestDescription");
    }

    @Test
    void createItemRequest() {
        UserDto userDto = userService.createUser(userDtoInit1);
        ItemRequestRespDto itemRequestRespDto = itemRequestService.createItemRequest(itemRequestDto, userDto.id());

        assertThat(itemRequestRespDto.description()).isEqualTo(itemRequestDto.description());
    }

    @Test
    void createItemRequestWithWrongUser() {
        assertThatThrownBy(() ->
                itemRequestService.createItemRequest(itemRequestDto, 0L)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void getAllUserRequests() {
        UserDto userDto = userService.createUser(userDtoInit1);
        ItemRequestRespDto itemRequestRespDto1 = itemRequestService.createItemRequest(itemRequestDto, userDto.id());
        ItemRequestRespDto itemRequestRespDto2 = itemRequestService.createItemRequest(itemRequestDto, userDto.id());
        ItemRequestRespDto itemRequestRespDto3 = itemRequestService.createItemRequest(itemRequestDto, userDto.id());

        List<ItemRequestRespDto> requests = itemRequestService.getAllUserRequests(userDto.id());

        assertThat(requests.size()).isEqualTo(3);
    }

    @Test
    void getAllRequests() {
        UserDto userDto1 = userService.createUser(userDtoInit1);
        UserDto userDto2 = userService.createUser(userDtoInit2);
        ItemRequestRespDto itemRequestRespDto1 = itemRequestService.createItemRequest(itemRequestDto, userDto1.id());
        ItemRequestRespDto itemRequestRespDto2 = itemRequestService.createItemRequest(itemRequestDto, userDto2.id());

        List<ItemRequestRespDto> requests = itemRequestService.getAllRequests(userDto1.id());

        assertThat(requests.size()).isEqualTo(2);
    }

    @Test
    void getRequestById() {
        UserDto userDto = userService.createUser(userDtoInit1);
        ItemRequestRespDto itemRequestRespDto = itemRequestService.createItemRequest(itemRequestDto, userDto.id());

        ItemRequestRespDto findedItemRequest = itemRequestService.getRequestById(itemRequestRespDto.id());

        assertThat(findedItemRequest.description()).isEqualTo(itemRequestRespDto.description());
    }
}
