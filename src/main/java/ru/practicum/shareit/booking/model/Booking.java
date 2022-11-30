package ru.practicum.shareit.booking.model;

import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.Instant;

@Data
public class Booking {

    private Long id;

    private Instant startDateTime;

    private Instant endDateTime;

    private BookingStatus status;

    private Item item;

    private User booker;

    public Booking() {

    }
}

