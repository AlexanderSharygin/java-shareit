package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class MappersTests {
    @Test
    public void userMapperToDtoTest() {
        User user = new User(1L, "Name", "Email@email.com");
        UserDto userDto = UserMapper.toUserDto(user);
        assertEquals(userDto.getEmail(), "Email@email.com");
        assertEquals(userDto.getId(), 1L);
        assertEquals(userDto.getName(), "Name");
    }

    @Test
    public void userMapperFromDtoTest() {
        UserDto userDto = new UserDto(1L, "Name", "Email@email.com");
        User user = UserMapper.fromUserDto(userDto);
        assertEquals(user.getEmail(), "Email@email.com");
        assertEquals(user.getId(), -1L);
        assertEquals(user.getName(), "Name");
    }

    @Test
    public void itemMapperToDtoTest() {
        User user = new User(1L, "Name", "Email@email.com");
        Item item = new Item(1L, "Name", "description", true, user, null);
        ItemDto itemDto = ItemMapper.toItemDto(item);
        assertEquals(itemDto.getName(), "Name");
        assertEquals(itemDto.getId(), 1L);
        assertEquals(itemDto.getDescription(), "description");
        assertEquals(itemDto.getAvailable(), true);
        assertEquals(itemDto.getOwner(), user);
        assertNull(itemDto.getLastBooking());
        assertNull(itemDto.getNextBooking());
        assertEquals(itemDto.getComments(), new ArrayList<>());
        assertNull(itemDto.getRequestId());
    }

    @Test
    public void itemMapperFromDtoTest() {
        User user = new User(1L, "Name", "Email@email.com");
        ItemDto itemDto = new ItemDto(1L, "Name", "Description", true, user, null, null, null, new ArrayList<>());
        Item item = ItemMapper.fromItemDto(itemDto);
        assertEquals(item.getName(), "Name");
        assertEquals(item.getId(), -1L);
        assertEquals(item.getDescription(), "Description");
        assertEquals(item.getAvailable(), true);
        assertEquals(item.getOwner(), user);
        assertNull(item.getItemRequest());
    }
}