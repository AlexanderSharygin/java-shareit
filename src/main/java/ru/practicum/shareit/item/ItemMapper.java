package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        return new ItemDto(item.getName(), item.getDescription(), item.getAvailable(), item.getOwner(), item.getRequest());
    }

    public static Item fromItemDto(long id, ItemDto itemDto) {
        return new Item(id, itemDto.getName(), itemDto.getDescription(), itemDto.getAvailable(), itemDto.getOwner(), itemDto.getRequest());
    }
}
