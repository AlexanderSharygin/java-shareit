package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingInfo;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.model.BadRequestException;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j

public class ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    @Autowired
    public ItemService(ItemRepository itemRepository, UserRepository userRepository, BookingRepository bookingRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
    }

    public List<ItemDto> getAll(long userId) {
        List<Item> items = itemRepository.findAll();
        List<ItemDto> itemsDtos = new ArrayList<>();
        for (Item item : items) {
            ItemDto itemDto = ItemMapper.toItemDto(item);
            List<Long> ids = List.of(itemDto.getId());
            List<Booking> itemFutureBookings = bookingRepository.findDistinctByItem_IdInAndStartDateTimeAfterOrderByStartDateTimeDesc(ids, Instant.now());
            List<Booking> itemPastBookings = bookingRepository.findDistinctByItem_IdInAndEndDateTimeBeforeOrderByStartDateTimeDesc(ids, Instant.now());

            if (itemDto.getOwner().getId() == userId) {
                if (!itemFutureBookings.isEmpty()) {
                    itemDto.setNextBooking(new BookingInfo(itemFutureBookings.get(0).getId(),
                            LocalDateTime.ofInstant(itemFutureBookings.get(0).getStartDateTime(), ZoneId.of("UTC")),
                            itemFutureBookings.get(0).getBooker().getId()));
                } else {
                    itemDto.setNextBooking(null);
                }
                if (!itemPastBookings.isEmpty()) {
                    itemDto.setLastBooking(new BookingInfo(itemPastBookings.get(itemPastBookings.size() - 1).getId(),
                            LocalDateTime.ofInstant(itemFutureBookings.get(
                                    itemPastBookings.size() - 1).getStartDateTime(), ZoneId.of("UTC")),
                            itemFutureBookings.get(itemFutureBookings.size() - 1).getBooker().getId()));
                } else {
                    itemDto.setLastBooking(null);
                }
                itemsDtos.add(0, itemDto);
            } else {
                itemDto.setLastBooking(null);
                itemDto.setNextBooking(null);
                itemsDtos.add(itemDto);
            }

        }

        return itemsDtos;
    }

    public List<ItemDto> getAllForUser(long userId) {
        List<Item> items = itemRepository.findAllByOwnerId(userId);

        List<ItemDto> itemsDtos = new ArrayList<>();
        for (Item item : items) {
            ItemDto itemDto = ItemMapper.toItemDto(item);
            List<Long> ids = List.of(itemDto.getId());
            List<Booking> itemFutureBookings = bookingRepository.findDistinctByItem_IdInAndStartDateTimeAfterOrderByStartDateTimeDesc(ids, Instant.now());
            List<Booking> itemPastBookings = bookingRepository.findDistinctByItem_IdInAndEndDateTimeBeforeOrderByStartDateTimeDesc(ids, Instant.now());
            if (itemDto.getOwner().getId() == userId) {
                if (!itemFutureBookings.isEmpty()) {
                    itemDto.setNextBooking(new BookingInfo(itemFutureBookings.get(0).getId(),
                            LocalDateTime.ofInstant(itemFutureBookings.get(0).getStartDateTime(), ZoneId.of("UTC")),
                            itemFutureBookings.get(0).getBooker().getId()));
                } else {
                    itemDto.setNextBooking(null);
                }
                if (!itemPastBookings.isEmpty()) {
                    itemDto.setLastBooking(new BookingInfo(itemPastBookings.get(
                            itemPastBookings.size() - 1).getId(),
                            LocalDateTime.ofInstant(itemFutureBookings.get(
                                    itemPastBookings.size() - 1).getStartDateTime(), ZoneId.of("UTC")),
                            itemFutureBookings.get(itemFutureBookings.size() - 1).getBooker().getId()));
                } else {
                    itemDto.setLastBooking(null);
                }
                if (itemDto.getNextBooking() == null && itemDto.getLastBooking() == null) {
                    itemsDtos.add(itemDto);
                } else {
                    itemsDtos.add(0, itemDto);
                }
            } else {
                itemDto.setLastBooking(null);
                itemDto.setNextBooking(null);
                itemsDtos.add(itemDto);
            }

        }
        return itemsDtos;
    }

    public ItemDto getById(long id, long userId) {
        ItemDto itemDto = itemRepository.findById(id).map(ItemMapper::toItemDto)
                .orElseThrow(() -> new NotFoundException("Item with id " + id + " not exists in the DB"));
        List<Long> ids = List.of(itemDto.getId());
        List<Booking> itemFutureBookings = bookingRepository.findDistinctByItem_IdInAndStartDateTimeAfterOrderByStartDateTimeDesc(ids, Instant.now());
        List<Booking> itemPastBookings = bookingRepository.findDistinctByItem_IdInAndEndDateTimeBeforeOrderByStartDateTimeDesc(ids, Instant.now());

        if (itemDto.getOwner().getId() == userId) {
            if (!itemFutureBookings.isEmpty()) {
                itemDto.setNextBooking(new BookingInfo(itemFutureBookings.get(0).getId(),
                        LocalDateTime.ofInstant(itemFutureBookings.get(0).getStartDateTime(), ZoneId.of("UTC")),
                        itemFutureBookings.get(0).getBooker().getId()));
            } else {
                itemDto.setNextBooking(null);
            }
            if (!itemPastBookings.isEmpty()) {
                itemDto.setLastBooking(new BookingInfo(itemPastBookings.get(itemPastBookings.size() - 1).getId(),
                        LocalDateTime.ofInstant(itemFutureBookings.get(
                                itemPastBookings.size() - 1).getStartDateTime(), ZoneId.of("UTC")),
                        itemFutureBookings.get(itemFutureBookings.size() - 1).getBooker().getId()));
            } else {
                itemDto.setLastBooking(null);
            }
        } else {
            itemDto.setLastBooking(null);
            itemDto.setNextBooking(null);
        }
        return itemDto;
    }

    public List<ItemDto> getByNameOrDescription(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        List<Item> items = itemRepository
                .findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailableIsTrue(text, text);
        return items.stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }


    public ItemDto create(long userId, ItemDto itemDto) {
        if (itemDto.getAvailable() == null) {
            throw new BadRequestException("Available flag can't be null");
        }
        if (itemDto.getName() == null || itemDto.getName().isBlank()) {
            throw new BadRequestException("Name can't be empty or null");
        }
        if (itemDto.getDescription() == null) {
            throw new BadRequestException("Name can't be null");
        }
        User owner = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(
                "User with id " + userId + " not exists in the DB"));
        itemDto.setOwner(owner);
        Item item = ItemMapper.fromItemDto(itemDto);
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    public ItemDto update(long itemId, long userId, ItemDto itemDto) {
        Item existedItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item with id " + itemId + " not exists in the DB"));
        if (!existedItem.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Wrong owner is specified for the item");
        }
        if (itemDto.getAvailable() == null) {
            itemDto.setAvailable(existedItem.getAvailable());
        }
        if (itemDto.getName() == null || itemDto.getName().isBlank()) {
            itemDto.setName(existedItem.getName());
        }
        if (itemDto.getDescription() == null || itemDto.getDescription().isBlank()) {
            itemDto.setDescription(existedItem.getDescription());
        }

        User owner = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(
                "User with id " + userId + " not exists in the DB"));
        owner.setId(userId);
        itemDto.setOwner(owner);
        itemDto.setId(userId);
        Item item = ItemMapper.fromItemDto(itemDto);
        item.setId(itemId);
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    public void remove(long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item with id " + itemId + " not exists in the DB"));
        itemRepository.delete(item);
    }
}
