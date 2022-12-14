package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingInfo;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.model.BadRequestException;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
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

    private final CommentRepository commentRepository;

    @Autowired
    public ItemService(ItemRepository itemRepository, UserRepository userRepository, BookingRepository bookingRepository, CommentRepository commentRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
    }

    public List<ItemDto> getAll() {
        List<Item> items = itemRepository.findAll();
        List<ItemDto> result = items.stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
        for (ItemDto itemDto : result) {
            List<CommentDto> comments = getCommentsForItem(itemDto.getId()).stream()
                    .map(CommentMapper::toCommentDto).collect(Collectors.toList());
            itemDto.setComments(comments);
        }
        return result;
    }

    public List<ItemDto> getAllForUser(long userId) {
        List<Item> items = itemRepository.findAllByOwnerId(userId);
        List<ItemDto> result = setBookingInfo(items, userId);
        for (ItemDto itemDto : result) {
            List<CommentDto> comments = getCommentsForItem(itemDto.getId()).stream()
                    .map(CommentMapper::toCommentDto).collect(Collectors.toList());
            itemDto.setComments(comments);
        }
        return result;
    }

    public ItemDto getById(long id, long userId) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Item with id " + id + " not exists in the DB"));

        ItemDto itemDto = setBookingInfo(List.of(item), userId).get(0);
        List<CommentDto> comments = getCommentsForItem(itemDto.getId()).stream()
                .map(CommentMapper::toCommentDto).collect(Collectors.toList());
        itemDto.setComments(comments);
        return itemDto;
    }

    public List<ItemDto> getByNameOrDescription(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        List<Item> items = itemRepository
                .findAvailableItemsByNameOrDescription(text, text);

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

    public CommentDto addComment(long itemId, long userId, CommentDto commentDto) {
        List<Booking> usersBookings = bookingRepository.findPastBookingsByBookerId(userId, Instant.now());
        Booking booking = usersBookings
                .stream()
                .filter(k -> k.getItem().getId() == itemId)
                .findFirst()
                .orElse(null);
        if (booking == null) {
            throw new BadRequestException(
                    "User with id " + userId + " can't left the comment for booking with id " + itemId);
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id " + userId + " not exists in the DB"));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item with id " + itemId + " not exists in the DB"));
        Comment comment = CommentMapper.froCommentDto(commentDto);
        comment.setAuthor(user);
        comment.setItem(item);
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    private List<Comment> getCommentsForItem(Long itemId) {
        return commentRepository.findByItem_Id(itemId);
    }

    private List<ItemDto> setBookingInfo(List<Item> items, long userId) {
        List<ItemDto> itemsDto = new ArrayList<>();
        for (Item item : items) {
            ItemDto itemDto = ItemMapper.toItemDto(item);
            List<Long> ids = List.of(itemDto.getId());
            List<Booking> futureBookingsForItem = bookingRepository
                    .findFutureBookingsDistinctByItemsIdList(ids, Instant.now());
            List<Booking> pastBookingsForItems = bookingRepository
                    .findPastBookingsByItemsIdList(ids, Instant.now());
            long itemOwnerId = itemDto.getOwner().getId();
            if (itemOwnerId != userId || (futureBookingsForItem.isEmpty() && pastBookingsForItems.isEmpty())) {
                itemsDto.add(itemDto);
                continue;
            }
            if (!futureBookingsForItem.isEmpty()) {
                Booking nextBooking = futureBookingsForItem.get(0);
                itemDto.setNextBooking(new BookingInfo(
                        nextBooking.getId(),
                        LocalDateTime.ofInstant(nextBooking.getStartDateTime(), ZoneId.of("UTC")),
                        LocalDateTime.ofInstant(nextBooking.getEndDateTime(), ZoneId.of("UTC")),
                        nextBooking.getBooker().getId()));
            }
            if (!pastBookingsForItems.isEmpty()) {
                Booking lastBooking = pastBookingsForItems.get(pastBookingsForItems.size() - 1);
                itemDto.setLastBooking(new BookingInfo(
                        lastBooking.getId(),
                        LocalDateTime.ofInstant(lastBooking.getStartDateTime(), ZoneId.of("UTC")),
                        LocalDateTime.ofInstant(lastBooking.getEndDateTime(), ZoneId.of("UTC")),
                        lastBooking.getBooker().getId()));
            }
            itemsDto.add(0, itemDto);
        }
        return itemsDto;
    }
}
