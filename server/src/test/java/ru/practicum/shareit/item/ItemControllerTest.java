package ru.practicum.shareit.item;

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
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ItemControllerTest {
    private final ObjectMapper mapper = new ObjectMapper();
    private MockMvc mvc;

    @Mock
    ItemService itemService;

    @InjectMocks
    private ItemController itemController;
    private UserDto userDto;
    private ItemDto itemDto;
    private Item item;
    private CommentDto commentDto;
    private CommentResponseDto commentResponseDto;

    @BeforeEach
    void init() {
        LocalDateTime now = LocalDateTime.now();

        mvc = MockMvcBuilders
                .standaloneSetup(itemController)
                .build();
        userDto = new UserDto(0L, "user", "user@email.com");
        itemDto = new ItemDto(0L, "itemDto", "description", true, null,
                null, null, null, null);
        User user = new User(1L, "name", "user@email.com");

        item = new Item(1L, "item", "description", true, user, null);
        commentDto = new CommentDto("description");
        commentResponseDto = new CommentResponseDto(0L, "text", item, "name", now);
    }

    @Test
    void createItem() throws Exception {
        when(itemService.createItem(anyLong(), any()))
                .thenReturn(itemDto);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userDto.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(0))
                .andExpect(jsonPath("$.name").value("itemDto"))
                .andExpect(jsonPath("$.available").value(true))
                .andExpect(jsonPath("$.description").value("description"));
    }

    @Test
    void getItemById() throws Exception {
        when(itemService.getItemById(anyLong(), anyLong()))
                .thenReturn(itemDto);

        mvc.perform(get("/items/" + itemDto.id())
                        .content(mapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userDto.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(0))
                .andExpect(jsonPath("$.name").value("itemDto"))
                .andExpect(jsonPath("$.available").value(true))
                .andExpect(jsonPath("$.description").value("description"));
    }

    @Test
    void getAllUserItems() throws Exception {
        when(itemService.getAllUserItems(anyLong()))
                .thenReturn(List.of(itemDto));

        mvc.perform(get("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userDto.id()))
                .andExpect(status().isOk())
                .andExpect((result -> {
                    String json = result.getResponse().getContentAsString();
                    List<ItemDto> dtos = mapper.readValue(json, new TypeReference<>() {
                    });
                    if (dtos.isEmpty()) {
                        throw new AssertionError("Пустой лист c ItemDto");
                    }
                }));
    }

    @Test
    void getItemByText() throws Exception {
        when(itemService.getItemByText(anyString()))
                .thenReturn(List.of(itemDto));

        mvc.perform(get("/items/search?text=" + anyString())
                        .content(mapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect((result -> {
                    String json = result.getResponse().getContentAsString();
                    List<ItemDto> dtos = mapper.readValue(json, new TypeReference<>() {
                    });
                    if (dtos.isEmpty()) {
                        throw new AssertionError("Пустой лист c ItemDto");
                    }
                }));
    }

    @Test
    void deleteItem() throws Exception {
        mvc.perform(delete("/items/" + itemDto.id())
                        .content(mapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(itemService, times(1))
                .deleteItem(anyLong());
    }

    @Test
    void addComment() throws Exception {
        when(itemService.addComment(anyLong(), anyLong(), any()))
                .thenReturn(commentResponseDto);

        mvc.perform(post("/items/" + itemDto.id() + "/comment")
                        .content(mapper.writeValueAsString(commentDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userDto.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(0))
                .andExpect(jsonPath("$.text").value("text"))
                .andExpect(jsonPath("$.item.name").value("item"))
                .andExpect(jsonPath("$.authorName").value("name"));
    }
}
