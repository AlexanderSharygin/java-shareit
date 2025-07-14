package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingInfo;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.model.BadRequestException;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.comment.repository.CommentsRepository;
import ru.practicum.shareit.item.repository.ItemsRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.time.LocalDateTime.now;
import static java.time.LocalDateTime.ofInstant;
import static java.time.ZoneOffset.UTC;

@Service
@Slf4j
public class ItemService {

    private final ItemsRepository itemsRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentsRepository commentsRepository;


    public ItemService(ItemsRepository itemsRepository, UserService userService, UserRepository userRepository, BookingRepository bookingRepository, CommentsRepository commentsRepository) {
        this.itemsRepository = itemsRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.commentsRepository = commentsRepository;
    }


    public List<ItemDto> getAll() {
        List<Item> items = itemsRepository.findAll();
        List<ItemDto> itemsDto = items.stream().map(ItemMapper::toItemDto).toList();
        setCommentsForItems(itemsDto);

        return itemsDto;
    }

    public List<ItemDto> getAllByUserId(long userId) {
        List<Item> items = itemsRepository.findAllByOwnerId(userId);
        List<ItemDto> itemsDto = items.stream().map(ItemMapper::toItemDto).toList();
        setCommentsForItems(itemsDto);

        return itemsDto;
    }

    public ItemDto getById(long id, long userId) {
        Item item = itemsRepository.findById(id)
                        .orElseThrow(() -> new NotFoundException("Item с id " + id + " не найден"));
        ItemDto result = setBookingInfo(List.of(item), userId).getFirst();
        setCommentsForItems(List.of(result));

        return result;
    }

    public List<ItemDto> getAllByNameOrDescription(String text) {
        return itemsRepository.findAllAvailableByNameLikeIgnoreCaseOrDescriptionLikeIgnoreCase(text, text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    public ItemDto create(long userId, ItemDto itemDto) {
        if (itemDto.getAvailable() == null) {
            throw new BadRequestException("Поле Available не может быть null");
        }
        if (itemDto.getName() == null || itemDto.getName().isBlank()) {
            throw new BadRequestException("Поле Name является обязательным и не может быть пустым");
        }
        if (itemDto.getDescription() == null) {
            throw new BadRequestException("Поле Description является обязательным");
        }
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User с " + userId + " не найден"));
        itemDto.setOwner(owner);
        Item item = ItemMapper.fromItemDto(itemDto);

        return ItemMapper.toItemDto(itemsRepository.save(item));
    }

    public ItemDto update(long itemId, long userId, ItemDto itemDto) {
        Item existedItem = itemsRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item с id " + itemId + " не найден"));
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User с " + userId + " не найден"));
        if (!existedItem.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Item может быть изменен только владельцем");
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

        owner.setId(userId);
        itemDto.setOwner(owner);
        //  itemDto.setId(userId);
        Item item = ItemMapper.fromItemDto(itemDto);
        item.setId(itemId);

        return ItemMapper.toItemDto(itemsRepository.save(item));
    }

    public void delete(long itemId) {
        Item item = itemsRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item с id " + itemId + " не найден"));
        itemsRepository.delete(item);
    }

    public CommentDto addComment(long itemId, long userId, CommentDto commentDto) {
        List<Booking> usersBookings = bookingRepository.findPastBookingsByBookerId(userId,
                now().toInstant(UTC));
        Optional<Booking> booking = usersBookings.stream().filter(k -> k.getItem().getId() == itemId)
                .findFirst();
        if (booking.isEmpty()) {
            throw new BadRequestException("User с id " + userId + " не может оставить отзыв для " +
                    "бронирования с id " + itemId);
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User с " + userId + " не найден"));
        Item item = itemsRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item с id " + itemId + " не найден"));

        Comment comment = CommentMapper.froCommentDto(commentDto);
        comment.setAuthor(user);
        comment.setItem(item);

        return CommentMapper.toCommentDto(commentsRepository.save(comment));
    }

    private void setCommentsForItems(List<ItemDto> items) {
        List<Long> itemIds = items.stream().map(ItemDto::getId).toList();
        List<Comment> comments = commentsRepository.findCommentsForItems(itemIds);
        for (ItemDto itemDto : items) {
            List<CommentDto> itemComments = comments.stream()
                    .filter(k -> k.getItem().getId().equals(itemDto.getId()))
                    .map(CommentMapper::toCommentDto).toList();
            itemDto.setComments(itemComments);
        }
    }

    private List<ItemDto> setBookingInfo(List<Item> items, long userId) {
        List<ItemDto> itemsDto = new ArrayList<>();
        List<Long> itemIds = items.stream().map(Item::getId).toList();
        List<Booking> futureBookings = bookingRepository.findFutureBookingsDistinctByItemsIdList(itemIds,
                now().toInstant(UTC));
        List<Booking> pastBookings = bookingRepository.findPastBookingsByItemsIdList(itemIds, now().toInstant(UTC));
        for (Item item : items) {
            ItemDto itemDto = ItemMapper.toItemDto(item);
            List<Booking> futureBookingsForItem = futureBookings.stream()
                    .filter(k -> k.getItem().getId().equals(item.getId())).toList();
            List<Booking> pastBookingsForItems = pastBookings.stream()
                    .filter(k -> k.getItem().getId().equals(item.getId())).toList();
            long itemOwnerId = itemDto.getOwner().getId();
            if (itemOwnerId != userId || (futureBookingsForItem.isEmpty() && pastBookingsForItems.isEmpty())) {
                itemsDto.add(itemDto);
                continue;
            }
            if (!futureBookingsForItem.isEmpty()) {
                Booking nextBooking = futureBookingsForItem.getFirst();
                itemDto.setNextBooking(new BookingInfo(
                        nextBooking.getId(),
                        ofInstant(nextBooking.getStartDateTime(), ZoneId.of("UTC")),
                        ofInstant(nextBooking.getEndDateTime(), ZoneId.of("UTC")),
                        nextBooking.getBooker().getId()));
            }
            if (!pastBookingsForItems.isEmpty()) {
                Booking lastBooking = pastBookingsForItems.getLast();
                itemDto.setLastBooking(new BookingInfo(
                        lastBooking.getId(),
                        ofInstant(lastBooking.getStartDateTime(), ZoneId.of(UTC.toString())),
                        ofInstant(lastBooking.getEndDateTime(), ZoneId.of("UTC")),
                        lastBooking.getBooker().getId()));
            }
            itemsDto.addFirst(itemDto);
        }
        return itemsDto;
    }
}