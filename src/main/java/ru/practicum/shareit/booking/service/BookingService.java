package ru.practicum.shareit.booking.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoStatus;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.model.BadRequestException;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemsRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.time.LocalDateTime.now;


@Service
@Slf4j
public class BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemsRepository itemRepository;

    @Autowired
    public BookingService(BookingRepository bookingRepository, UserRepository userRepository,
                          ItemsRepository itemRepository) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    public BookingDto getById(long bookingId, long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с " + bookingId + " не существует"));
        Long bookerId = booking.getBooker().getId();
        Long ownerId = booking.getItem().getOwner().getId();
        if (bookerId != userId && ownerId != userId) {
            throw new NotFoundException("User c id " + userId + "не является собственником или " +
                    "создателем бронирования " + bookingId);
        }

        return BookingMapper.toBookingDto(booking);
    }

    public List<BookingDto> getBookingsForUser(String status, long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User с id " + userId + " не найден"));
        if (status.equals(BookingDtoStatus.WAITING.toString())) {
            return bookingRepository.findByBooker_IdAndStatusOrderByStartDateTimeDesc(userId, BookingStatus.WAITING)
                    .stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
        } else if (status.equals(BookingDtoStatus.REJECTED.toString())) {
            return bookingRepository.findByBooker_IdAndStatusOrderByStartDateTimeDesc(userId, BookingStatus.REJECTED)
                    .stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
        } else if (status.equals(BookingDtoStatus.FUTURE.toString())) {
            return bookingRepository.findFutureBookingsByBookerId(userId, now().toInstant(ZoneOffset.UTC))
                    .stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
        } else if (status.equals(BookingDtoStatus.PAST.toString())) {
            return bookingRepository.findPastBookingsByBookerId(userId, now().toInstant(ZoneOffset.UTC))
                    .stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
        } else if (status.equals(BookingDtoStatus.CURRENT.toString())) {
            return bookingRepository.findCurrentBookingsByBookerId(userId, now().toInstant(ZoneOffset.UTC),
                            now().toInstant(ZoneOffset.UTC)).stream().map(BookingMapper::toBookingDto)
                    .collect(Collectors.toList());
        } else if (status.equals(BookingDtoStatus.ALL.toString())) {
            return bookingRepository.findByBooker_IdOrderByStartDateTimeDesc(userId)
                    .stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
        } else {
            throw new BadRequestException("Статус " + status + " не известен");
        }
    }

    public List<BookingDto> getBookingsForUserItems(String status, long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User с id " + userId + " не найден"));
        List<Long> userItems = itemRepository.findAllByOwnerId(userId)
                .stream().map(Item::getId).collect(Collectors.toList());
        if (userItems.isEmpty()) {
            return new ArrayList<>();
        }
        if (status.equals(BookingDtoStatus.WAITING.toString())) {
            return bookingRepository.findDistinctByItem_IdInAndStatus(userItems, BookingStatus.WAITING)
                    .stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
        } else if (status.equals(BookingDtoStatus.REJECTED.toString())) {
            return bookingRepository.findDistinctByItem_IdInAndStatus(userItems, BookingStatus.REJECTED)
                    .stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
        } else if (status.equals(BookingDtoStatus.FUTURE.toString())) {
            return bookingRepository.findFutureBookingsDistinctByItemsIdList(userItems, now().toInstant(ZoneOffset.UTC))
                    .stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
        } else if (status.equals(BookingDtoStatus.PAST.toString())) {
            return bookingRepository.findPastBookingsByItemsIdList(userItems, now().toInstant(ZoneOffset.UTC))
                    .stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
        } else if (status.equals(BookingDtoStatus.CURRENT.toString())) {
            return bookingRepository.findCurrentBookingsByItemIdList(userItems, now().toInstant(ZoneOffset.UTC),
                            now().toInstant(ZoneOffset.UTC)).stream().map(BookingMapper::toBookingDto)
                    .collect(Collectors.toList());
        } else if (status.equals(BookingDtoStatus.ALL.toString())) {
            return bookingRepository.findDistinctByItem_IdInOrderByStartDateTimeDesc(userItems)
                    .stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
        } else {
            throw new BadRequestException("Статус " + status + " не известен");
        }
    }

    public BookingDto create(long userId, BookingDto bookingDto) {
        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User с id " + userId + " не найден"));
        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Item с id " + bookingDto.getItemId() + " не найден"));
        if (bookingDto.getEnd().isBefore(bookingDto.getStart()) || bookingDto.getEnd().equals(bookingDto.getStart())) {
            throw new BadRequestException("Неверные даты начала/окончания бронирования");
        }
        if (userId == item.getOwner().getId()) {
            throw new NotFoundException("Невозможно создать бронирование для собственной вещи");
        }
        if (!item.getAvailable()) {
            throw new BadRequestException("Нельзя создать бронирование для недоступной вещи");
        }
        Booking booking = BookingMapper.fromBookingDto(bookingDto);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);
        booking.setBooker(booker);

        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    public BookingDto changeBookingStatus(long bookingId, long userId, boolean isSetApprove) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с " + bookingId + " не существует"));
        Long ownerId = booking.getItem().getOwner().getId();
        if (ownerId != userId) {
            throw new BadRequestException("User c id " + userId + " не является собственником вещи");
        }
        if (isSetApprove && !booking.getStatus().equals(BookingStatus.APPROVED)) {
            booking.setStatus(BookingStatus.APPROVED);

            return BookingMapper.toBookingDto(bookingRepository.save(booking));
        } else if (!isSetApprove && !booking.getStatus().equals(BookingStatus.REJECTED)) {
            booking.setStatus(BookingStatus.REJECTED);

            return BookingMapper.toBookingDto(bookingRepository.save(booking));
        } else {
            throw new BadRequestException("Ошибка обновления статуса для бронирования с id " + bookingId);
        }
    }
}
