package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTests {
    @MockBean
    private BookingService bookingService;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    private BookingDto bookingDto;

    private User user;

    private Item item;

    @Test
    void getBookingsForUserSuccessTest() throws Exception {
        user = new User(1L, "Name", "email@email.com");
        item = new Item(1L, "Name", "Description", true, user, null);
        bookingDto = new BookingDto(1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2),
                BookingStatus.WAITING, 1L, user, item);
        BookingDto bookingDto2 = new BookingDto(2L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2),
                BookingStatus.WAITING, 1L, user, item);
        Pageable paging = PageRequest.of(0, 100);
        when(bookingService.getBookingsForUser("ALL", 1L, paging))
                .thenReturn(List.of(bookingDto, bookingDto2));

        mvc.perform(get("/bookings")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].itemId", is(bookingDto.getItemId().intValue())))
                .andExpect(jsonPath("$[1].itemId", is(bookingDto2.getItemId().intValue())));
    }

    @Test
    void getBookingsForUserExceptionTest() throws Exception {
        Pageable paging = PageRequest.of(0, 100);
        when(bookingService.getBookingsForUser("ALL", 1L, paging))
                .thenThrow(new NotFoundException("Item with id 1 not exists in the DB"));

        mvc.perform(get("/bookings")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("Item with id 1 not exists in the DB")));
    }

    @Test
    void getBookingByIdSuccessTest() throws Exception {
        user = new User(1L, "Name", "email@email.com");
        item = new Item(1L, "Name", "Description", true, user, null);
        bookingDto = new BookingDto(1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2),
                BookingStatus.WAITING, 1L, user, item);
        when(bookingService.getById(anyLong(), anyLong()))
                .thenReturn(bookingDto);

        mvc.perform(get("/bookings/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.itemId", is(bookingDto.getItemId().intValue())));
    }

    @Test
    void getBookingsByIdExceptionTest() throws Exception {
        when(bookingService.getById(anyLong(), anyLong()))
                .thenThrow(new NotFoundException("User with id 1 not exists in the DB"));

        mvc.perform(get("/bookings/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("User with id 1 not exists in the DB")));
    }

    @Test
    void getBookingsForUserItemsSuccessTest() throws Exception {
        user = new User(1L, "Name", "email@email.com");
        item = new Item(1L, "Name", "Description", true, user, null);
        bookingDto = new BookingDto(1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2),
                BookingStatus.WAITING, 1L, user, item);
        BookingDto bookingDto2 = new BookingDto(2L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2),
                BookingStatus.WAITING, 1L, user, item);
        Pageable paging = PageRequest.of(0, 100);
        when(bookingService.getBookingsForUserItems("ALL", 1L, paging))
                .thenReturn(List.of(bookingDto, bookingDto2));

        mvc.perform(get("/bookings/owner")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].itemId", is(bookingDto.getItemId().intValue())))
                .andExpect(jsonPath("$[1].itemId", is(bookingDto2.getItemId().intValue())));
    }

    @Test
    void getBookingsForUserItemsExceptionTest() throws Exception {
        Pageable paging = PageRequest.of(0, 100);
        when(bookingService.getBookingsForUserItems("ALL", 1L, paging))
                .thenThrow(new NotFoundException("Item with id 1 not exists in the DB"));

        mvc.perform(get("/bookings/owner")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("Item with id 1 not exists in the DB")));
    }

    @Test
    void createBookingSuccessTest() throws Exception {
        user = new User(1L, "Name", "email@email.com");
        item = new Item(1L, "Name", "Description", true, user, null);
        bookingDto = new BookingDto(1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2),
                BookingStatus.WAITING, 1L, user, item);
        when(bookingService.create(anyLong(), any()))
                .thenReturn(bookingDto);

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.itemId", is(bookingDto.getItemId().intValue())));
    }

    @Test
    void createBookingExceptionTest() throws Exception {
        user = new User(1L, "Name", "email@email.com");
        item = new Item(1L, "Name", "Description", true, user, null);
        bookingDto = new BookingDto(1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2),
                BookingStatus.WAITING, 1L, user, item);
        when(bookingService.create(anyLong(), any()))
                .thenThrow(new NotFoundException("User with id 1 not exists in the DB"));

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("User with id 1 not exists in the DB")));
    }

    @Test
    void updateStatusSuccessTest() throws Exception {
        user = new User(1L, "Name", "email@email.com");
        item = new Item(1L, "Name", "Description", true, user, null);
        bookingDto = new BookingDto(1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2),
                BookingStatus.WAITING, 1L, user, item);
        when(bookingService.changeBookingStatus(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(bookingDto);

        mvc.perform(patch("/bookings/1?approved=true")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.itemId", is(bookingDto.getItemId().intValue())));
    }

    @Test
    void updateStatusExceptionTest() throws Exception {
        when(bookingService.changeBookingStatus(anyLong(), anyLong(), anyBoolean()))
                .thenThrow(new NotFoundException("User with id 1 not exists in the DB"));

        mvc.perform(patch("/bookings/1?approved=true")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("User with id 1 not exists in the DB")));
    }
}