package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.model.BadRequestException;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class RequestServiceTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    private RequestService requestService;

    private User user;
    private Item item;

    private ItemRequest itemRequest;

    @BeforeEach
    public void setup() {
        item = new Item(1L, "Test", "Test",
                false, new User(1L, "Test", "email@email.com"), null);
        user = new User(1L, "Name", "email@email.com");
        itemRequest = new ItemRequest(1L, user, "Test", Instant.now());
        requestService = new RequestService(userRepository, itemRequestRepository, itemRepository);
    }

    @Test
    public void createRequestWrongUserExceptionTest() {
        Mockito.when(userRepository.findById(999L))
                .thenThrow(new NotFoundException("User with id 999 not exists in the DB"));
        var exception = assertThrows(
                NotFoundException.class,
                () -> requestService.create(ItemRequestMapper.toItemRequestDto(itemRequest), 999L));

        assertEquals("User with id 999 not exists in the DB", exception.getMessage());
    }

    @Test
    public void createRequestSuccessTest() {
        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.ofNullable(user));
        Mockito.when(itemRequestRepository.save(Mockito.any()))
                .thenReturn(itemRequest);

        ItemRequestDto result = requestService.create(ItemRequestMapper.toItemRequestDto(itemRequest), 1L);
        assertEquals(result, ItemRequestMapper.toItemRequestDto(itemRequest));
    }


    @Test
    public void getUserRequestsWrongUserExceptionTest() {
        Mockito.when(userRepository.findById(999L))
                .thenThrow(new NotFoundException("User with id 999 not exists in the DB"));
        var exception = assertThrows(
                NotFoundException.class,
                () -> requestService.getUserRequests(999L));

        assertEquals("User with id 999 not exists in the DB", exception.getMessage());
    }

    @Test
    public void getUserRequestsSuccessTest() {
        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
        Mockito.when(itemRequestRepository.findByOwner_IdOrderByCreateDateTimeDesc(1L))
                .thenReturn(List.of(itemRequest));
        var result = requestService.getUserRequests(1);

        assertEquals(result, List.of(ItemRequestMapper.toItemRequestDto(itemRequest)));
    }

    @Test
    public void getAllRequestsWrongUserExceptionTest() {
        Mockito.when(userRepository.findById(999L))
                .thenThrow(new NotFoundException("User with id 999 not exists in the DB"));
        var exception = assertThrows(
                NotFoundException.class,
                () -> requestService.getAllRequests(999L, 1, 1));

        assertEquals("User with id 999 not exists in the DB", exception.getMessage());
    }

    @Test
    public void getAllRequestsSuccessTest() {
        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
        Mockito.when(itemRequestRepository.findByOwner_IdNotOrderByCreateDateTimeDesc(Mockito.any(), Mockito.any()))
                .thenReturn(List.of(itemRequest));
        var result = requestService.getAllRequests(1L, 1, 1);

        assertEquals(result, List.of(ItemRequestMapper.toItemRequestDto(itemRequest)));
    }

    @Test
    public void getRequestWrongUserExceptionTest() {
        Mockito.when(userRepository.findById(999L))
                .thenThrow(new NotFoundException("User with id 999 not exists in the DB"));
        var exception = assertThrows(
                NotFoundException.class,
                () -> requestService.getRequestById(999L, 1));

        assertEquals("User with id 999 not exists in the DB", exception.getMessage());
    }

    @Test
    public void getRequestWrongRequestExceptionTest() {
        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
        Mockito.when(itemRequestRepository.findById(999L))
                .thenThrow(new NotFoundException("Request with id 999 not exists in the DB"));
        var exception = assertThrows(
                NotFoundException.class,
                () -> requestService.getRequestById(1, 999L));

        assertEquals("Request with id 999 not exists in the DB", exception.getMessage());
    }

    @Test
    public void getRequestSuccessTest() {
        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
        Mockito.when(itemRequestRepository.findById(1L))
                .thenReturn(Optional.ofNullable(itemRequest));
        Mockito.when(itemRepository.findByItemRequest_IdIn(List.of(1L)))
                .thenReturn(List.of(item));
        var result = requestService.getRequestById(1L, 1);

        assertEquals(result.getId(), ItemRequestMapper.toItemRequestDto(itemRequest).getId());
    }
}