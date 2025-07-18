package shareit.request.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shareit.request.client.ItemRequestClient;
import shareit.request.dto.ItemRequestDto;


@RestController
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    public ItemRequestController(ItemRequestClient itemRequestClient) {
        this.itemRequestClient = itemRequestClient;
    }


    @PostMapping(value = "/requests")
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @Valid @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestClient.create(itemRequestDto, userId);
    }

    @GetMapping(value = "/requests")
    public ResponseEntity<Object> getUserRequestsWithResponses(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemRequestClient.getUserRequests(userId);
    }

    @GetMapping(value = "/requests/all")
    public ResponseEntity<Object> getAllRequestsWithResponses(@RequestHeader("X-Sharer-User-Id") long userId,
                                                              @RequestParam(required = false, defaultValue = "0") int from,
                                                              @RequestParam(required = false, defaultValue = "100") int size) {
        return itemRequestClient.getAllRequests(userId, from, size);
    }

    @GetMapping(value = "/requests/{id}")
    public ResponseEntity<Object> getRequestWithResponsesById(@RequestHeader("X-Sharer-User-Id") long userId,
                                                              @PathVariable("id") long requestId) {
        return itemRequestClient.getById(userId, requestId);
    }
}
