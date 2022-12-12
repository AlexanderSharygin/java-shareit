package ru.practicum.shareit.booking.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

public class BookingMapper {

    public static BookingDto toBookingDto(Booking booking) {
        return new BookingDto(booking.getId(),
                LocalDateTime.ofInstant(booking.getStartDateTime(), ZoneId.of("UTC")),
                LocalDateTime.ofInstant(booking.getEndDateTime(), ZoneId.of("UTC")),
                booking.getStatus(),
                booking.getItem().getId(),
                booking.getBooker(),
                booking.getItem());
    }

    public static Booking fromBookingDto(BookingDto bookingDto) {
        return new Booking(-1L,
                bookingDto.getStart().toInstant(ZoneOffset.UTC),
                bookingDto.getEnd().toInstant(ZoneOffset.UTC),
                bookingDto.getStatus(),
                null,
                null);
    }
}
