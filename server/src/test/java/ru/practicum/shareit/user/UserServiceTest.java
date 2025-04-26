package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class UserServiceTest {
    @Autowired
    UserService userService;
    static UserDto userDtoInit1;
    static UserDto userDtoInit2;

    @BeforeAll
    static void init() {
        userDtoInit1 = new UserDto(null, "user1@email.com", "user1");
        userDtoInit2 = new UserDto(null, "user2@email.com", "user2");
    }

    @Test
    void createUser() {
        UserDto userDto = userService.createUser(userDtoInit1);

        assertThat(userDto.name()).isEqualTo(userDtoInit1.name());
        assertThat(userDto.email()).isEqualTo(userDtoInit1.email());
    }

    @Test
    void updateUser() {
        UserDto userDto = userService.createUser(userDtoInit1);

        UserDto userDto2 = userService.updateUser(userDto.id(), userDtoInit2);

        assertThat(userDto2.name()).isEqualTo(userDtoInit2.name());
        assertThat(userDto2.email()).isEqualTo(userDtoInit2.email());
    }

    @Test
    void deleteUser() {
        UserDto userDto = userService.createUser(userDtoInit1);
        UserDto findingUserDto = userService.getUserById(userDto.id());

        assertThat(userDto.id()).isEqualTo(findingUserDto.id());

        userService.deleteUserById(userDto.id());

        assertThatThrownBy(() -> {
            userService.getUserById(userDto.id());
        }).isInstanceOf(NotFoundException.class);
    }

    @Test
    void getUserById() {
        UserDto userDto = userService.createUser(userDtoInit1);
        UserDto findingUserDto = userService.getUserById(userDto.id());

        assertThat(userDto.id()).isEqualTo(findingUserDto.id());
        assertThat(userDto.name()).isEqualTo(findingUserDto.name());
        assertThat(userDto.email()).isEqualTo(findingUserDto.email());
    }

    @Test
    void getAllUsers() {
        userService.createUser(userDtoInit1);
        userService.createUser(userDtoInit2);

        List<UserDto> users = userService.getAllUsers();

        assertThat(users.size()).isEqualTo(2);

    }
}
