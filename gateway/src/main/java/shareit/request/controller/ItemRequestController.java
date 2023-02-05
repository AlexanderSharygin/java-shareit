package shareit.request.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import shareit.exception.model.BadRequestException;
import shareit.request.client.ItemRequestClient;
import shareit.request.dto.ItemRequestDto;


import javax.validation.Valid;


@RestController
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    public ItemRequestController(ItemRequestClient itemRequestClient) {
        this.itemRequestClient = itemRequestClient;
    }

    @PostMapping(value = "/requests")
    public ResponseEntity<Object> create(@Valid @RequestBody ItemRequestDto itemRequestDto,
                                         @RequestHeader("X-Sharer-User-Id") long userId) {
        if (itemRequestDto.getDescription() == null) {
            throw new BadRequestException("Description can't be null");
        }

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
