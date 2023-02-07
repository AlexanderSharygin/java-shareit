package ru.practicum.shareit.item.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

@RestController
public class ItemController {

    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping("/items")
    public List<ItemDto> getAllItems(@RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId,
                                     @RequestParam int from,
                                     @RequestParam int size) {
        Pageable paging = PageRequest.of(from, size);
        if (userId == null) {
            return itemService.getAll(paging);
        } else {
            return itemService.getAllForUser(userId, paging);
        }
    }

    @GetMapping("/items/{id}")
    public ItemDto getItemById(@PathVariable("id") long itemId, @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.getById(itemId, userId);
    }

    @GetMapping("/items/search")
    public List<ItemDto> getItemsWithSearch(@RequestHeader(value = "X-Sharer-User-Id", required = false)
                                            @RequestParam String text,
                                            @RequestParam int from,
                                            @RequestParam int size) {
        Pageable paging = PageRequest.of(from, size);
        return itemService.getByNameOrDescription(text, paging);
    }

    @PostMapping(value = "/items")
    public ItemDto create(@Valid @RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.create(userId, itemDto);
    }

    @PatchMapping(value = "/items/{id}")
    public ItemDto update(@Valid @RequestBody ItemDto itemDto, @PathVariable("id") long itemId,
                          @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.update(itemId, userId, itemDto);
    }

    @PostMapping(value = "/items/{id}/comment")
    public CommentDto addComment(@Valid @RequestBody CommentDto commentDto,
                                 @PathVariable("id") long itemId, @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.addComment(itemId, userId, commentDto);
    }
}
