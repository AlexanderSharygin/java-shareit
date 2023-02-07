package ru.practicum.shareit.request.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.RequestService;

import java.util.List;


@RestController
public class ItemRequestController {
    private final RequestService requestService;

    @Autowired
    public ItemRequestController(RequestService requestService) {
        this.requestService = requestService;
    }

    @PostMapping(value = "/requests")
    public ItemRequestDto create(@RequestBody ItemRequestDto itemRequestDto,
                                 @RequestHeader("X-Sharer-User-Id") long userId) {
        return requestService.create(itemRequestDto, userId);
    }

    @GetMapping(value = "/requests")
    public List<ItemRequestDto> getUserRequestsWithResponses(@RequestHeader("X-Sharer-User-Id") long userId) {
        return requestService.getUserRequests(userId);
    }

    @GetMapping(value = "/requests/all")
    public List<ItemRequestDto> getAllRequestsWithResponses(@RequestHeader("X-Sharer-User-Id") long userId,
                                                            @RequestParam int from,
                                                            @RequestParam int size) {
        return requestService.getAllRequests(userId, from, size);
    }

    @GetMapping(value = "/requests/{id}")
    public ItemRequestDto getRequestWithResponsesById(@RequestHeader("X-Sharer-User-Id") long userId,
                                                      @PathVariable("id") long requestId) {
        return requestService.getRequestById(userId, requestId);
    }
}


