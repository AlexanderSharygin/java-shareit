package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.model.BadRequestException;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {

    @Mock
    private UserRepository userRepository;

    private UserService userService;

    private User user;


    @BeforeEach
    public void setup() {
        user = new User(1L, "Name", "email@email.com");
        userService = new UserService(userRepository);
    }

    @Test
    public void getAllUserTest() {
        Mockito.when(userRepository.findAll())
                .thenReturn(List.of(user));
        List<UserDto> expectedResult = List.of(UserMapper.toUserDto(user));

        assertEquals(expectedResult, userService.getAll());
    }

    @Test
    public void getByIdWrongUserTest() {
        Mockito.when(userRepository.findById(1L))
                .thenThrow(new NotFoundException("User with id 1 not exists in the DB"));
        var exception = assertThrows(
                NotFoundException.class,
                () -> userService.getById(1));

        assertEquals("User with id 1 not exists in the DB", exception.getMessage());
    }

    @Test
    public void getByIdSucessTest() {
        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
        userService.getById(1);

        assertEquals(UserMapper.toUserDto(user), userService.getById(1));
    }

    @Test
    public void createEmptyEmailTest() {
        UserDto userDto = UserMapper.toUserDto(user);
        userDto.setEmail("");
        var exception = assertThrows(
                BadRequestException.class,
                () -> userService.create(userDto));

        assertEquals("Email can't be null", exception.getParameter());
    }

    @Test
    public void createEmptyNameTest() {
        UserDto userDto = UserMapper.toUserDto(user);
        userDto.setName("");
        var exception = assertThrows(
                BadRequestException.class,
                () -> userService.create(userDto));

        assertEquals("Name can't be null", exception.getParameter());
    }

    @Test
    public void createSuccessTest() {
        Mockito.when(userRepository.save(Mockito.any()))
                .thenReturn(user);
        UserDto userDto = UserMapper.toUserDto(user);

        assertEquals(userDto.getEmail(), userService.create(userDto).getEmail());
    }

    @Test
    public void deleteWrongUserIdTest() {
        Mockito.when(userRepository.findById(1L))
                .thenThrow(new NotFoundException("User with id 1 not exists in the DB"));
        var exception = assertThrows(
                NotFoundException.class,
                () -> userService.delete(1));

        assertEquals("User with id 1 not exists in the DB", exception.getMessage());
    }

    @Test
    public void updateByWrongIdTest() {
        Mockito.when(userRepository.findById(Mockito.any()))
                .thenThrow(new NotFoundException("User with id 1 not exists in the DB"));
        UserDto userDto = UserMapper.toUserDto(user);
        var exception = assertThrows(
                NotFoundException.class,
                () -> userService.update(2L, userDto));

        assertEquals("User with id 1 not exists in the DB", exception.getMessage());
    }

    @Test
    public void updateEmptyEmailTest() {
        user.setEmail("");
        user.setName("update");
        UserDto userDto = UserMapper.toUserDto(user);
        Mockito.when(userRepository.findById(Mockito.any()))
                .thenReturn(Optional.ofNullable(user));
        Mockito.when(userRepository.save(Mockito.any()))
                .thenReturn(user);
        UserDto result = userService.update(userDto.getId(), userDto);

        assertEquals(result.getName(), "update");
    }

    @Test
    public void updateEmptyNameTest() {
        user.setEmail("update@qqq.cv");
        user.setName("");
        UserDto userDto = UserMapper.toUserDto(user);
        Mockito.when(userRepository.findById(Mockito.any()))
                .thenReturn(Optional.ofNullable(user));
        Mockito.when(userRepository.save(Mockito.any()))
                .thenReturn(user);
        UserDto result = userService.update(userDto.getId(), userDto);

        assertEquals(result.getEmail(), "update@qqq.cv");
    }
}