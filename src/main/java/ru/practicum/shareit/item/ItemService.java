package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.model.AlreadyExistException;
import ru.practicum.shareit.exception.model.NotExistException;
import ru.practicum.shareit.exception.model.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ItemService {

    private final ItemDaoImpl itemDao;
    private final UserService userService;

    @Autowired
    public ItemService(ItemDaoImpl itemDao, UserService userService) {
        this.itemDao = itemDao;
        this.userService = userService;
    }

    public List<ItemDto> findAll() {
        List<Item> items = itemDao.findAll();

        return items.stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    public List<ItemDto> findAllForUser(long userId) {
        List<Item> items = itemDao.findAllForUser(userId);

        return items.stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    public ItemDto findById(long id) {
        Item item = itemDao.findById(id)
                .orElseThrow(() -> new NotExistException("Item with id " + id + " not exists in the DB"));

        return ItemMapper.toItemDto(item);
    }

    public List<ItemDto> findByNameOrDescription(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        List<Item> items = itemDao.findByNameOrDescription(text);

        return items.stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    public ItemDto create(ItemDto itemDto) {
        if (itemDto.getAvailable() == null) {
            throw new ValidationException("Available flag can't be null");
        }
        if (itemDto.getName() == null || itemDto.getName().isBlank()) {
            throw new ValidationException("Name can't be empty or null");
        }
        if (itemDto.getDescription() == null) {
            throw new ValidationException("Name can't be null");
        }
        Item item = ItemMapper.fromItemDto(itemDto);
        Optional<Item> addedItem = itemDao.create(item);
        if (addedItem.isEmpty()) {
            throw new AlreadyExistException("User already exists in the DB");
        }

        return ItemMapper.toItemDto(addedItem.get());
    }

    public void update(long itemId, long userId, ItemDto itemDto) {
        ItemDto originalItem = findById(itemId);
        if (!originalItem.getOwner().getId().equals(userId)) {
            throw new NotExistException("Wrong owner is specified for the item");
        }
        User owner = UserMapper.fromUserDto(userService.findById(userId));
        owner.setId(userId);
        itemDto.setOwner(owner);
        Item item = ItemMapper.fromItemDto(itemDto);
        item.setId(itemId);
        Optional<Item> updatedItem = itemDao.update(item);
        if (updatedItem.isEmpty()) {
            throw new AlreadyExistException("Item with is not exist in the DB");
        }
    }

    public void remove(long itemId) {
        findById(itemId);
        itemDao.remove(itemId);
    }
}
