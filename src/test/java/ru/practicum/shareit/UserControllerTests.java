package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTests {
    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    private UserDto userDto;

    @Test
    void saveNewUserSuccessTest() throws Exception {
        userDto = new UserDto(-1L, "NewUser", "user@email.com");
        when(userService.create(any()))
                .thenReturn(userDto);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @Test
    void saveNewUserEmptyNameTest() throws Exception {
        userDto = new UserDto(-1L, "", "user@email.com");
        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllSuccessTest() throws Exception {
        userDto = new UserDto(-1L, "test1", "user@email.com");
        UserDto userDto2 = new UserDto(-1L, "test2", "user@email.com");
        when(userService.getAll())
                .thenReturn(List.of(userDto, userDto2));

        mvc.perform(get("/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", is(userDto.getName())))
                .andExpect(jsonPath("$[0].email", is(userDto.getEmail())))
                .andExpect(jsonPath("$[1].name", is(userDto2.getName())))
                .andExpect(jsonPath("$[1].email", is(userDto2.getEmail())));
    }

    @Test
    void getByIdSuccessTest() throws Exception {
        userDto = new UserDto(-1L, "test1", "user@email.com");
        when(userService.getById(1L))
                .thenReturn(userDto);

        mvc.perform(get("/users/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @Test
    void getByIdExceptionTest() throws Exception {
        when(userService.getById(999L))
                .thenThrow(new NotFoundException(""));

        mvc.perform(get("/users/999")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateUserSuccessTest() throws Exception {
        userDto = new UserDto(1L, "update", "user@email.com");
        when(userService.update(1L, userDto))
                .thenReturn(userDto);

        mvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @Test
    void updateUserExceptionTest() throws Exception {
        userDto = new UserDto(1L, "update", "user@email.com");
        when(userService.update(999L, userDto))
                .thenThrow(new NotFoundException("User with id 999 not exists in the DB"));

        mvc.perform(patch("/users/999")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("User with id 999 not exists in the DB")));
    }
}