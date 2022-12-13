package ru.practicum.shareit.booking.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;

@RestController
public class BookingController {
    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping("/bookings/{id}")
    public BookingDto getBookingById(@PathVariable("id") Long bookingId, @RequestHeader("X-Sharer-User-Id") long userId) {
        return bookingService.getById(bookingId, userId);
    }

    @GetMapping("/bookings")
    public List<BookingDto> getBookingsForUser(@RequestParam(required = false, defaultValue = "ALL")
                                               String state, @RequestHeader("X-Sharer-User-Id") long userId) {
        return bookingService.getBookingsForUser(state, userId);

    }

    @GetMapping("/bookings/owner")
    public List<BookingDto> getBookingsForUsersItems(@RequestParam(required = false, defaultValue = "ALL")
                                                     String state, @RequestHeader("X-Sharer-User-Id") long userId) {
        return bookingService.getBookingsForUserItems(state, userId);

    }

    @PostMapping(value = "/bookings")
    public BookingDto create(@Valid @RequestBody BookingDto bookingDto, @RequestHeader("X-Sharer-User-Id") long userId) {
        return bookingService.create(userId, bookingDto);
    }

    @PatchMapping("/bookings/{id}")
    public BookingDto updateStatus(@PathVariable("id") Long bookingId, @RequestHeader("X-Sharer-User-Id") long userId,
                                   @RequestParam Boolean approved) {
        return bookingService.changeBookingStatus(bookingId, userId, approved);
    }
}
