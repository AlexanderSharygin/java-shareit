package ru.practicum.shareit.booking.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoStatus;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.model.BadRequestException;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.time.LocalDateTime.now;
import static java.time.ZoneOffset.UTC;
import static ru.practicum.shareit.booking.model.BookingStatus.*;


@Service
@Slf4j
public class BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Autowired
    public BookingService(BookingRepository bookingRepository, UserRepository userRepository, ItemRepository itemRepository) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    public BookingDto create(long userId, BookingDto bookingDto) {
        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id " + userId + " not exists in the DB"));
        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new NotFoundException(
                        "Item with id " + bookingDto.getItemId() + " not exists in the DB"));
        if (!item.getAvailable()) {
            throw new BadRequestException("Can't create the booking for the unavailable item");
        }
        if (bookingDto.getEnd().isBefore(now())) {
            throw new BadRequestException("Can't create the booking with end date in the past");
        }
        if (bookingDto.getStart().isBefore(now())) {
            throw new BadRequestException("Can't create the booking with start date in the past");
        }
        if (bookingDto.getEnd().isBefore(bookingDto.getStart())) {
            throw new BadRequestException("Can't create the booking with end date which is before start date");
        }
        if (userId == item.getOwner().getId()) {
            throw new NotFoundException("Can't create the booking. User can't book own items");
        }
        Booking booking = BookingMapper.fromBookingDto(bookingDto);
        booking.setItem(item);
        booking.setStatus(WAITING);
        booking.setBooker(booker);

        return BookingMapper.toBookingDto(bookingRepository.save(booking));

    }

    public BookingDto getById(long bookingId, long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking with id " + bookingId + " not exists in the DB"));
        Long bookerId = booking.getBooker()
                .getId();
        Long itemOwnerId = booking.getItem()
                .getOwner()
                .getId();
        if (bookerId != userId && itemOwnerId != userId) {
            throw new NotFoundException(
                    "User with id " + userId + " is not booker/owner for booking with id " + bookingId);
        }

        return BookingMapper.toBookingDto(booking);
    }

    public BookingDto changeBookingStatus(long bookingId, long userId, boolean isSetApprove) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking with id " + bookingId + " not exists in the DB"));
        Long itemOwnerId = booking.getItem()
                .getOwner()
                .getId();
        if (itemOwnerId != userId) {
            throw new NotFoundException(
                    "User with id " + userId + " is not owner for item from booking with id " + bookingId);
        }
        if (isSetApprove && !booking.getStatus().equals(APPROVED)) {
            booking.setStatus(APPROVED);
            bookingRepository.save(booking);
        } else if (!isSetApprove && !booking.getStatus().equals(REJECTED)) {
            booking.setStatus(REJECTED);
            bookingRepository.save(booking);
        } else {
            throw new BadRequestException("Status is already updated for booking with id " + bookingId);
        }

        return BookingMapper.toBookingDto(bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking with id " + bookingId + " not exists in the DB")));
    }

    public List<BookingDto> getBookingsForUser(String status, long userId, Pageable paging) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id " + userId + " not exists in the DB"));
        List<BookingDto> result;
        if (status.equals(BookingDtoStatus.WAITING.toString())) {
            result = bookingRepository.findByBooker_IdAndStatusOrderByStartDateTimeDesc(userId, WAITING, paging)
                    .stream()
                    .map(BookingMapper::toBookingDto)
                    .collect(Collectors.toList());
        } else if (status.equals(BookingDtoStatus.REJECTED.toString())) {
            result = bookingRepository.findByBooker_IdAndStatusOrderByStartDateTimeDesc(userId, REJECTED, paging)
                    .stream()
                    .map(BookingMapper::toBookingDto)
                    .collect(Collectors.toList());
        } else if (status.equals(BookingDtoStatus.FUTURE.toString())) {
            result = bookingRepository.findFutureBookingsByBookerId(userId, now().toInstant(UTC), paging)
                    .stream()
                    .map(BookingMapper::toBookingDto)
                    .collect(Collectors.toList());
        } else if (status.equals(BookingDtoStatus.PAST.toString())) {
            result = bookingRepository.findPastBookingsByBookerId(userId, now().toInstant(UTC), paging)
                    .stream()
                    .map(BookingMapper::toBookingDto)
                    .collect(Collectors.toList());
        } else if (status.equals(BookingDtoStatus.CURRENT.toString())) {
            result = bookingRepository.findCurrentBookingsByBookerId(userId, now().toInstant(UTC), now().toInstant(UTC), paging)
                    .stream()
                    .map(BookingMapper::toBookingDto)
                    .collect(Collectors.toList());
        } else if (status.equals(BookingDtoStatus.ALL.toString())) {
            result = bookingRepository.findByBooker_IdOrderByStartDateTimeDesc(userId, paging)
                    .stream()
                    .map(BookingMapper::toBookingDto)
                    .collect(Collectors.toList());
        } else {
            throw new BadRequestException("Unknown state: " + status);
        }

        return result;
    }

    public List<BookingDto> getBookingsForUserItems(String status, long userId, Pageable paging) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id " + userId + " not exists in the DB"));
        List<Long> userItems = itemRepository.findByOwner_Id(userId)
                .stream()
                .map(Item::getId)
                .collect(Collectors.toList());
        List<BookingDto> result;
        if (userItems.isEmpty()) {
            result = new ArrayList<>();
        } else if (status.equals(BookingDtoStatus.WAITING.toString())) {
            result = bookingRepository.findDistinctByItem_IdInAndStatus(userItems, WAITING, paging)
                    .stream()
                    .map(BookingMapper::toBookingDto)
                    .collect(Collectors.toList());
        } else if (status.equals(BookingDtoStatus.REJECTED.toString())) {
            result = bookingRepository.findDistinctByItem_IdInAndStatus(userItems, REJECTED, paging)
                    .stream()
                    .map(BookingMapper::toBookingDto)
                    .collect(Collectors.toList());
        } else if (status.equals(BookingDtoStatus.FUTURE.toString())) {
            result = bookingRepository.findFutureBookingsDistinctByItemsIdList(userItems, now().toInstant(UTC), paging)
                    .stream()
                    .map(BookingMapper::toBookingDto)
                    .collect(Collectors.toList());
        } else if (status.equals(BookingDtoStatus.PAST.toString())) {
            result = bookingRepository.findPastBookingsByItemsIdList(userItems, now().toInstant(UTC), paging)
                    .stream()
                    .map(BookingMapper::toBookingDto)
                    .collect(Collectors.toList());
        } else if (status.equals(BookingDtoStatus.CURRENT.toString())) {
            result = bookingRepository.findCurrentBookingsByItemIdList(userItems, now().toInstant(UTC), now().toInstant(UTC), paging)
                    .stream()
                    .map(BookingMapper::toBookingDto)
                    .collect(Collectors.toList());

        } else if (status.equals(BookingDtoStatus.ALL.toString())) {
            result = bookingRepository.findDistinctByItem_IdInOrderByStartDateTimeDesc(userItems, paging)
                    .stream()
                    .map(BookingMapper::toBookingDto)
                    .collect(Collectors.toList());
        } else {
            throw new BadRequestException("Unknown state: " + status);
        }

        return result;
    }
}