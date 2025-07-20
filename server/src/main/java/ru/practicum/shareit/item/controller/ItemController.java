package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

import static ru.practicum.shareit.base.Constants.USER_ID_REQUEST_HEADER_NAME;

@RestController
public class ItemController {

    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping("/items")
    public List<ItemDto> getItems(@RequestHeader(value = USER_ID_REQUEST_HEADER_NAME, required = false) Long userId,
                                  @RequestParam(required = false, defaultValue = "0") int from,
                                  @RequestParam(required = false, defaultValue = "100") int size) {
        Pageable paging = PageRequest.of(from, size);
        if (userId == null) {
            return itemService.getAll();
        } else {
            return itemService.getAllByUserId(userId, paging);
        }
    }

    @GetMapping("/items/{id}")
    public ItemDto getItemById(@PathVariable("id") long itemId, @RequestHeader(USER_ID_REQUEST_HEADER_NAME) long userId) {
        return itemService.getById(itemId, userId);
    }

    @GetMapping("/items/search")
    public List<ItemDto> getItemsWithSearch(@RequestHeader(value = USER_ID_REQUEST_HEADER_NAME, required = false)
                                            @RequestParam String text,
                                            @RequestParam(required = false, defaultValue = "0") int from,
                                            @RequestParam(required = false, defaultValue = "100") int size) {
        Pageable paging = PageRequest.of(from, size);
        return itemService.getAllByNameOrDescription(text, paging);
    }

    @PostMapping(value = "/items")
    public ItemDto create(@Valid @RequestBody ItemDto itemDto, @RequestHeader(USER_ID_REQUEST_HEADER_NAME) long userId) {
        return itemService.create(userId, itemDto);

    }

    @PatchMapping(value = "/items/{id}")
    public ItemDto update(@Valid @RequestBody ItemDto itemDto, @PathVariable("id") long itemId,
                          @RequestHeader(USER_ID_REQUEST_HEADER_NAME) long userId) {

        return itemService.update(itemId, userId, itemDto);
    }

    @DeleteMapping(value = "/items/{id}")
    public void delete(@PathVariable("id") long usersId) {
        itemService.delete(usersId);
    }

    @PostMapping(value = "/items/{id}/comment")
    public CommentDto addComment(@Valid @RequestBody CommentDto commentDto,
                                 @PathVariable("id") Long itemId,
                                 @RequestHeader(USER_ID_REQUEST_HEADER_NAME) Long userId) {
        return itemService.addComment(itemId, userId, commentDto);
    }
}

