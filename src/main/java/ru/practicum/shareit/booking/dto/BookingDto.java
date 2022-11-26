package ru.practicum.shareit.booking.dto;

import lombok.Data;
import lombok.NonNull;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.Instant;

/**
 * TODO Sprint add-bookings.
 */
@Data
public class BookingDto {
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

    public BookingDto() {

    }
}
