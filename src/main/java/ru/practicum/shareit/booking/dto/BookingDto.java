package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.utils.LocalDateTimeDeserializer;
import ru.practicum.shareit.utils.LocalDateTimeSerializer;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BookingDto {
    @NonNull
    private Long id;

    @NonNull
    private LocalDateTime start;

    @NonNull
    private LocalDateTime end;

    @NonNull
    private BookingStatus status;

    @NonNull
    private Long itemId;

    @NonNull
    private User booker;

    @NonNull
    private Item item;

    public BookingDto() {

    }
}