package shareit.item.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import shareit.item.client.ItemClient;

import javax.validation.Valid;


@RestController
public class ItemController {

    private final ItemClient itemClient;

    @Autowired
    public ItemController(ItemClient itemClient) {
        this.itemClient = itemClient;
    }

    @GetMapping("/items")
    public ResponseEntity<Object> getAllItems(@RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId,
                                              @RequestParam(required = false, defaultValue = "0") int from,
                                              @RequestParam(required = false, defaultValue = "100") int size) {
        return itemClient.getAll(userId, from, size);
    }

    @GetMapping("/items/{id}")
    public ResponseEntity<Object> getItemById(@PathVariable("id") long itemId, @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemClient.getById(itemId, userId);
    }

    @GetMapping("/items/search")
    public ResponseEntity<Object> getItemsWithSearch(@RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId,
                                                     @RequestParam String text,
                                                     @RequestParam(required = false, defaultValue = "0") int from,
                                                     @RequestParam(required = false, defaultValue = "100") int size) {

        return itemClient.search(userId, text, from, size);
    }

    @PostMapping(value = "/items")
    public ResponseEntity<Object> create(@Valid @RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemClient.create(userId, itemDto);
    }

    @PatchMapping(value = "/items/{id}")
    public ResponseEntity<Object> update(@Valid @RequestBody ItemDto itemDto, @PathVariable("id") long itemId,
                                         @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemClient.update(itemId, userId, itemDto);
    }

    @PostMapping(value = "/items/{id}/comment")
    public ResponseEntity<Object> addComment(@Valid @RequestBody CommentDto commentDto,
                                             @PathVariable("id") long itemId, @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemClient.addComment(itemId, userId, commentDto);
    }
}