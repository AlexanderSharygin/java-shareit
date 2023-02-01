package shareit.request.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.model.BadRequestException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import shareit.request.client.ItemRequestClient;

import javax.validation.Valid;


@RestController
public class ItemRequestController {
   private final ItemRequestClient requestService;


    public ItemRequestController(ItemRequestClient requestService) {
        this.requestService = requestService;
    }

    @PostMapping(value = "/requests")
    public ResponseEntity<Object> create(@Valid @RequestBody ItemRequestDto itemRequestDto,
                                         @RequestHeader("X-Sharer-User-Id") long userId) {
        if (itemRequestDto.getDescription() == null) {
            throw new BadRequestException("Description can't be null");
        }

        return requestService.create(itemRequestDto, userId);
    }

    @GetMapping(value = "/requests")
    public ResponseEntity<Object> getUserRequestsWithResponses(@RequestHeader("X-Sharer-User-Id") long userId) {
        return requestService.getUserRequests(userId);
    }

    @GetMapping(value = "/requests/all")
    public ResponseEntity<Object> getAllRequestsWithResponses(@RequestHeader("X-Sharer-User-Id") long userId,
                                                            @RequestParam(required = false, defaultValue = "0") int from,
                                                            @RequestParam(required = false, defaultValue = "100") int size) {
        return requestService.getAllRequests(userId, from, size);
    }

    @GetMapping(value = "/requests/{id}")
    public ResponseEntity<Object> getRequestWithResponsesById(@RequestHeader("X-Sharer-User-Id") long userId,
                                                      @PathVariable("id") long requestId) {
        return requestService.getRequestById(userId, requestId);
    }
}
