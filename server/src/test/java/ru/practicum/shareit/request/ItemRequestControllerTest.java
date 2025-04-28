package ru.practicum.shareit.request;

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
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestRespDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ItemRequestControllerTest {
    private final ObjectMapper mapper = new ObjectMapper();

    @Mock
    ItemRequestService itemRequestService;
    private MockMvc mvc;

    @InjectMocks
    private ItemRequestController controller;
    private UserDto userDto;
    private ItemRequestDto itemRequestDto;
    private ItemRequestRespDto itemRequestRespDto;

    @BeforeEach
    void init() {
        mvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();

        userDto = new UserDto(0L, "user", "user@email.com");
        itemRequestDto = new ItemRequestDto("description");
        itemRequestRespDto = new ItemRequestRespDto(1L, "description",
                userDto, null, null);
    }

    @Test
    void createItemRequest() throws Exception {
        when(itemRequestService.createItemRequest(any(ItemRequestDto.class), anyLong()))
                .thenReturn(itemRequestRespDto);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userDto.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("description"));
    }

    @Test
    void getAllUserRequests() throws Exception {
        when(itemRequestService.getAllUserRequests(anyLong()))
                .thenReturn(List.of(itemRequestRespDto));

        mvc.perform(get("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userDto.id()))
                .andExpect(status().isOk())
                .andExpect((result -> {
                    String json = result.getResponse().getContentAsString();
                    List<ItemRequestRespDto> dtos = mapper.readValue(json, new TypeReference<>() {
                    });
                    if (dtos.isEmpty()) {
                        throw new AssertionError("Пустой лист c ItemRequestRespDto");
                    }
                }));
    }

    @Test
    void getAllRequests() throws Exception {
        when(itemRequestService.getAllRequests(anyLong()))
                .thenReturn(List.of(itemRequestRespDto));

        mvc.perform(get("/requests/all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userDto.id()))
                .andExpect(status().isOk())
                .andExpect((result -> {
                    String json = result.getResponse().getContentAsString();
                    List<ItemRequestRespDto> dtos = mapper.readValue(json, new TypeReference<>() {
                    });
                    if (dtos.isEmpty()) {
                        throw new AssertionError("Пустой лист c ItemRequestRespDto");
                    }
                }));
    }

    @Test
    void getRequestById() throws Exception {
        when(itemRequestService.getRequestById(anyLong()))
                .thenReturn(itemRequestRespDto);

        mvc.perform(get("/requests/" + itemRequestRespDto.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userDto.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("description"));
    }
}
