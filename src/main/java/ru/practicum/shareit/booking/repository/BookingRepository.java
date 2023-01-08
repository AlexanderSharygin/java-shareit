package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("SELECT DISTINCT b FROM Booking b WHERE b.item.id IN ?1 AND b.status = ?2 ORDER BY b.startDateTime DESC")
    List<Booking> findDistinctByItem_IdInAndStatus(Collection<Long> ids, BookingStatus status, Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = ?1 AND b.startDateTime > ?2 ORDER BY b.startDateTime DESC")
    List<Booking> findFutureBookingsByBookerId(Long id, Instant startDateTime, Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = ?1 AND b.endDateTime < ?2 ORDER BY b.startDateTime DESC")
    List<Booking> findPastBookingsByBookerId(Long id, Instant startDateTime, Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = ?1 AND b.endDateTime < ?2 ORDER BY b.startDateTime DESC")
    List<Booking> findPastBookingsByBookerId(Long id, Instant startDateTime);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = ?1 AND b.startDateTime < ?2 AND b.endDateTime > ?3 " +
            "ORDER BY b.startDateTime DESC")
    List<Booking> findCurrentBookingsByBookerId(Long id, Instant startDateTime, Instant endDateTime, Pageable pageable);

    List<Booking> findByBooker_IdAndStatusOrderByStartDateTimeDesc(Long id, BookingStatus status, Pageable pageable);

    List<Booking> findByBooker_IdOrderByStartDateTimeDesc(Long id, Pageable pageable);

    @Query("SELECT DISTINCT b FROM Booking b WHERE b.item.id IN ?1 AND b.startDateTime > ?2 ORDER BY b.startDateTime DESC")
    List<Booking> findFutureBookingsDistinctByItemsIdList(Collection<Long> itemId, Instant startDateTime, Pageable pageable);


    @Query("SELECT DISTINCT b FROM Booking b WHERE b.item.id IN ?1 AND b.startDateTime > ?2 ORDER BY b.startDateTime DESC")
    List<Booking> findFutureBookingsDistinctByItemsIdList(Collection<Long> itemId, Instant startDateTime);

    @Query("SELECT DISTINCT b FROM Booking b WHERE b.item.id IN ?1 AND b.endDateTime < ?2 ORDER BY b.startDateTime DESC")
    List<Booking> findPastBookingsByItemsIdList(Collection<Long> itemId, Instant startDateTimeInstant, Pageable pageable);

    @Query("SELECT DISTINCT b FROM Booking b WHERE b.item.id IN ?1 AND b.endDateTime < ?2 ORDER BY b.startDateTime DESC")
    List<Booking> findPastBookingsByItemsIdList(Collection<Long> itemId, Instant startDateTimeInstant);

    List<Booking> findDistinctByItem_IdInOrderByStartDateTimeDesc(Collection<Long> itemId, Pageable pageable);

    @Query("SELECT DISTINCT b FROM Booking b WHERE b.item.id IN ?1 AND b.startDateTime < ?2 AND b.endDateTime > ?3 " +
            "ORDER BY b.startDateTime DESC")
    List<Booking> findCurrentBookingsByItemIdList(Collection<Long> itemId, Instant startDateTime, Instant endDateTime, Pageable pageable);
}
