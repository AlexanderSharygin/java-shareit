package ru.practicum.shareit.booking.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
public class BookingController {
    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping("/bookings/{id}")
    public BookingDto getBookingById(@PathVariable("id") Long bookingId,
                                     @RequestHeader("X-Sharer-User-Id") long userId) {
        return bookingService.getById(bookingId, userId);
    }

    @GetMapping("/bookings")
    public List<BookingDto> getBookingsForUser(@RequestParam(required = false, defaultValue = "ALL")
                                               String state,
                                               @RequestHeader("X-Sharer-User-Id") long userId,
                                               @RequestParam int from,
                                               @RequestParam int size) {
        Pageable paging = PageRequest.of(from / size, size);
        return bookingService.getBookingsForUser(state, userId, paging);
    }

    @GetMapping("/bookings/owner")
    public List<BookingDto> getBookingsForUsersItems(@RequestParam(required = false, defaultValue = "ALL")
                                                     String state,
                                                     @RequestHeader("X-Sharer-User-Id") long userId,
                                                     @RequestParam int from,
                                                     @RequestParam int size) {
        Pageable paging = PageRequest.of(from, size);
        return bookingService.getBookingsForUserItems(state, userId, paging);
    }

    @PostMapping(value = "/bookings")
    public BookingDto create(@RequestBody BookingDto bookingDto,
                             @RequestHeader("X-Sharer-User-Id") long userId) {
        return bookingService.create(userId, bookingDto);
    }

    @PatchMapping("/bookings/{id}")
    public BookingDto updateStatus(@PathVariable("id") Long bookingId,
                                   @RequestHeader("X-Sharer-User-Id") long userId,
                                   @RequestParam Boolean approved) {
        return bookingService.changeBookingStatus(bookingId, userId, approved);
    }
}