package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("select distinct b from Booking b where b.item.id in ?1 and b.status = ?2 order by b.startDateTime DESC")
    List<Booking> findDistinctByItem_IdInAndStatus(Collection<Long> ids, BookingStatus status);

    List<Booking> findDistinctByItem_IdInOrderByStartDateTimeDesc(Collection<Long> itemId);

    List<Booking> findByBooker_IdAndStatusOrderByStartDateTimeDesc(Long id, BookingStatus status);

    List<Booking> findByBooker_IdOrderByStartDateTimeDesc(Long id);

    @Query("select b from Booking b where b.booker.id = ?1 and b.endDateTime < ?2 order by b.startDateTime DESC")
    List<Booking> findPastBookingsByBookerId(Long id, Instant startDateTime);

    @Query("select distinct b from Booking b where b.item.id in ?1 and b.endDateTime < ?2 order by b.startDateTime DESC")
    List<Booking> findPastBookingsByItemsIdList(Collection<Long> itemId, Instant startDateTimeInstant);

    @Query("select b from Booking b where b.booker.id = ?1 and b.startDateTime < ?2 and b.endDateTime > ?3 " +
            "order by b.startDateTime DESC")
    List<Booking> findCurrentBookingsByBookerId(Long id, Instant startDateTime, Instant endDateTime);

    @Query("select distinct b from Booking b where b.item.id in ?1 and b.startDateTime < ?2 and b.endDateTime > ?3 " +
            "order by b.startDateTime DESC")
    List<Booking> findCurrentBookingsByItemIdList(Collection<Long> itemId, Instant startDateTime, Instant endDateTime);

    @Query("select b from Booking b where b.booker.id = ?1 and b.startDateTime > ?2 order by b.startDateTime DESC")
    List<Booking> findFutureBookingsByBookerId(Long id, Instant startDateTime);

    @Query("select distinct b from Booking b where b.item.id in ?1 and b.startDateTime > ?2 order by b.startDateTime DESC")
    List<Booking> findFutureBookingsDistinctByItemsIdList(Collection<Long> itemId, Instant startDateTime);
}