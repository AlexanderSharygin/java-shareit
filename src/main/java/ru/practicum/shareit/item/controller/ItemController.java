package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@RestController
public class ItemController {

    private final ItemService itemService;
    private final UserService userService;

    @Autowired
    public ItemController(ItemService itemService, UserService userService) {
        this.itemService = itemService;
        this.userService = userService;
    }

    @GetMapping("/items")
    public List<ItemDto> getItems(@RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId) {
        if (userId == null) {
            return itemService.getAll();
        } else {
            return itemService.getAllByUserId(userId);
        }
    }

    @GetMapping("/items/{id}")
    public ItemDto getItemById(@PathVariable("id") long itemId, @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.getById(itemId, userId);
    }

    @GetMapping("/items/search")
    public List<ItemDto> getItemsWithSearch(@RequestHeader(value = "X-Sharer-User-Id", required = false)
                                            @RequestParam String text) {
        return itemService.getAllByNameOrDescription(text);
    }

    @PostMapping(value = "/items")
    public ItemDto create(@Valid @RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") long userId) {
        User owner = UserMapper.fromUserDto(userService.getById(userId));
        owner.setId(userId);
        itemDto.setOwner(owner);
        return itemService.create(userId, itemDto);

    }

    @PatchMapping(value = "/items/{id}")
    public ItemDto update(@Valid @RequestBody ItemDto itemDto, @PathVariable("id") long itemId,
                          @RequestHeader("X-Sharer-User-Id") long userId) {
        itemService.update(itemId, userId, itemDto);
        return itemService.getById(itemId, userId);
    }

    @DeleteMapping(value = "/items/{id}")
    public void delete(@PathVariable("id") long usersId) {
        itemService.delete(usersId);
    }

    @PostMapping(value = "/items/{id}/comment")
    public CommentDto addComment(@Valid @RequestBody CommentDto commentDto,
                                 @PathVariable("id") Long itemId,
                                 @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.addComment(itemId, userId, commentDto);
    }
}

