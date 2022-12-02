package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.model.BadRequestException;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.List;
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

    public List<ItemDto> getAll() {
        List<Item> items = itemDao.findAll();

        return items.stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    public List<ItemDto> getAllForUser(long userId) {
        List<Item> items = itemDao.findAllForUser(userId);

        return items.stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    public ItemDto getById(long id) {
        return itemDao.findById(id).map(ItemMapper::toItemDto)
                .orElseThrow(() -> new NotFoundException("Item with id " + id + " not exists in the DB"));
    }

    public List<ItemDto> getByNameOrDescription(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        List<Item> items = itemDao.findByNameOrDescription(text);

        return items.stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    public ItemDto getNewest() {
        return itemDao.findNewest().map(ItemMapper::toItemDto)
                .orElseThrow(() -> new NotFoundException("Something went wrong"));
    }

    public void create(ItemDto itemDto) {
        if (itemDto.getAvailable() == null) {
            throw new BadRequestException("Available flag can't be null");
        }
        if (itemDto.getName() == null || itemDto.getName().isBlank()) {
            throw new BadRequestException("Name can't be empty or null");
        }
        if (itemDto.getDescription() == null) {
            throw new BadRequestException("Name can't be null");
        }
        Item item = ItemMapper.fromItemDto(itemDto);
        itemDao.create(item);
    }

    public void update(long itemId, long userId, ItemDto itemDto) {
        ItemDto originalItem = getById(itemId);
        if (!originalItem.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Wrong owner is specified for the item");
        }
        User owner = UserMapper.fromUserDto(userService.getById(userId));
        owner.setId(userId);
        itemDto.setOwner(owner);
        Item item = ItemMapper.fromItemDto(itemDto);
        item.setId(itemId);
        itemDao.update(item);
    }

    public void remove(long itemId) {
        getById(itemId);
        itemDao.remove(itemId);
    }
}
