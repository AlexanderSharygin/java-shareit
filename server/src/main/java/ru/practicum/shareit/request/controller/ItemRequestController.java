package ru.practicum.shareit.request.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    public ItemRequestDto create(@RequestHeader("X-Sharer-User-Id") long userId,
                                 @Valid @RequestBody ItemRequestDto itemRequestDto) {
        return requestService.create(itemRequestDto, userId);
    }

    @GetMapping(value = "/requests")
    public List<ItemRequestDto> getUserRequestsWithResponses(@RequestHeader("X-Sharer-User-Id") long userId) {
        return requestService.getUserRequests(userId);
    }

    @GetMapping(value = "/requests/all")
    public List<ItemRequestDto> getAllRequestsWithResponses(@RequestHeader("X-Sharer-User-Id") long userId,
                                                            @RequestParam(required = false, defaultValue = "0") int from,
                                                            @RequestParam(required = false, defaultValue = "100") int size) {
        Pageable paging = PageRequest.of(from, size);

        return requestService.getAllRequests(userId, paging);
    }

    @GetMapping(value = "/requests/{id}")
    public ItemRequestDto getRequestWithResponsesById(@RequestHeader("X-Sharer-User-Id") long userId,
                                                      @PathVariable("id") long requestId) {
        return requestService.getRequestById(userId, requestId);
    }
}