package shareit.item.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shareit.item.client.ItemClient;
import shareit.item.dto.CommentDto;
import shareit.item.dto.ItemDto;

import static shareit.base.Constants.USER_ID_REQUEST_HEADER_NAME;


@RestController
public class ItemController {

    private final ItemClient itemClient;

    public ItemController(ItemClient itemClient) {
        this.itemClient = itemClient;
    }

    @GetMapping("/items")
    public ResponseEntity<Object> getItems(@RequestHeader(value = USER_ID_REQUEST_HEADER_NAME, required = false) Long userId,
                                           @RequestParam(required = false, defaultValue = "0") int from,
                                           @RequestParam(required = false, defaultValue = "100") int size) {
        if (userId == null) {
            return itemClient.getAll();
        } else {
            return itemClient.getAllByUserId(userId, from, size);
        }
    }

    @GetMapping("/items/{id}")
    public ResponseEntity<Object> getItemById(@PathVariable("id") long itemId, @RequestHeader(USER_ID_REQUEST_HEADER_NAME) long userId) {
        return itemClient.getById(itemId, userId);
    }

    @GetMapping("/items/search")
    public ResponseEntity<Object> getItemsWithSearch(@RequestHeader(value = USER_ID_REQUEST_HEADER_NAME, required = false) long userId,
                                                     @RequestParam String text,
                                                     @RequestParam(required = false, defaultValue = "0") int from,
                                                     @RequestParam(required = false, defaultValue = "100") int size) {
        return itemClient.search(userId, text, from, size);
    }

    @PostMapping(value = "/items")
    public ResponseEntity<Object> create(@Valid @RequestBody ItemDto itemDto, @RequestHeader(USER_ID_REQUEST_HEADER_NAME) long userId) {
        return itemClient.create(userId, itemDto);

    }

    @PatchMapping(value = "/items/{id}")
    public ResponseEntity<Object> update(@Valid @RequestBody ItemDto itemDto, @PathVariable("id") long itemId,
                                         @RequestHeader(USER_ID_REQUEST_HEADER_NAME) long userId) {

        return itemClient.update(itemId, userId, itemDto);
    }

    @PostMapping(value = "/items/{id}/comment")
    public ResponseEntity<Object> addComment(@Valid @RequestBody CommentDto commentDto,
                                             @PathVariable("id") Long itemId,
                                             @RequestHeader(USER_ID_REQUEST_HEADER_NAME) Long userId) {
        return itemClient.addComment(itemId, userId, commentDto);
    }


}