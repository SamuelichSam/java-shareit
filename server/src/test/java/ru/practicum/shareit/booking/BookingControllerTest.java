package ru.practicum.shareit.booking;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRespDto;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class BookingControllerTest {
    private final ObjectMapper mapper = new ObjectMapper();
    private MockMvc mvc;

    @Mock
    BookingService bookingService;

    @InjectMocks
    private BookingController bookingController;
    private UserDto userDto;
    private ItemDto itemDto;
    private Item item;
    private User user;
    private BookingDto bookingDto;
    private BookingRespDto bookingRespDto;

    @BeforeEach
    void init() {
        LocalDateTime now = LocalDateTime.now();

        mvc = MockMvcBuilders
                .standaloneSetup(bookingController)
                .build();
        user = new User(1L, "name", "user@email.com");
        item = new Item(0L, "name", "description", true, user, null);
        userDto = new UserDto(0L, "nameDto", "userDto@email.com");
        bookingDto = new BookingDto(0L, null, null);
        bookingRespDto = new BookingRespDto(0L, null, null, item, user, Status.WAITING);
    }

    @Test
    void createBooking() throws Exception {
        when(bookingService.createBooking(any(BookingDto.class), anyLong()))
                .thenReturn(bookingRespDto);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userDto.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(0))
                .andExpect(jsonPath("$.item.id").value(item.getId()))
                .andExpect(jsonPath("$.booker.id").value(user.getId()))
                .andExpect(jsonPath("$.status").value("WAITING"));
    }

    @Test
    void approveBooking() throws Exception {
        when(bookingService.approveBooking(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(bookingRespDto);

        mvc.perform(patch("/bookings/" + bookingRespDto.id() + "/?approved=true")
                        .content(mapper.writeValueAsString(bookingDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userDto.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(0))
                .andExpect(jsonPath("$.item.name").value(item.getName()))
                .andExpect(jsonPath("$.booker.name").value(user.getName()))
                .andExpect(jsonPath("$.status").value("WAITING"));
    }

    @Test
    void getBookingById() throws Exception {
        when(bookingService.getBookingById(anyLong(), anyLong()))
                .thenReturn(bookingRespDto);

        mvc.perform(get("/bookings/" + bookingRespDto.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userDto.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(0))
                .andExpect(jsonPath("$.item.name").value(item.getName()))
                .andExpect(jsonPath("$.booker.name").value(user.getName()))
                .andExpect(jsonPath("$.status").value("WAITING"));
    }

    @Test
    void getAllBookingsByUser() throws Exception {
        when(bookingService.getAllBookingsByUser(anyLong(), any()))
                .thenReturn(List.of(bookingRespDto));

        mvc.perform(get("/bookings?state=")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userDto.id()))
                .andExpect(status().isOk())
                .andExpect((result -> {
                    String json = result.getResponse().getContentAsString();
                    List<BookingRespDto> dtos = mapper.readValue(json, new TypeReference<>() {
                    });
                    if (dtos.isEmpty()) {
                        throw new AssertionError("Пустой лист c BookingRespDto");
                    }
                }));
    }

    @Test
    void getAllBookingsByItems() throws Exception {
        when(bookingService.getAllBookingsByItems(anyLong(), any()))
                .thenReturn(List.of(bookingRespDto));

        mvc.perform(get("/bookings/owner?state=")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userDto.id()))
                .andExpect(status().isOk())
                .andExpect((result -> {
                    String json = result.getResponse().getContentAsString();
                    List<BookingRespDto> dtos = mapper.readValue(json, new TypeReference<>() {
                    });
                    if (dtos.isEmpty()) {
                        throw new AssertionError("Пустой лист c BookingRespDto");
                    }
                }));
    }
}
