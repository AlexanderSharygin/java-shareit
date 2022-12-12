package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findDistinctByItem_IdInAndStatusOrderByStartDateTimeDesc(Collection<Long> ids, BookingStatus status);
    List<Booking> findByBooker_IdAndStartDateTimeAfterOrderByStartDateTimeDesc(Long id, Instant startDateTime);
    List<Booking> findByBooker_IdAndEndDateTimeBeforeOrderByStartDateTimeDesc(Long id, Instant startDateTime);
    List<Booking> findByBooker_IdAndStartDateTimeBeforeAndEndDateTimeAfterOrderByStartDateTimeDesc(Long id, Instant startDateTime, Instant endDateTime);
    List<Booking> findByBooker_IdAndStatusOrderByStartDateTimeDesc(Long id, BookingStatus status);
    List<Booking> findByBooker_IdOrderByStartDateTimeDesc(Long id);
    List<Booking>  findDistinctByItem_IdInAndStartDateTimeAfterOrderByStartDateTimeDesc(Collection<Long> item_id, Instant startDateTime);
    List<Booking> findDistinctByItem_IdInAndEndDateTimeBeforeOrderByStartDateTimeDesc(Collection<Long> item_id, Instant startDateTimeInstant);
    List<Booking> findDistinctByItem_IdInOrderByStartDateTimeDesc(Collection<Long> item_id);
    List<Booking>  findDistinctByItem_IdInAndStartDateTimeBeforeAndEndDateTimeAfterOrderByStartDateTimeDesc(Collection<Long> item_id, Instant startDateTime, Instant endDateTime);
}
