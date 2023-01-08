package ru.practicum.shareit;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
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
public class ItemRepositoryTests {

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Test
    @Transactional
    void findByOwner_IdTest() {
        List<Item> items = itemRepository.findByOwner_Id(1L);
        Assertions.assertEquals(items.get(0).getId(), 1L);
        Assertions.assertEquals(items.get(0).getName(), "Name");
        Assertions.assertEquals(items.get(0).getDescription(), "Description");
        Assertions.assertEquals(items.get(0).getAvailable(), true);
        Assertions.assertEquals(items.get(0).getOwner().getId(), 1L);
        Assertions.assertEquals(items.get(0).getOwner().getEmail(), "Email@ec.cd");
        Assertions.assertEquals(items.get(0).getOwner().getName(), "Name");
    }

    @Test
    @Transactional
    void findByItemRequest_IdInTest() {
        List<Item> items = itemRepository.findByItemRequest_IdIn(List.of(1L));
        Assertions.assertEquals(items.get(0).getId(), 1L);
        Assertions.assertEquals(items.get(0).getName(), "Name");
        Assertions.assertEquals(items.get(0).getDescription(), "Description");
        Assertions.assertEquals(items.get(0).getAvailable(), true);
        Assertions.assertEquals(items.get(0).getOwner().getId(), 1L);
        Assertions.assertEquals(items.get(0).getOwner().getEmail(), "Email@ec.cd");
        Assertions.assertEquals(items.get(0).getOwner().getName(), "Name");
    }

    @Test
    @Order(0)
    @Transactional
    @Rollback(value = false)
    void findAvailableItemsByNameOrDescriptionTest() {
        User user = new User(1L, "Name", "Email@ec.cd");
        userRepository.save(user);
        ItemRequest itemRequest = new ItemRequest(1L, user, "Desc", Instant.now());
        itemRequestRepository.save(itemRequest);
        Item item = new Item(1L, "Name", "Description", true, user, itemRequest);
        item.setOwner(user);
        itemRepository.save(item);
        List<Item> items = itemRepository.findAvailableItemsByNameOrDescription("Name", "Name", null);
        Assertions.assertEquals(items.get(0).getId(), 1L);
        Assertions.assertEquals(items.get(0).getName(), "Name");
        Assertions.assertEquals(items.get(0).getDescription(), "Description");
        Assertions.assertEquals(items.get(0).getAvailable(), true);
        Assertions.assertEquals(items.get(0).getOwner().getId(), 1L);
        Assertions.assertEquals(items.get(0).getOwner().getEmail(), "Email@ec.cd");
        Assertions.assertEquals(items.get(0).getOwner().getName(), "Name");
    }
}

