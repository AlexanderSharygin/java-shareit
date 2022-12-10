package ru.practicum.shareit.booking.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;

/**
 * TODO Sprint add-bookings.
 */
@RestController
public class BookingController {
    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }
    @PostMapping(value = "/bookings")
    public BookingDto create(@Valid @RequestBody BookingDto bookingDto, @RequestHeader("X-Sharer-User-Id") long userId) {
        return bookingService.create(userId, bookingDto);
    }
}
