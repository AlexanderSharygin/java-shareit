package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    public BookingDto create(long userId, BookingDto bookingDto) {
        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id " + userId + " not exists in the DB"));
        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new NotFoundException(
                        "Item with id " + bookingDto.getItemId() + " not exists in the DB"));

        Booking booking = BookingMapper.fromBookingDto(bookingDto);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);
        booking.setBooker(booker);
        if (booking.getStartDateTime().equals(booking.getEndDateTime()))
        {
            booking.setEndDateTime(booking.getEndDateTime().plusSeconds(1));
        }
       return BookingMapper.toBookingDto(bookingRepository.save(booking));

    }


}