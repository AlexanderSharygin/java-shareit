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
public class CommentRepositoryTests {

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private CommentRepository commentRepository;


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

        Comment comment = new Comment(1L, "Text", user, item, LocalDateTime.now());
        commentRepository.save(comment);

        List<Comment> comments = commentRepository.findCommentsForItems(List.of(1L));
        Assertions.assertEquals(comments.get(0).getId(), 1L);
        Assertions.assertEquals(comments.get(0).getText(), "Text");
        Assertions.assertEquals(comments.get(0).getAuthor().getId(), 1L);
        Assertions.assertEquals(comments.get(0).getAuthor().getEmail(), "Email@ec.cd");
        Assertions.assertEquals(comments.get(0).getAuthor().getName(), "Name");
        Assertions.assertEquals(comments.get(0).getItem().getId(), 1L);
        Assertions.assertEquals(comments.get(0).getItem().getDescription(), "Description");
        Assertions.assertEquals(comments.get(0).getItem().getAvailable(), true);
    }
}

