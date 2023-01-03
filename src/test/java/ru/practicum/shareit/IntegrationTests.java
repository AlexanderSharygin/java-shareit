package ru.practicum.shareit;

import lombok.RequiredArgsConstructor;
import org.apache.maven.surefire.shared.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class IntegrationTests {
    private final EntityManager em;
    private final UserService userService;
    private final ItemService itemService;

    private final BookingService bookingService;

    private final RequestService requestService;

    @Test
    void getUserByIdTest() {
        UserDto userDto = new UserDto(-1L, "some1@email.com", "name1");
        userService.create(userDto);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User user = query
                .setParameter("email", userDto.getEmail())
                .getSingleResult();
        long id = user.getId();
        UserDto userDtoGet = userService.getById(id);

        assertNotNull(userDtoGet.getId());
        assertEquals(userDtoGet.getName(), userDto.getName());
        assertEquals(userDtoGet.getEmail(), userDto.getEmail());
    }

    @Test
    void getAllUsersTest() {
        UserDto userDto = new UserDto(-1L, "some1@email.com", "name1");
        UserDto userDto2 = new UserDto(-1L, "some2@email.com", "name2");
        userService.create(userDto);
        userService.create(userDto2);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User user1 = query
                .setParameter("email", userDto.getEmail())
                .getSingleResult();
        long id1 = user1.getId();
        User user2 = query
                .setParameter("email", userDto2.getEmail())
                .getSingleResult();
        long id2 = user2.getId();
        List<UserDto> usersList = userService.getAll();
        UserDto userDtoOne = usersList.stream().filter(k -> k.getId() == id1).findFirst().orElse(null);
        UserDto userDtoTwo = usersList.stream().filter(k -> k.getId() == id2).findFirst().orElse(null);

        assertNotNull(userDtoOne);
        assertNotNull(userDtoTwo);
        assertEquals(userDtoOne.getName(), userDto.getName());
        assertEquals(userDtoOne.getEmail(), userDto.getEmail());
        assertEquals(userDtoTwo.getName(), userDto2.getName());
        assertEquals(userDtoTwo.getEmail(), userDto2.getEmail());
    }

    @Test
    void createUserTest() {
        UserDto userDto = new UserDto(-1L, "some@email.com", "name");
        userService.create(userDto);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User user = query
                .setParameter("email", userDto.getEmail())
                .getSingleResult();
        assertNotNull(user.getId());
        assertEquals(user.getName(), userDto.getName());
        assertEquals(user.getEmail(), userDto.getEmail());
    }

    @Test
    void updateUserTest() {
        UserDto userDto = new UserDto(-1L, "some1@email.com", "name1");
        userService.create(userDto);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User user = query
                .setParameter("email", userDto.getEmail())
                .getSingleResult();
        long id = user.getId();
        UserDto userDtoUpd = new UserDto(id, "upd@email.com", "upd");
        userService.update(id, userDtoUpd);

        TypedQuery<User> queryUpd = em.createQuery("Select u from User u where u.id = :id", User.class);
        user = queryUpd
                .setParameter("id", id)
                .getSingleResult();
        assertNotNull(user.getId());
        assertEquals(user.getName(), userDtoUpd.getName());
        assertEquals(user.getEmail(), userDtoUpd.getEmail());
    }

    @Test
    void deleteUserTest() {
        UserDto userDto = new UserDto(-1L, "some1@email.com", "name1");
        userService.create(userDto);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User user = query
                .setParameter("email", userDto.getEmail())
                .getSingleResult();
        long id = user.getId();
        userService.delete(id);

        TypedQuery<User> queryUpd = em.createQuery("Select u from User u where u.id = :id", User.class);
        assertThrows(NoResultException.class,
                () -> {
                    queryUpd
                            .setParameter("id", id)
                            .getSingleResult();
                });
    }

    @Test
    void getItemByIdTest() {
        UserDto userDto = new UserDto(-1L, "some1@email.com", "name1");
        userService.create(userDto);
        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User user = query
                .setParameter("email", userDto.getEmail())
                .getSingleResult();
        long id = user.getId();
        ItemDto itemDto = new ItemDto(1L, "name", "desc", true, user, null, null, null, new ArrayList<>());
        itemService.create(id, itemDto);
        TypedQuery<Item> itemQuery = em.createQuery("Select i from Item i where i.name = :name", Item.class);
        Item item = itemQuery
                .setParameter("name", itemDto.getName())
                .getSingleResult();
        ItemDto itemDto1 = itemService.getById(item.getId(), user.getId());

        assertNotNull(itemDto1.getId());
        assertEquals(itemDto1.getName(), "name");
        assertEquals(itemDto1.getDescription(), "desc");
        assertEquals(itemDto1.getAvailable(), true);
        assertEquals(itemDto1.getOwner(), user);
    }

    @Test
    void getAllItemsTest() {
        UserDto userDto = new UserDto(-1L, "some1@email.com", "name1");
        userService.create(userDto);
        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User user = query
                .setParameter("email", userDto.getEmail())
                .getSingleResult();
        long id = user.getId();

        ItemDto itemDto = new ItemDto(1L, "name", "desc", true, user, null, null, null, new ArrayList<>());
        itemService.create(id, itemDto);

        ItemDto itemDto2 = new ItemDto(2L, "name2", "desc2", true, user, null, null, null, new ArrayList<>());
        itemService.create(id, itemDto2);
        List<ItemDto> items = itemService.getAll(0, 100);

        assertTrue(items.size() > 0);
        assertNotNull(items.stream().filter(k -> k.getName().equals("name")).findFirst().orElse(null));
        assertNotNull(items.stream().filter(k -> k.getName().equals("name2")).findFirst().orElse(null));
    }

    @Test
    void getAllForUserItemsTest() {
        UserDto userDto = new UserDto(-1L, "some1@email.com", "name1");
        userService.create(userDto);
        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User user = query
                .setParameter("email", userDto.getEmail())
                .getSingleResult();
        long id = user.getId();

        UserDto userDto2 = new UserDto(-1L, "some2@email.com", "name2");
        userService.create(userDto2);
        User user2 = query
                .setParameter("email", userDto2.getEmail())
                .getSingleResult();

        ItemDto itemDto = new ItemDto(1L, "name", "desc", true, user, null, null, null, new ArrayList<>());
        itemService.create(id, itemDto);
        ItemDto itemDto2 = new ItemDto(2L, "name2", "desc2", true, user2, null, null, null, new ArrayList<>());
        itemService.create(id, itemDto2);
        List<ItemDto> items = itemService.getAllForUser(id, 0, 100);

        assertTrue(items.size() > 0);
        for (var item : items) {
            assertEquals(item.getOwner().getId(), id);
        }
    }

    @Test
    void getItemsByNameTest() {
        UserDto userDto = new UserDto(-1L, "some1@email.com", "name1");
        userService.create(userDto);
        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User user = query
                .setParameter("email", userDto.getEmail())
                .getSingleResult();
        long id = user.getId();

        ItemDto itemDto = new ItemDto(1L, "name", "desc", true, user, null, null, null, new ArrayList<>());
        itemService.create(id, itemDto);
        ItemDto itemDto2 = new ItemDto(2L, "name2", "desc2", true, user, null, null, null, new ArrayList<>());
        itemService.create(id, itemDto2);
        List<ItemDto> items = itemService.getByNameOrDescription("name", 0, 100);

        assertTrue(items.size() > 0);
        for (var item : items) {
            assertTrue(item.getName().contains("name"));
        }
    }

    @Test
    void createItemTest() {
        UserDto userDto = new UserDto(-1L, "some1@email.com", "name1");
        userService.create(userDto);
        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User user = query
                .setParameter("email", userDto.getEmail())
                .getSingleResult();
        long id = user.getId();

        String name = RandomStringUtils.randomAlphabetic(10);
        ItemDto itemDto = new ItemDto(1L, name, "desc", true, user, null, null, null, new ArrayList<>());
        itemService.create(id, itemDto);

        TypedQuery<Item> queryItem = em.createQuery("Select i from Item i where i.name = :name", Item.class);
        Item item = queryItem
                .setParameter("name", name)
                .getSingleResult();
        assertNotNull(item.getId());
        assertEquals(item.getName(), name);
        assertEquals(item.getDescription(), "desc");
        assertEquals(item.getAvailable(), true);
        assertEquals(item.getOwner(), user);
    }

    @Test
    void updateItemTest() {
        UserDto userDto = new UserDto(-1L, "some1@email.com", "name1");
        userService.create(userDto);
        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User user = query
                .setParameter("email", userDto.getEmail())
                .getSingleResult();
        long id = user.getId();

        String name = RandomStringUtils.randomAlphabetic(10);
        ItemDto itemDto = new ItemDto(1L, name, "desc", true, user, null, null, null, new ArrayList<>());
        itemService.create(id, itemDto);

        TypedQuery<Item> queryItem = em.createQuery("Select i from Item i where i.name = :name", Item.class);
        Item item = queryItem
                .setParameter("name", name)
                .getSingleResult();
        long itemId = item.getId();
        String newName = RandomStringUtils.randomAlphabetic(10);
        itemDto.setName(newName);
        itemService.update(itemId, id, itemDto);

        assertNotNull(item.getId());
        assertEquals(item.getName(), newName);
        assertEquals(item.getDescription(), "desc");
        assertEquals(item.getAvailable(), true);
        assertEquals(item.getOwner(), user);
    }

    @Test
    void createBookingTest() {
        UserDto userDto = new UserDto(-1L, "some1@email.com", "name1");
        userService.create(userDto);
        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User user = query
                .setParameter("email", userDto.getEmail())
                .getSingleResult();
        long userId = user.getId();

        UserDto userDto2 = new UserDto(-1L, "some1@emai21.com", "name2");
        userService.create(userDto2);
        TypedQuery<User> query2 = em.createQuery("Select u from User u where u.email = :email", User.class);
        User user2 = query2
                .setParameter("email", userDto2.getEmail())
                .getSingleResult();
        long userId2 = user2.getId();

        String name = RandomStringUtils.randomAlphabetic(10);
        ItemDto itemDto = new ItemDto(1L, name, "desc", true, user2, null, null, null, new ArrayList<>());
        itemService.create(userId2, itemDto);

        TypedQuery<Item> queryItem = em.createQuery("Select i from Item i where i.name = :name", Item.class);
        Item item = queryItem
                .setParameter("name", name)
                .getSingleResult();
        long itemId = item.getId();

        BookingDto bookingDto = new BookingDto(-1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2),
                BookingStatus.WAITING, itemId, user2, item);
        bookingService.create(userId, bookingDto);

        TypedQuery<Booking> queryBooking = em.createQuery("Select b from Booking b where b.item.id = :id", Booking.class);
        Booking booking = queryBooking
                .setParameter("id", itemId)
                .getSingleResult();

        assertNotNull(booking.getId());
        assertEquals(booking.getItem(), item);
        assertEquals(booking.getBooker(), user);
        assertEquals(booking.getStatus(), BookingStatus.WAITING);
    }

    @Test
    void changeBookingStatusTest() {
        UserDto userDto = new UserDto(-1L, "some1@email.com", "name1");
        userService.create(userDto);
        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User user = query
                .setParameter("email", userDto.getEmail())
                .getSingleResult();
        long userId = user.getId();

        UserDto userDto2 = new UserDto(-1L, "some1@emai21.com", "name2");
        userService.create(userDto2);
        TypedQuery<User> query2 = em.createQuery("Select u from User u where u.email = :email", User.class);
        User user2 = query2
                .setParameter("email", userDto2.getEmail())
                .getSingleResult();
        long userId2 = user2.getId();

        String name = RandomStringUtils.randomAlphabetic(10);
        ItemDto itemDto = new ItemDto(1L, name, "desc", true, user2, null, null, null, new ArrayList<>());
        itemService.create(userId2, itemDto);

        TypedQuery<Item> queryItem = em.createQuery("Select i from Item i where i.name = :name", Item.class);
        Item item = queryItem
                .setParameter("name", name)
                .getSingleResult();
        long itemId = item.getId();

        BookingDto bookingDto = new BookingDto(-1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2),
                BookingStatus.WAITING, itemId, user2, item);
        bookingService.create(userId, bookingDto);

        TypedQuery<Booking> queryBooking = em.createQuery("Select b from Booking b where b.item.id = :id", Booking.class);
        Booking booking = queryBooking
                .setParameter("id", itemId)
                .getSingleResult();
        long bookingId = booking.getId();
        bookingService.changeBookingStatus(bookingId, userId2, true);

        assertNotNull(booking.getId());
        assertEquals(booking.getItem(), item);
        assertEquals(booking.getBooker(), user);
        assertEquals(booking.getStatus(), BookingStatus.APPROVED);
    }

    @Test
    void createRequestTest() {
        UserDto userDto = new UserDto(-1L, "some@email123.com", "name");
        userService.create(userDto);
        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User user = query
                .setParameter("email", userDto.getEmail())
                .getSingleResult();
        long userId = user.getId();
        String name = RandomStringUtils.randomAlphabetic(10);
        ItemDto itemDto = new ItemDto(1L, name, "desc", true, user, null, null, null, new ArrayList<>());
        itemService.create(userId, itemDto);
        String description = RandomStringUtils.randomAlphabetic(10);
        ItemRequestDto itemRequestDto = new ItemRequestDto(-1L, user, description, LocalDateTime.now(), List.of(itemDto));
        requestService.create(itemRequestDto, userId);

        TypedQuery<ItemRequest> requestQuery = em.createQuery("Select ir from ItemRequest ir where ir.description = :description", ItemRequest.class);
        ItemRequest result = requestQuery
                .setParameter("description", description)
                .getSingleResult();

        assertNotNull(result.getId());
        assertEquals(result.getOwner(), user);
        assertEquals(result.getDescription(), description);
    }
}
