package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;

public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        return new ItemDto(item.getId(), item.getName(), item.getDescription(), item.getAvailable(),
                item.getOwner(), item.getRequest(), new ArrayList<>(), null, null);
    }

    public static Item fromItemDto(ItemDto itemDto) {
        return new Item(-1L, itemDto.getName(), itemDto.getDescription(), itemDto.getAvailable(),
                itemDto.getOwner(), itemDto.getRequest());
    }
}
