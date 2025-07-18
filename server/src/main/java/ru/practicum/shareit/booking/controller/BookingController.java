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

@RestController
public class BookingController {

    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping(value = "/bookings")
    public BookingDto create(@Valid @RequestBody BookingDto bookingDto,
                             @RequestHeader("X-Sharer-User-Id") long userId) {
        return bookingService.create(userId, bookingDto);
    }

    @PatchMapping("/bookings/{id}")
    public BookingDto changeBookingStatus(@PathVariable("id") Long bookingId,
                                          @RequestHeader("X-Sharer-User-Id") long userId,
                                          @RequestParam Boolean approved) {
        return bookingService.changeBookingStatus(bookingId, userId, approved);
    }

    @GetMapping("/bookings/{id}")
    public BookingDto getBooking(@RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId,
                                 @PathVariable("id") Long bookingId) {
        return bookingService.getById(bookingId, userId);
    }

    @GetMapping("/bookings")
    public List<BookingDto> getBookingsForUser(@RequestHeader("X-Sharer-User-Id") Long userId,
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
    public List<BookingDto> getBookingsForUserItems(@RequestHeader("X-Sharer-User-Id") Long userId,
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