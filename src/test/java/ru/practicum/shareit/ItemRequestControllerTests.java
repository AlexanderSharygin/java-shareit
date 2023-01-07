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
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
public class ItemRequestControllerTests {
    @MockBean
    private RequestService requestService;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    private ItemRequestDto itemRequestDto;

    private User user;

    @Test
    void createSuccessTest() throws Exception {
        user = new User(1L, "Name", "email@email.com");
        itemRequestDto = new ItemRequestDto(-1L, user, "desc", LocalDateTime.now(), new ArrayList<>());
        when(requestService.create(any(), anyLong()))
                .thenReturn(itemRequestDto);

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.owner.id", is(itemRequestDto.getOwner().getId().intValue())))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())));
    }

    @Test
    void createExceptionTest() throws Exception {
        user = new User(1L, "Name", "email@email.com");
        itemRequestDto = new ItemRequestDto(-1L, user, "desc", LocalDateTime.now(), new ArrayList<>());
        when(requestService.create(any(), anyLong()))
                .thenThrow(new BadRequestException("Description can't be null"));

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Description can't be null")));
    }

    @Test
    void getUserRequestsWithResponsesSuccessTest() throws Exception {
        user = new User(1L, "Name", "email@email.com");
        itemRequestDto = new ItemRequestDto(-1L, user, "desc", LocalDateTime.now(), new ArrayList<>());
        ItemRequestDto itemRequestDto2 = new ItemRequestDto(-1L, user, "desc", LocalDateTime.now(), new ArrayList<>());
        when(requestService.getUserRequests(1L))
                .thenReturn(List.of(itemRequestDto, itemRequestDto2));

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].owner.id", is(itemRequestDto.getOwner().getId().intValue())))
                .andExpect(jsonPath("$[0].description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$[1].owner.id", is(itemRequestDto2.getOwner().getId().intValue())))
                .andExpect(jsonPath("$[1].description", is(itemRequestDto2.getDescription())));
    }

    @Test
    void getUserRequestsWithResponsesExceptionTest() throws Exception {
        when(requestService.getUserRequests(99L))
                .thenThrow(new NotFoundException("User with id 99 not exists in the DB"));

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 99)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("User with id 99 not exists in the DB")));
    }

    @Test
    void getAllRequestsWithResponsesSuccessTest() throws Exception {
        user = new User(1L, "Name", "email@email.com");
        itemRequestDto = new ItemRequestDto(-1L, user, "desc", LocalDateTime.now(), new ArrayList<>());
        ItemRequestDto itemRequestDto2 = new ItemRequestDto(-1L, user, "desc", LocalDateTime.now(), new ArrayList<>());
        when(requestService.getAllRequests(1L, 0, 100))
                .thenReturn(List.of(itemRequestDto, itemRequestDto2));

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].owner.id", is(itemRequestDto.getOwner().getId().intValue())))
                .andExpect(jsonPath("$[0].description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$[1].owner.id", is(itemRequestDto2.getOwner().getId().intValue())))
                .andExpect(jsonPath("$[1].description", is(itemRequestDto2.getDescription())));
    }

    @Test
    void getAllRequestsWithResponsesExceptionTest() throws Exception {
        when(requestService.getAllRequests(99L, 0, 100))
                .thenThrow(new NotFoundException("User with id 99 not exists in the DB"));

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 99)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("User with id 99 not exists in the DB")));
    }

    @Test
    void getRequestWithResponsesSuccessTest() throws Exception {
        user = new User(1L, "Name", "email@email.com");
        itemRequestDto = new ItemRequestDto(-1L, user, "desc", LocalDateTime.now(), new ArrayList<>());
        when(requestService.getRequestById(1L, 1L))
                .thenReturn(itemRequestDto);

        mvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.owner.id", is(itemRequestDto.getOwner().getId().intValue())))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())));
    }

    @Test
    void getRequestWithResponsesExceptionTest() throws Exception {
        when(requestService.getRequestById(99L, 1L))
                .thenThrow(new NotFoundException("User with id 99 not exists in the DB"));

        mvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", 99)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("User with id 99 not exists in the DB")));
    }
}