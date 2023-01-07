package ru.practicum.shareit;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@DataJpaTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ItemRequestRepositoryTests {

    @Autowired
    private ItemRequestRepository itemRequestRepository;
    @Autowired
    private UserRepository userRepository;


    @Test
    @Order(0)
    @Transactional
    @Rollback(value = false)
    void findByOwner_IdOrderByCreateDateTimeDescTest() {
        User user = new User(1L, "Name", "Email@ec.cd");
        userRepository.save(user);
        ItemRequest itemRequest = new ItemRequest(1L, user, "Desc", Instant.now());
        itemRequestRepository.save(itemRequest);

        List<ItemRequest> result = itemRequestRepository.findByOwner_IdOrderByCreateDateTimeDesc(1L);
        Assertions.assertEquals(result.get(0).getId(), 1L);
        Assertions.assertEquals(result.get(0).getDescription(), "Desc");
        Assertions.assertEquals(result.get(0).getOwner().getName(), "Name");
        Assertions.assertEquals(result.get(0).getOwner().getEmail(), "Email@ec.cd");
        Assertions.assertEquals(result.get(0).getOwner().getId(), 1L);
    }

    @Test
    @Transactional
    void findByOwner_IdNotOrderByCreateDateTimeDescTest() {
        User user = new User(1L, "Name", "Email@ec.cd");
        userRepository.save(user);
        ItemRequest itemRequest = new ItemRequest(1L, user, "Desc", Instant.now());
        itemRequestRepository.save(itemRequest);

        List<ItemRequest> result = itemRequestRepository.findByOwner_IdNotOrderByCreateDateTimeDesc(2L, null);
        Assertions.assertEquals(result.get(0).getId(), 1L);
        Assertions.assertEquals(result.get(0).getDescription(), "Desc");
        Assertions.assertEquals(result.get(0).getOwner().getName(), "Name");
        Assertions.assertEquals(result.get(0).getOwner().getEmail(), "Email@ec.cd");
        Assertions.assertEquals(result.get(0).getOwner().getId(), 1L);
    }
}