package ru.practicum.shareit.booking.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.model.BadRequestException;

import java.util.List;

import static ru.practicum.shareit.base.Constants.USER_ID_REQUEST_HEADER_NAME;

@RestController
public class BookingController {

    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping(value = "/bookings")
    public BookingDto create(@Valid @RequestBody BookingDto bookingDto,
                             @RequestHeader(USER_ID_REQUEST_HEADER_NAME) long userId) {
        return bookingService.create(userId, bookingDto);
    }

    @PatchMapping("/bookings/{id}")
    public BookingDto changeBookingStatus(@PathVariable("id") Long bookingId,
                                          @RequestHeader(USER_ID_REQUEST_HEADER_NAME) long userId,
                                          @RequestParam Boolean approved) {
        return bookingService.changeBookingStatus(bookingId, userId, approved);
    }

    @GetMapping("/bookings/{id}")
    public BookingDto getBooking(@RequestHeader(value = USER_ID_REQUEST_HEADER_NAME, required = false) Long userId,
                                 @PathVariable("id") Long bookingId) {
        return bookingService.getById(bookingId, userId);
    }

    @GetMapping("/bookings")
    public List<BookingDto> getBookingsForUser(@RequestHeader(USER_ID_REQUEST_HEADER_NAME) Long userId,
                                               @RequestParam(required = false, defaultValue = "ALL") String state,
                                               @RequestParam(required = false, defaultValue = "0") int from,
                                               @RequestParam(required = false, defaultValue = "100") int size) {
        if (from < 0 || size < 0) {
            throw new BadRequestException("Неверные параметры пагинации");
        }
        Pageable paging = PageRequest.of(from / size, size);

        return bookingService.getBookingsForUser(state, userId, paging);
    }

    @GetMapping("/bookings/owner")
    public List<BookingDto> getBookingsForUserItems(@RequestHeader(USER_ID_REQUEST_HEADER_NAME) Long userId,
                                                    @RequestParam(required = false, defaultValue = "ALL") String state,
                                                    @RequestParam(required = false, defaultValue = "0") int from,
                                                    @RequestParam(required = false, defaultValue = "100") int size) {
        if (from < 0 || size < 0) {
            throw new BadRequestException("Неверные параметры пагинации");
        }
        Pageable paging = PageRequest.of(from / size, size);
        return bookingService.getBookingsForUserItems(state, userId, paging);
    }
}