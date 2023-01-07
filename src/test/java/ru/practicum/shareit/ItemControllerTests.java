package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.exception.controller.ExceptionApiHandler;
import ru.practicum.shareit.exception.model.BadRequestException;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTests {
    @MockBean
    private ItemService itemService;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    private ItemDto itemDto;

    @Test
    void getAllSuccessTest() throws Exception {
        User user = new User(1L, "Name", "email@email.com");
        itemDto = new ItemDto(-1L, "Name", "Description", true, user, null,
                null, null, new ArrayList<>());
        ItemDto itemDto2 = new ItemDto(-1L, "Name2", "Description2", true, user, null,
                null, null, new ArrayList<>());
        when(itemService.getAll(0, 100))
                .thenReturn(List.of(itemDto, itemDto2));

        mvc.perform(get("/items")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", is(itemDto.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$[1].name", is(itemDto2.getName())))
                .andExpect(jsonPath("$[1].description", is(itemDto2.getDescription())))
                .andExpect(jsonPath("$[1].available", is(itemDto.getAvailable())));
    }

    @Test
    void getByIdSuccessTest() throws Exception {
        User user = new User(1L, "Name", "email@email.com");
        itemDto = new ItemDto(-1L, "Name", "Description", true, user, null,
                null, null, new ArrayList<>());
        when(itemService.getById(anyLong(), anyLong()))
                .thenReturn(itemDto);

        mvc.perform(get("/items/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
    }

    @Test
    void getByIdExceptionTest() throws Exception {
        when(itemService.getById(1L, 1L))
                .thenThrow(new NotFoundException("Item with id 1 not exists in the DB"));

        mvc.perform(get("/items/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("Item with id 1 not exists in the DB")));
    }


    @Test
    void getByNameSuccessTest() throws Exception {
        User user = new User(1L, "Name", "email@email.com");
        itemDto = new ItemDto(-1L, "Name", "Description", true, user, null,
                null, null, new ArrayList<>());
        when(itemService.getByNameOrDescription("name", 0, 100))
                .thenReturn(List.of(itemDto));

        mvc.perform(get("/items/search?text=name")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", is(itemDto.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemDto.getAvailable())));
    }

    @Test
    void createSuccessTest() throws Exception {
        User user = new User(1L, "Name", "email@email.com");
        itemDto = new ItemDto(-1L, "Name", "Description", true, user, null,
                null, null, new ArrayList<>());
        when(itemService.create(anyLong(), any()))
                .thenReturn(itemDto);

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
    }


    @Test
    void createExceptionTest() throws Exception {
        User user = new User(1L, "Name", "email@email.com");
        itemDto = new ItemDto(-1L, "Name", "Description", true, user, null,
                null, null, new ArrayList<>());
        when(itemService.create(anyLong(), any()))
                .thenThrow(new BadRequestException("Available flag can't be null"));

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Available flag can't be null")));
    }

    @Test
    void updateSuccessTest() throws Exception {
        User user = new User(1L, "Name", "email@email.com");
        itemDto = new ItemDto(-1L, "Name", "Description", true, user, null,
                null, null, new ArrayList<>());
        when(itemService.update(anyLong(), anyLong(), any()))
                .thenReturn(itemDto);

        mvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
    }

    @Test
    void updateExceptionTest() throws Exception {
        User user = new User(1L, "Name", "email@email.com");
        itemDto = new ItemDto(-1L, "Name", "Description", true, user, null,
                null, null, new ArrayList<>());
        when(itemService.update(anyLong(), anyLong(), any()))
                .thenThrow(new NotFoundException("User with id 1 not exists in the DB"));

        mvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("User with id 1 not exists in the DB")));
    }

    @Test
    void addCommentSuccessTest() throws Exception {
        CommentDto comment = new CommentDto(-1L, "text", "author", LocalDateTime.now());
        when(itemService.addComment(anyLong(), anyLong(), any()))
                .thenReturn(comment);

        mvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(comment))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void addCommentExceptionTest() throws Exception {
        CommentDto comment = new CommentDto(-1L, "text", "author", LocalDateTime.now());
        when(itemService.addComment(anyLong(), anyLong(), any()))
                .thenThrow(new BadRequestException("User with id 1 can't left the comment for booking with id 1"));

        mvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(comment))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("User with id 1 can't left the comment for booking with id 1")));
    }
}