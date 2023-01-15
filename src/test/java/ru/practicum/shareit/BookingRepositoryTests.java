package ru.practicum.shareit;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.Instant;
import java.util.List;

@DataJpaTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BookingRepositoryTests {

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private BookingRepository bookingRepository;


    @Test
    @Order(1)
    @Transactional
    @Rollback(value = false)
    void findDistinctByItem_IdInAndStatusTest() {
        User user = new User(1L, "Name", "Email@ec.cd");
        userRepository.save(user);
        ItemRequest itemRequest = new ItemRequest(1L, user, "Desc", Instant.now());
        itemRequestRepository.save(itemRequest);
        Item item = new Item(1L, "Name", "Description", true, user, itemRequest);
        item.setOwner(user);
        itemRepository.save(item);

        Booking booking = new Booking(1L, Instant.now().plusSeconds(100),
                Instant.now().plusSeconds(200), BookingStatus.WAITING, item, user);

        bookingRepository.save(booking);
        List<Booking> bookings = bookingRepository.findDistinctByItem_IdInAndStatus(List.of(1L), BookingStatus.WAITING, null);

        Assertions.assertEquals(bookings.get(0).getId(), 1L);
        Assertions.assertEquals(bookings.get(0).getStatus(), BookingStatus.WAITING);
        Assertions.assertEquals(bookings.get(0).getId(), 1L);
        Assertions.assertEquals(bookings.get(0).getBooker().getName(), "Name");
        Assertions.assertEquals(bookings.get(0).getBooker().getEmail(), "Email@ec.cd");
        Assertions.assertEquals(bookings.get(0).getBooker().getId(), 1L);
        Assertions.assertEquals(bookings.get(0).getItem().getId(), 1L);
        Assertions.assertEquals(bookings.get(0).getItem().getDescription(), "Description");
        Assertions.assertEquals(bookings.get(0).getItem().getOwner().getName(), "Name");
        Assertions.assertEquals(bookings.get(0).getItem().getOwner().getEmail(), "Email@ec.cd");
        Assertions.assertEquals(bookings.get(0).getItem().getOwner().getId(), 1L);
    }

    @Test
    @Transactional
    void findFutureBookingsByBookerIdTest() {
        List<Booking> bookings = bookingRepository.findFutureBookingsByBookerId(1L, Instant.now(), null);
        Assertions.assertEquals(bookings.get(0).getId(), 1L);
        Assertions.assertEquals(bookings.get(0).getStatus(), BookingStatus.WAITING);
        Assertions.assertEquals(bookings.get(0).getId(), 1L);
        Assertions.assertEquals(bookings.get(0).getBooker().getName(), "Name");
        Assertions.assertEquals(bookings.get(0).getBooker().getEmail(), "Email@ec.cd");
        Assertions.assertEquals(bookings.get(0).getBooker().getId(), 1L);
        Assertions.assertEquals(bookings.get(0).getItem().getId(), 1L);
        Assertions.assertEquals(bookings.get(0).getItem().getDescription(), "Description");
        Assertions.assertEquals(bookings.get(0).getItem().getOwner().getName(), "Name");
        Assertions.assertEquals(bookings.get(0).getItem().getOwner().getEmail(), "Email@ec.cd");
        Assertions.assertEquals(bookings.get(0).getItem().getOwner().getId(), 1L);
    }

    @Test
    @Transactional
    void findPastBookingsByBookerIdTest() {
        User user = new User(1L, "Name", "Email@ec.cd");
        ItemRequest itemRequest = new ItemRequest(1L, user, "Desc", Instant.now());
        Item item = new Item(1L, "Name", "Description", true, user, itemRequest);
        item.setOwner(user);
        Booking booking = new Booking(1L, Instant.now().minusSeconds(200),
                Instant.now().minusSeconds(100), BookingStatus.WAITING, item, user);
        bookingRepository.save(booking);

        List<Booking> bookings = bookingRepository.findPastBookingsByBookerId(1L, Instant.now(), null);
        Assertions.assertEquals(bookings.get(0).getId(), 1L);
        Assertions.assertEquals(bookings.get(0).getStatus(), BookingStatus.WAITING);
        Assertions.assertEquals(bookings.get(0).getId(), 1L);
        Assertions.assertEquals(bookings.get(0).getBooker().getName(), "Name");
        Assertions.assertEquals(bookings.get(0).getBooker().getEmail(), "Email@ec.cd");
        Assertions.assertEquals(bookings.get(0).getBooker().getId(), 1L);
        Assertions.assertEquals(bookings.get(0).getItem().getId(), 1L);
        Assertions.assertEquals(bookings.get(0).getItem().getDescription(), "Description");
        Assertions.assertEquals(bookings.get(0).getItem().getOwner().getName(), "Name");
        Assertions.assertEquals(bookings.get(0).getItem().getOwner().getEmail(), "Email@ec.cd");
        Assertions.assertEquals(bookings.get(0).getItem().getOwner().getId(), 1L);
    }

    @Test
    @Transactional
    void findCurrentBookingsByBookerIdTest() {
        User user = new User(1L, "Name", "Email@ec.cd");
        ItemRequest itemRequest = new ItemRequest(1L, user, "Desc", Instant.now());
        Item item = new Item(1L, "Name", "Description", true, user, itemRequest);
        item.setOwner(user);
        Booking booking = new Booking(1L, Instant.now().minusSeconds(200),
                Instant.now().plusSeconds(100), BookingStatus.WAITING, item, user);
        bookingRepository.save(booking);

        List<Booking> bookings = bookingRepository
                .findCurrentBookingsByBookerId(1L, Instant.now(), Instant.now(), null);
        Assertions.assertEquals(bookings.get(0).getId(), 1L);
        Assertions.assertEquals(bookings.get(0).getStatus(), BookingStatus.WAITING);
        Assertions.assertEquals(bookings.get(0).getId(), 1L);
        Assertions.assertEquals(bookings.get(0).getBooker().getName(), "Name");
        Assertions.assertEquals(bookings.get(0).getBooker().getEmail(), "Email@ec.cd");
        Assertions.assertEquals(bookings.get(0).getBooker().getId(), 1L);
        Assertions.assertEquals(bookings.get(0).getItem().getId(), 1L);
        Assertions.assertEquals(bookings.get(0).getItem().getDescription(), "Description");
        Assertions.assertEquals(bookings.get(0).getItem().getOwner().getName(), "Name");
        Assertions.assertEquals(bookings.get(0).getItem().getOwner().getEmail(), "Email@ec.cd");
        Assertions.assertEquals(bookings.get(0).getItem().getOwner().getId(), 1L);
    }

    @Test
    @Transactional
    void findByBooker_IdAndStatusOrderByStartDateTimeDescTest() {
        User user = new User(1L, "Name", "Email@ec.cd");
        ItemRequest itemRequest = new ItemRequest(1L, user, "Desc", Instant.now());
        Item item = new Item(1L, "Name", "Description", true, user, itemRequest);
        item.setOwner(user);
        Booking booking = new Booking(1L, Instant.now().minusSeconds(200),
                Instant.now().plusSeconds(100), BookingStatus.WAITING, item, user);
        bookingRepository.save(booking);

        List<Booking> bookings = bookingRepository
                .findByBooker_IdAndStatusOrderByStartDateTimeDesc(1L, BookingStatus.WAITING, null);
        Assertions.assertEquals(bookings.get(0).getId(), 1L);
        Assertions.assertEquals(bookings.get(0).getStatus(), BookingStatus.WAITING);
        Assertions.assertEquals(bookings.get(0).getId(), 1L);
        Assertions.assertEquals(bookings.get(0).getBooker().getName(), "Name");
        Assertions.assertEquals(bookings.get(0).getBooker().getEmail(), "Email@ec.cd");
        Assertions.assertEquals(bookings.get(0).getBooker().getId(), 1L);
        Assertions.assertEquals(bookings.get(0).getItem().getId(), 1L);
        Assertions.assertEquals(bookings.get(0).getItem().getDescription(), "Description");
        Assertions.assertEquals(bookings.get(0).getItem().getOwner().getName(), "Name");
        Assertions.assertEquals(bookings.get(0).getItem().getOwner().getEmail(), "Email@ec.cd");
        Assertions.assertEquals(bookings.get(0).getItem().getOwner().getId(), 1L);
    }

    @Test
    @Transactional
    void findByBooker_IdOrderByStartDateTimeDescTest() {
        User user = new User(1L, "Name", "Email@ec.cd");
        ItemRequest itemRequest = new ItemRequest(1L, user, "Desc", Instant.now());
        Item item = new Item(1L, "Name", "Description", true, user, itemRequest);
        item.setOwner(user);
        Booking booking = new Booking(1L, Instant.now().minusSeconds(200),
                Instant.now().plusSeconds(100), BookingStatus.APPROVED, item, user);
        bookingRepository.save(booking);

        List<Booking> bookings = bookingRepository
                .findByBooker_IdOrderByStartDateTimeDesc(1L, null);
        Assertions.assertEquals(bookings.get(0).getId(), 1L);
        Assertions.assertEquals(bookings.get(0).getStatus(), BookingStatus.APPROVED);
        Assertions.assertEquals(bookings.get(0).getId(), 1L);
        Assertions.assertEquals(bookings.get(0).getBooker().getName(), "Name");
        Assertions.assertEquals(bookings.get(0).getBooker().getEmail(), "Email@ec.cd");
        Assertions.assertEquals(bookings.get(0).getBooker().getId(), 1L);
        Assertions.assertEquals(bookings.get(0).getItem().getId(), 1L);
        Assertions.assertEquals(bookings.get(0).getItem().getDescription(), "Description");
        Assertions.assertEquals(bookings.get(0).getItem().getOwner().getName(), "Name");
        Assertions.assertEquals(bookings.get(0).getItem().getOwner().getEmail(), "Email@ec.cd");
        Assertions.assertEquals(bookings.get(0).getItem().getOwner().getId(), 1L);
    }

    @Test
    @Transactional
    void findFutureBookingsDistinctByItemsIdListTest() {
        User user = new User(1L, "Name", "Email@ec.cd");
        ItemRequest itemRequest = new ItemRequest(1L, user, "Desc", Instant.now());
        Item item = new Item(1L, "Name", "Description", true, user, itemRequest);
        item.setOwner(user);
        Booking booking = new Booking(1L, Instant.now().plusSeconds(200),
                Instant.now().plusSeconds(300), BookingStatus.APPROVED, item, user);
        bookingRepository.save(booking);

        List<Booking> bookings = bookingRepository
                .findFutureBookingsDistinctByItemsIdList(List.of(1L), Instant.now(), null);
        Assertions.assertEquals(bookings.get(0).getId(), 1L);
        Assertions.assertEquals(bookings.get(0).getStatus(), BookingStatus.APPROVED);
        Assertions.assertEquals(bookings.get(0).getId(), 1L);
        Assertions.assertEquals(bookings.get(0).getBooker().getName(), "Name");
        Assertions.assertEquals(bookings.get(0).getBooker().getEmail(), "Email@ec.cd");
        Assertions.assertEquals(bookings.get(0).getBooker().getId(), 1L);
        Assertions.assertEquals(bookings.get(0).getItem().getId(), 1L);
        Assertions.assertEquals(bookings.get(0).getItem().getDescription(), "Description");
        Assertions.assertEquals(bookings.get(0).getItem().getOwner().getName(), "Name");
        Assertions.assertEquals(bookings.get(0).getItem().getOwner().getEmail(), "Email@ec.cd");
        Assertions.assertEquals(bookings.get(0).getItem().getOwner().getId(), 1L);
    }

    @Test
    @Transactional
    void findPastBookingsByItemsIdListTest() {
        User user = new User(1L, "Name", "Email@ec.cd");
        ItemRequest itemRequest = new ItemRequest(1L, user, "Desc", Instant.now());
        Item item = new Item(1L, "Name", "Description", true, user, itemRequest);
        item.setOwner(user);
        Booking booking = new Booking(1L, Instant.now().minusSeconds(200),
                Instant.now().minusSeconds(100), BookingStatus.APPROVED, item, user);
        bookingRepository.save(booking);

        List<Booking> bookings = bookingRepository
                .findPastBookingsByItemsIdList(List.of(1L), Instant.now(), null);
        Assertions.assertEquals(bookings.get(0).getId(), 1L);
        Assertions.assertEquals(bookings.get(0).getStatus(), BookingStatus.APPROVED);
        Assertions.assertEquals(bookings.get(0).getId(), 1L);
        Assertions.assertEquals(bookings.get(0).getBooker().getName(), "Name");
        Assertions.assertEquals(bookings.get(0).getBooker().getEmail(), "Email@ec.cd");
        Assertions.assertEquals(bookings.get(0).getBooker().getId(), 1L);
        Assertions.assertEquals(bookings.get(0).getItem().getId(), 1L);
        Assertions.assertEquals(bookings.get(0).getItem().getDescription(), "Description");
        Assertions.assertEquals(bookings.get(0).getItem().getOwner().getName(), "Name");
        Assertions.assertEquals(bookings.get(0).getItem().getOwner().getEmail(), "Email@ec.cd");
        Assertions.assertEquals(bookings.get(0).getItem().getOwner().getId(), 1L);
    }

    @Test
    @Transactional
    void findCurrentBookingsByItemIdListTest() {
        User user = new User(1L, "Name", "Email@ec.cd");
        ItemRequest itemRequest = new ItemRequest(1L, user, "Desc", Instant.now());
        Item item = new Item(1L, "Name", "Description", true, user, itemRequest);
        item.setOwner(user);
        Booking booking = new Booking(1L, Instant.now().minusSeconds(200),
                Instant.now().plusSeconds(100), BookingStatus.APPROVED, item, user);
        bookingRepository.save(booking);

        List<Booking> bookings = bookingRepository
                .findCurrentBookingsByItemIdList(List.of(1L), Instant.now(), Instant.now(), null);
        Assertions.assertEquals(bookings.get(0).getId(), 1L);
        Assertions.assertEquals(bookings.get(0).getStatus(), BookingStatus.APPROVED);
        Assertions.assertEquals(bookings.get(0).getId(), 1L);
        Assertions.assertEquals(bookings.get(0).getBooker().getName(), "Name");
        Assertions.assertEquals(bookings.get(0).getBooker().getEmail(), "Email@ec.cd");
        Assertions.assertEquals(bookings.get(0).getBooker().getId(), 1L);
        Assertions.assertEquals(bookings.get(0).getItem().getId(), 1L);
        Assertions.assertEquals(bookings.get(0).getItem().getDescription(), "Description");
        Assertions.assertEquals(bookings.get(0).getItem().getOwner().getName(), "Name");
        Assertions.assertEquals(bookings.get(0).getItem().getOwner().getEmail(), "Email@ec.cd");
        Assertions.assertEquals(bookings.get(0).getItem().getOwner().getId(), 1L);
    }
}

