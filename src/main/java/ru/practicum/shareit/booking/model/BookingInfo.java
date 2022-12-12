package ru.practicum.shareit.booking.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BookingInfo {

    private Long id;

    private LocalDateTime startDateTime;

    private Long bookerId;
}