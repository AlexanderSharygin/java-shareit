package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.model.BadRequestException;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class BookServiceTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ItemRepository itemRepository;

    private BookingService bookService;

    private User user;
    private Item item;
    private Item item2;

    @BeforeEach
    public void setup() {
        user = new User(1L, "Name", "email@email.com");
        item = new Item(1L, "Test", "Test",
                false, new User(2L, "Test", "email@email.com"), null);
        item2 = new Item(1L, "Test", "Test", false, user, null);
        bookService = new BookingService(bookingRepository, userRepository, itemRepository);
    }

    @Test
    public void createBookingWithWrongUserIdTest() {
        Mockito.when(userRepository.findById(999L))
                .thenThrow(new NotFoundException("User with id 999 not exists in the DB"));
        var exception = assertThrows(
                NotFoundException.class,
                () -> bookService.create(999, null));

        assertEquals("User with id 999 not exists in the DB", exception.getMessage());
    }

    @Test
    public void createBookingWithWrongItemIdTest() {
        BookingDto bookingDto = new BookingDto(1L, LocalDateTime.now(), LocalDateTime.now().minusDays(1),
                BookingStatus.WAITING, 1L,
                new User(1L, "Name", "email@email.com"), new Item());
        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
        Mockito.when(itemRepository.findById(1L))
                .thenThrow(new NotFoundException("Item with id 1 not exists in the DB"));
        var exception = assertThrows(
                NotFoundException.class,
                () -> bookService.create(1L, bookingDto));

        assertEquals("Item with id 1 not exists in the DB", exception.getMessage());
    }

    @Test
    public void createBookingForUnavailableItemTest() {
        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
        Mockito.when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));
        BookingDto bookingDto = new BookingDto(1L, LocalDateTime.now(), LocalDateTime.now(),
                BookingStatus.WAITING, 1L,
                new User(1L, "Name", "email@email.com"), new Item());
        var exception = assertThrows(
                BadRequestException.class,
                () -> bookService.create(1, bookingDto));

        assertEquals("Can't create the booking for the unavailable item",
                exception.getParameter());
    }

    @Test
    public void createBookingWithPastEndDateTest() {
        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
        Mockito.when(itemRepository.findById(1L))
                .thenReturn(Optional.of(new Item(1L, "Test", "Test", true, null,
                        null)));
        BookingDto bookingDto = new BookingDto(1L, LocalDateTime.now(), LocalDateTime.now().minusDays(1),
                BookingStatus.WAITING, 1L,
                new User(1L, "Name", "email@email.com"), new Item());
        var exception = assertThrows(
                BadRequestException.class,
                () -> bookService.create(1, bookingDto));
        assertEquals("Can't create the booking with end date in the past",
                exception.getParameter());
    }

    @Test
    public void createBookingWithPastStartDateTest() {
        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
        Mockito.when(itemRepository.findById(1L))
                .thenReturn(Optional.of(new Item(1L, "Test", "Test", true, null,
                        null)));
        BookingDto bookingDto = new BookingDto(1L, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1),
                BookingStatus.WAITING, 1L,
                new User(1L, "Name", "email@email.com"), new Item());
        var exception = assertThrows(
                BadRequestException.class,
                () -> bookService.create(1, bookingDto));

        assertEquals("Can't create the booking with start date in the past",
                exception.getParameter());
    }

    @Test
    public void createBookingWithEndSDateWhichIsBeforeStartDateTest() {
        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
        Mockito.when(itemRepository.findById(1L))
                .thenReturn(Optional.of(new Item(1L, "Test", "Test", true, null,
                        null)));
        BookingDto bookingDto = new BookingDto(1L, LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(1),
                BookingStatus.WAITING, 1L,
                new User(1L, "Name", "email@email.com"), new Item());
        var exception = assertThrows(
                BadRequestException.class,
                () -> bookService.create(1, bookingDto));

        assertEquals("Can't create the booking with end date which is before start date",
                exception.getParameter());
    }

    @Test
    public void createBookingWhenUserIdEqualsItemOwnerIdTest() {
        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
        Mockito.when(itemRepository.findById(1L))
                .thenReturn(Optional.of(new Item(1L, "Test", "Test",
                        true, user, null)));
        BookingDto bookingDto = new BookingDto(1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2),
                BookingStatus.WAITING, 1L, new User(1L, "Name", "email@email.com"), new Item());
        var exception = assertThrows(
                NotFoundException.class,
                () -> bookService.create(1, bookingDto));

        assertEquals("Can't create the booking. User can't book own items", exception.getMessage());
    }

    @Test
    public void createBookingSuccessTest() {
        Item item = new Item(1L, "Test", "Test",
                true, new User(2L, "Test", "email@email.com"), null);
        BookingDto bookingDto = new BookingDto(1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2),
                BookingStatus.WAITING, 1L, user, new Item());
        Booking booking = BookingMapper.fromBookingDto(bookingDto);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);
        booking.setBooker(user);
        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
        Mockito.when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));
        Mockito.when(bookingRepository.save(Mockito.any()))
                .thenReturn(booking);
        BookingDto result = bookService.create(1L, bookingDto);

        assertEquals(-1L, result.getId());
    }

    @Test
    public void getBookingByIdWrongBookingIdExceptionTest() {
        Mockito.when(bookingRepository.findById(999L))
                .thenThrow(new NotFoundException("Booking with id 999 not exists in the DB"));
        var exception = assertThrows(
                NotFoundException.class,
                () -> bookService.getById(999L, 1));

        assertEquals("Booking with id 999 not exists in the DB", exception.getMessage());
    }

    @Test
    public void getBookingByIdUserIdIsNotBookerOrOwnerIdExceptionTest() {
        BookingDto bookingDto = new BookingDto(1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2),
                BookingStatus.WAITING, 1L,
                new User(1L, "Name", "email@email.com"), new Item());
        Booking booking = BookingMapper.fromBookingDto(bookingDto);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);
        booking.setBooker(user);

        Mockito.when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));
        var exception = assertThrows(
                NotFoundException.class,
                () -> bookService.getById(1, 5));

        assertEquals("User with id 5 is not booker/owner for booking with id 1", exception.getMessage());
    }

    @Test
    public void getBookingByIdSuccessTest() {
        BookingDto bookingDto = new BookingDto(1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2),
                BookingStatus.WAITING, 1L, user, new Item());
        Booking booking = BookingMapper.fromBookingDto(bookingDto);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);
        booking.setBooker(user);
        Mockito.when(bookingRepository.findById(Mockito.any()))
                .thenReturn(Optional.of(booking));
        BookingDto result = bookService.getById(1, 1);

        assertEquals(-1L, result.getId());
    }

    @Test
    public void changingBookingStatusWrongBookingIdException() {
        Mockito.when(bookingRepository.findById(Mockito.any()))
                .thenThrow(new NotFoundException("Booking with id 999 not exists in the DB"));
        var exception = assertThrows(
                NotFoundException.class,
                () -> bookService.changeBookingStatus(999, 1, true));

        assertEquals("Booking with id 999 not exists in the DB", exception.getMessage());
    }

    @Test
    public void changingBookingStatusWrongUserIdExceptionTest() {
        BookingDto bookingDto = new BookingDto(1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2),
                BookingStatus.WAITING, 1L, user, item);
        Booking booking = BookingMapper.fromBookingDto(bookingDto);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);
        booking.setBooker(user);
        Mockito.when(bookingRepository.findById(Mockito.any()))
                .thenReturn(Optional.of(booking));
        var exception = assertThrows(
                NotFoundException.class,
                () -> bookService.changeBookingStatus(1, 1, true));

        assertEquals("User with id 1 is not owner for item from booking with id 1", exception.getMessage());
    }

    @Test
    public void changingBookingStatusForAlreadyApprovedBookingExceptionTest() {
        BookingDto bookingDto = new BookingDto(1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2),
                BookingStatus.WAITING, 1L, user, item);
        Booking booking = BookingMapper.fromBookingDto(bookingDto);
        booking.setItem(item);
        booking.setStatus(BookingStatus.APPROVED);
        booking.setBooker(user);
        Mockito.when(bookingRepository.findById(Mockito.any()))
                .thenReturn(Optional.of(booking));
        var exception = assertThrows(
                BadRequestException.class,
                () -> bookService.changeBookingStatus(1, 2, true));

        assertEquals("Status is already updated for booking with id 1", exception.getParameter());
    }

    @Test
    public void changingBookingStatusForAlreadyRejectedBookingExceptionTest() {
        BookingDto bookingDto = new BookingDto(1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2),
                BookingStatus.WAITING, 1L, user, item);
        Booking booking = BookingMapper.fromBookingDto(bookingDto);
        booking.setItem(item);
        booking.setStatus(BookingStatus.REJECTED);
        booking.setBooker(user);
        Mockito.when(bookingRepository.findById(Mockito.any()))
                .thenReturn(Optional.of(booking));
        var exception = assertThrows(
                BadRequestException.class,
                () -> bookService.changeBookingStatus(1, 2, false));

        assertEquals("Status is already updated for booking with id 1", exception.getParameter());
    }

    @Test
    public void changingBookingStatusSuccessTest() {
        BookingDto bookingDto = new BookingDto(1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2),
                BookingStatus.WAITING, 1L, user, item);
        Booking booking = BookingMapper.fromBookingDto(bookingDto);
        booking.setItem(item);
        booking.setStatus(BookingStatus.REJECTED);
        booking.setBooker(user);
        Mockito.when(bookingRepository.findById(Mockito.any()))
                .thenReturn(Optional.of(booking));
        BookingDto result = bookService.changeBookingStatus(1, 2, true);

        assertEquals(BookingStatus.APPROVED, result.getStatus());
    }

    @Test
    public void getBookingForUserWrongUserException() {
        Pageable paging = PageRequest.of(0, 100);
        Mockito.when(userRepository.findById(999L))
                .thenThrow(new NotFoundException("User with id 999 not exists in the DB"));
        var exception = assertThrows(
                NotFoundException.class,
                () -> bookService.getBookingsForUser("", 999L, paging));

        assertEquals("User with id 999 not exists in the DB", exception.getMessage());
    }

    @Test
    public void getBookingForUserWrongStatusException() {
        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.of(new User(1L, "Name", "email@email.com")));
        Pageable paging = PageRequest.of(0, 100);
        var exception = assertThrows(
                BadRequestException.class,
                () -> bookService.getBookingsForUser("Wrong", 1L, paging));

        assertEquals("Unknown state: Wrong", exception.getParameter());
    }

    @Test
    public void getWaitingBookingsForUserSuccess() {
        BookingDto bookingDto = new BookingDto(1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2),
                BookingStatus.WAITING, 1L, user, item);
        Booking booking = BookingMapper.fromBookingDto(bookingDto);
        booking.setItem(item);
        booking.setBooker(user);

        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
        Mockito.when(bookingRepository.findByBooker_IdAndStatusOrderByStartDateTimeDesc(1L, BookingStatus.WAITING,
                        PageRequest.of(1, 1)))
                .thenReturn(List.of(booking));
        List<BookingDto> expectedResult = List.of(BookingMapper.toBookingDto(booking));
        Pageable paging = PageRequest.of(1, 1);

        assertEquals(expectedResult, bookService.getBookingsForUser("WAITING", 1L, paging));
    }


    @Test
    public void getRejectedBookingsForUserSuccess() {
        BookingDto bookingDto = new BookingDto(1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2),
                BookingStatus.REJECTED, 1L, user, item);
        Booking booking = BookingMapper.fromBookingDto(bookingDto);
        booking.setItem(item);
        booking.setBooker(user);

        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
        Mockito.when(bookingRepository.findByBooker_IdAndStatusOrderByStartDateTimeDesc(1L, BookingStatus.REJECTED,
                        PageRequest.of(1, 1)))
                .thenReturn(List.of(booking));

        List<BookingDto> expectedResult = List.of(BookingMapper.toBookingDto(booking));
        Pageable paging = PageRequest.of(1, 1);

        assertEquals(expectedResult, bookService.getBookingsForUser("REJECTED", 1L, paging));
    }

    @Test
    public void getFutureBookingsForUserSuccess() {
        BookingDto bookingDto = new BookingDto(1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2),
                BookingStatus.REJECTED, 1L, user, item);
        Booking booking = BookingMapper.fromBookingDto(bookingDto);
        booking.setItem(item);
        booking.setBooker(user);

        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
        Mockito.when(bookingRepository.findFutureBookingsByBookerId(Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(List.of(booking));

        List<BookingDto> expectedResult = List.of(BookingMapper.toBookingDto(booking));
        Pageable paging = PageRequest.of(0, 100);

        assertEquals(expectedResult, bookService.getBookingsForUser("FUTURE", 1L, paging));
    }

    @Test
    public void getPastBookingsForUserSuccess() {
        BookingDto bookingDto = new BookingDto(1L, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1),
                BookingStatus.REJECTED, 1L, user, item);
        Booking booking = BookingMapper.fromBookingDto(bookingDto);
        booking.setItem(item);
        booking.setBooker(user);

        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
        Mockito.when(bookingRepository.findPastBookingsByBookerId(Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(List.of(booking));

        List<BookingDto> expectedResult = List.of(BookingMapper.toBookingDto(booking));
        Pageable paging = PageRequest.of(0, 100);
        assertEquals(expectedResult, bookService.getBookingsForUser("PAST", 1L, paging));
    }

    @Test
    public void findCurrentBookingsByBookerId() {
        BookingDto bookingDto = new BookingDto(1L, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1),
                BookingStatus.WAITING, 1L, user, item);
        Booking booking = BookingMapper.fromBookingDto(bookingDto);
        booking.setItem(item);
        booking.setBooker(user);

        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
        Mockito.when(bookingRepository.findCurrentBookingsByBookerId(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(List.of(booking));

        List<BookingDto> expectedResult = List.of(BookingMapper.toBookingDto(booking));
        Pageable paging = PageRequest.of(0, 100);
        assertEquals(expectedResult, bookService.getBookingsForUser("CURRENT", 1L, paging));
    }

    @Test
    public void findAllBookingsByBookerId() {
        BookingDto bookingDto = new BookingDto(1L, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1),
                BookingStatus.WAITING, 1L, user, item);
        Booking booking = BookingMapper.fromBookingDto(bookingDto);
        booking.setItem(item);
        booking.setBooker(user);

        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
        Mockito.when(bookingRepository.findByBooker_IdOrderByStartDateTimeDesc(Mockito.any(), Mockito.any()))
                .thenReturn(List.of(booking));

        List<BookingDto> expectedResult = List.of(BookingMapper.toBookingDto(booking));
        Pageable paging = PageRequest.of(0, 100);
        assertEquals(expectedResult, bookService.getBookingsForUser("ALL", 1L, paging));
    }

    @Test
    public void getBookingForUserItemsWrongUserException() {
        Pageable paging = PageRequest.of(0, 100);
        Mockito.when(userRepository.findById(999L))
                .thenThrow(new NotFoundException("User with id 999 not exists in the DB"));
        final NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> bookService.getBookingsForUserItems("", 999L, paging));
        assertEquals("User with id 999 not exists in the DB", exception.getMessage());
    }

    @Test
    public void getBookingForUserItemsWrongStatusException() {
        Pageable paging = PageRequest.of(0, 100);
        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
        Mockito.when(itemRepository.findByOwner_Id(1L))
                .thenReturn(List.of(item2));
        var exception = assertThrows(
                BadRequestException.class,
                () -> bookService.getBookingsForUserItems("Wrong", 1L, paging));

        assertEquals("Unknown state: Wrong", exception.getParameter());
    }

    @Test
    public void getWaitingBookingsForUserItemsSuccess() {
        BookingDto bookingDto = new BookingDto(1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2),
                BookingStatus.WAITING, 1L, user, item);
        Booking booking = BookingMapper.fromBookingDto(bookingDto);
        booking.setItem(item2);
        booking.setBooker(user);

        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
        Mockito.when(itemRepository.findByOwner_Id(1L))
                .thenReturn(List.of(item2));
        Mockito.when(bookingRepository.findDistinctByItem_IdInAndStatus(List.of(1L), BookingStatus.WAITING,
                        PageRequest.of(1, 1)))
                .thenReturn(List.of(booking));
        Pageable paging = PageRequest.of(1, 1);

        List<BookingDto> expectedResult = List.of(BookingMapper.toBookingDto(booking));
        assertEquals(expectedResult, bookService.getBookingsForUserItems("WAITING", 1L, paging));
    }

    @Test
    public void getFutureBookingsForUserItemsSuccess() {
        BookingDto bookingDto = new BookingDto(1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2),
                BookingStatus.WAITING, 1L, user, item2);
        Booking booking = BookingMapper.fromBookingDto(bookingDto);
        booking.setItem(item2);
        booking.setBooker(user);

        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
        Mockito.when(itemRepository.findByOwner_Id(1L))
                .thenReturn(List.of(item2));
        Mockito.when(bookingRepository.findFutureBookingsDistinctByItemsIdList(Mockito.any(), Mockito.any(),
                        Mockito.any()))
                .thenReturn(List.of(booking));

        List<BookingDto> expectedResult = List.of(BookingMapper.toBookingDto(booking));
        Pageable paging = PageRequest.of(0, 100);

        assertEquals(expectedResult, bookService.getBookingsForUserItems("FUTURE", 1L, paging));
    }


    @Test
    public void getPastBookingsForUserItemsSuccess() {
        BookingDto bookingDto = new BookingDto(1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2),
                BookingStatus.WAITING, 1L, user, item2);
        Booking booking = BookingMapper.fromBookingDto(bookingDto);
        booking.setItem(item2);
        booking.setBooker(user);

        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
        Mockito.when(itemRepository.findByOwner_Id(1L))
                .thenReturn(List.of(item2));
        Mockito.when(bookingRepository.findPastBookingsByItemsIdList(Mockito.any(), Mockito.any(),
                        Mockito.any()))
                .thenReturn(List.of(booking));
        List<BookingDto> expectedResult = List.of(BookingMapper.toBookingDto(booking));
        Pageable paging = PageRequest.of(0, 100);

        assertEquals(expectedResult, bookService.getBookingsForUserItems("PAST", 1L, paging));
    }

    @Test
    public void getCurrentBookingsForUserItemsSuccess() {
        BookingDto bookingDto = new BookingDto(1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2),
                BookingStatus.WAITING, 1L, user, item2);
        Booking booking = BookingMapper.fromBookingDto(bookingDto);
        booking.setItem(item2);
        booking.setBooker(user);

        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
        Mockito.when(itemRepository.findByOwner_Id(1L))
                .thenReturn(List.of(item2));
        Mockito.when(bookingRepository.findCurrentBookingsByItemIdList(Mockito.any(), Mockito.any(), Mockito.any(),
                        Mockito.any()))
                .thenReturn(List.of(booking));
        List<BookingDto> expectedResult = List.of(BookingMapper.toBookingDto(booking));
        Pageable paging = PageRequest.of(0, 100);

        assertEquals(expectedResult, bookService.getBookingsForUserItems("CURRENT", 1L, paging));
    }

    @Test
    public void getAllBookingsForUserItemsSuccess() {
        BookingDto bookingDto = new BookingDto(1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2),
                BookingStatus.WAITING, 1L, user, item2);
        Booking booking = BookingMapper.fromBookingDto(bookingDto);
        booking.setItem(item2);
        booking.setBooker(user);

        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
        Mockito.when(itemRepository.findByOwner_Id(1L))
                .thenReturn(List.of(item2));
        Mockito.when(bookingRepository.findDistinctByItem_IdInOrderByStartDateTimeDesc(Mockito.any(), Mockito.any()))
                .thenReturn(List.of(booking));
        List<BookingDto> expectedResult = List.of(BookingMapper.toBookingDto(booking));
        Pageable paging = PageRequest.of(0, 100);

        assertEquals(expectedResult, bookService.getBookingsForUserItems("ALL", 1L, paging));
    }
}