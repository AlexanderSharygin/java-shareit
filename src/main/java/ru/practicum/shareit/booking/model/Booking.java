package ru.practicum.shareit.booking.model;

import lombok.Data;
import lombok.NonNull;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.Instant;

@Data
public class Booking {
    @NonNull
    private Long id;

    @NonNull
    private Instant startDateTime;

    @NonNull
    private Instant endDateTime;

    @NonNull
    private BookingStatus status;

    @NonNull
    private Item item;

    @NonNull
    private User booker;

    public Booking() {

    }
}

