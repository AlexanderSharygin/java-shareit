package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingInfo {

    private Long id;

    private LocalDateTime startDateTime;

    private LocalDateTime endDateTime;

    private Long bookerId;
}