package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.model.AlreadyExistException;
import ru.practicum.shareit.exception.model.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.daoImpl.UserDaoImpl;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

@Service
@Slf4j
public class ItemService {

    private final ItemDaoImpl itemDao;

    @Autowired
    public ItemService(ItemDaoImpl itemDao) {
        this.itemDao = itemDao;
    }


    public ItemDto create(ItemDto itemDto) {
        if (itemDto.getAvailable() == null) {
            throw new ValidationException("Available flag can't be null");
        }
        if (itemDto.getName() == null) {
            throw new ValidationException("Name can't be null");
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
}
