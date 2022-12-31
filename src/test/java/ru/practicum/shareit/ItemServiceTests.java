package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.model.BadRequestException;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private ItemRequestRepository itemRequestRepository;


    private ItemService itemService;

    private User user;
    private Item item;
    private Item item2;

    @BeforeEach
    public void setup() {
        user = new User(1L, "Name", "email@email.com");
        item = new Item(1L, "Test", "Test",
                false, new User(1L, "Test", "email@email.com"), null);
        item2 = new Item(1L, "Test", "Test", false, user, null);
        itemService = new ItemService(itemRepository, userRepository, bookingRepository,
                commentRepository, itemRequestRepository);
    }

    @Test
    public void getAllItemsSuccessTest() {
        Pageable paging = PageRequest.of(1, 1);
        Page<Item> page = new PageImpl<>(List.of(item));
        Mockito.when(itemRepository.findAll(paging))
                .thenReturn(page);
        List<ItemDto> expectedResult = Stream.of(item).map(ItemMapper::toItemDto).collect(Collectors.toList());
        assertEquals(expectedResult, itemService.getAll(1, 1));
    }

    @Test
    public void getAllForUserItemsSuccessTest() {
        Pageable paging = PageRequest.of(1, 1);
        Mockito.when(itemRepository.findByOwner_Id(1L, paging))
                .thenReturn(List.of(item));
        List<ItemDto> expectedResult = Stream.of(item).map(ItemMapper::toItemDto).collect(Collectors.toList());
        assertEquals(expectedResult, itemService.getAllForUser(1, 1, 1));
    }

    @Test
    public void getItemByIdWrongItemIdExceptionTest() {
        Mockito.when(itemRepository.findById(999L))
                .thenThrow(new NotFoundException("Item with id 999 not exists in the DB"));

        var exception = assertThrows(
                NotFoundException.class,
                () -> itemRepository.findById(999L));
        assertEquals("Item with id 999 not exists in the DB", exception.getMessage());
    }

    @Test
    public void getItemByIdSuccessTest() {
        Mockito.when(itemRepository.findById(1L))
                .thenReturn(Optional.ofNullable(item));
        ItemDto result = itemService.getById(1, 1);

        assertEquals(1L, result.getId());
    }

    @Test
    public void getItemByEmptyNameSuccessTest() {
        List<ItemDto> result = itemService.getByNameOrDescription("", 1, 1);
        assertTrue(result.isEmpty());
    }

    @Test
    public void getItemByNameSuccessTest() {
        Mockito.when(itemRepository.findAvailableItemsByNameOrDescription(Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(List.of(item));
        List<ItemDto> result = itemService.getByNameOrDescription("Test", 1, 1);

        assertEquals(1L, result.get(0).getId());
    }

    @Test
    public void createItemWithWrongUserIdExceptionTest() {
        Mockito.when(userRepository.findById(999L))
                .thenThrow(new NotFoundException("User with id 999 not exists in the DB"));
        var exception = assertThrows(
                NotFoundException.class,
                () -> itemService.create(999, ItemMapper.toItemDto(item)));

        assertEquals("User with id 999 not exists in the DB", exception.getMessage());
    }

    @Test
    public void createItemWithEmptyNameExceptionTest() {
        item.setName("");
        var exception = assertThrows(
                BadRequestException.class,
                () -> itemService.create(999, ItemMapper.toItemDto(item)));

        assertEquals("Name can't be empty or null", exception.getParameter());
    }

    @Test
    public void createItemWithEmptyDescriptionExceptionTest() {
        item.setDescription(null);
        var exception = assertThrows(
                BadRequestException.class,
                () -> itemService.create(999, ItemMapper.toItemDto(item)));

        assertEquals("Description can't be null", exception.getParameter());
    }

    @Test
    public void createItemWithEmptyAvailableExceptionTest() {
        item.setAvailable(null);
        var exception = assertThrows(
                BadRequestException.class,
                () -> itemService.create(999, ItemMapper.toItemDto(item)));

        assertEquals("Available flag can't be null", exception.getParameter());
    }

    @Test
    public void createItemWitWrongRequestIdExceptionTest() {
        ItemRequest itemRequest = new ItemRequest(1L, user, "Test", Instant.now());
        item.setItemRequest(itemRequest);
        Mockito.when(itemRequestRepository.findById(1L))
                .thenThrow(new NotFoundException("Item request with id 1 is not found"));
        Mockito.when(userRepository.findById(Mockito.any()))
                .thenReturn(Optional.of(user));
        var exception = assertThrows(
                NotFoundException.class,
                () -> itemService.create(999, ItemMapper.toItemDto(item)));

        assertEquals("Item request with id 1 is not found", exception.getMessage());
    }

    @Test
    public void createItemSuccessTest() {
        Mockito.when(userRepository.findById(Mockito.any()))
                .thenReturn(Optional.of(user));
        Mockito.when(itemRepository.save(Mockito.any()))
                .thenReturn(item);
        ItemDto result = itemService.create(1L, ItemMapper.toItemDto(item));

        assertEquals(result, ItemMapper.toItemDto(item));
    }


    @Test
    public void updateItemWithWrongItemIdExceptionTest() {
        Mockito.when(itemRepository.findById(Mockito.any()))
                .thenThrow(new NotFoundException("Item with id 1 not exists in the DB"));
        var exception = assertThrows(
                NotFoundException.class,
                () -> itemService.update(1L, 999, ItemMapper.toItemDto(item)));

        assertEquals("Item with id 1 not exists in the DB", exception.getMessage());
    }

    @Test
    public void updateItemWithWrongOwnerIdExceptionTest() {
        Mockito.when(itemRepository.findById(Mockito.any()))
                .thenReturn(Optional.ofNullable(item));

        var exception = assertThrows(
                NotFoundException.class,
                () -> itemService.update(1L, 999, ItemMapper.toItemDto(item)));

        assertEquals("Wrong owner is specified for the item", exception.getMessage());
    }

    @Test
    public void updateItemWithWrongUserIdExceptionTest() {
        Mockito.when(itemRepository.findById(Mockito.any()))
                .thenReturn(Optional.ofNullable(item2));

        Mockito.when(userRepository.findById(1L))
                .thenThrow(new NotFoundException("User with id 1 not exists in the DB"));

        var exception = assertThrows(
                NotFoundException.class,
                () -> itemService.update(1L, 1L, ItemMapper.toItemDto(item)));

        assertEquals("User with id 1 not exists in the DB", exception.getMessage());
    }

    @Test
    public void updateItemSuccessTest() {
        Mockito.when(itemRepository.findById(Mockito.any()))
                .thenReturn(Optional.ofNullable(item));

        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.ofNullable(user));
        Mockito.when(itemRepository.save(Mockito.any()))
                .thenReturn(item);
        ItemDto result = itemService.update(1L, 1L, ItemMapper.toItemDto(item));

        assertEquals(result, ItemMapper.toItemDto(item));
    }

    @Test
    public void addCommentForWrongBookingExceptionTest() {
        Booking booking = new Booking(1L, Instant.now().plusSeconds(100),
                Instant.now().plusSeconds(200), BookingStatus.WAITING, item, user);
        Mockito.when(bookingRepository.findPastBookingsByBookerId(Mockito.any(), Mockito.any()))
                .thenReturn(List.of(booking));
        var exception = assertThrows(
                BadRequestException.class,
                () -> itemService.addComment(2L, 1L, new CommentDto(1L, "Test",
                        "Test", LocalDateTime.now())));

        assertEquals("User with id 1 can't left the comment for booking with id 2",
                exception.getParameter());
    }

    @Test
    public void addCommentWrongUserBookingExceptionTest() {
        Booking booking = new Booking(1L, Instant.now().plusSeconds(100),
                Instant.now().plusSeconds(200), BookingStatus.WAITING, item, user);
        Mockito.when(bookingRepository.findPastBookingsByBookerId(Mockito.any(), Mockito.any()))
                .thenReturn(List.of(booking));
        Mockito.when(userRepository.findById(1L))
                .thenThrow(new NotFoundException("User with id 1 not exists in the DB"));

        var exception = assertThrows(
                NotFoundException.class,
                () -> itemService.addComment(1L, 1L, new CommentDto(1L, "Test",
                        "Test", LocalDateTime.now())));

        assertEquals("User with id 1 not exists in the DB",
                exception.getMessage());
    }

    @Test
    public void addCommentWrongItemBookingExceptionTest() {
        Booking booking = new Booking(1L, Instant.now().plusSeconds(100),
                Instant.now().plusSeconds(200), BookingStatus.WAITING, item, user);
        Mockito.when(bookingRepository.findPastBookingsByBookerId(Mockito.any(), Mockito.any()))
                .thenReturn(List.of(booking));
        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.ofNullable(user));
        Mockito.when(itemRepository.findById(1L))
                .thenThrow(new NotFoundException("Item with id 1 not exists in the DB"));

        var exception = assertThrows(
                NotFoundException.class,
                () -> itemService.addComment(1L, 1L, new CommentDto(1L, "Test",
                        "Test", LocalDateTime.now())));
        assertEquals("Item with id 1 not exists in the DB",
                exception.getMessage());
    }

    @Test
    public void addCommentSuccessTest() {
        Comment comment = new Comment(1L, "Text", user, item, LocalDateTime.now());

        Booking booking = new Booking(1L, Instant.now().plusSeconds(100),
                Instant.now().plusSeconds(200), BookingStatus.WAITING, item, user);
        Mockito.when(bookingRepository.findPastBookingsByBookerId(Mockito.any(), Mockito.any()))
                .thenReturn(List.of(booking));
        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.ofNullable(user));
        Mockito.when(itemRepository.findById(1L))
                .thenReturn(Optional.ofNullable(item));
        Mockito.when(commentRepository.save(Mockito.any()))
                .thenReturn(comment);
        var result = itemService.addComment(1L, 1L, CommentMapper.toCommentDto(comment));

        assertEquals(result, CommentMapper.toCommentDto(comment));
    }
}