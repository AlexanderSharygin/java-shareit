package ru.practicum.shareit.request.mapper;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;

public class ItemRequestMapper {
    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return new ItemRequestDto(
                itemRequest.getId(),
                itemRequest.getOwner(),
                itemRequest.getDescription(),
                LocalDateTime.ofInstant(itemRequest.getCreateDateTime(), ZoneId.of("UTC")),
                new ArrayList<>());
    }

    public static ItemRequest fromItemRequestDto(ItemRequestDto itemRequestDto) {
        return new ItemRequest(
                -1L,
                null,
                itemRequestDto.getDescription(),
                LocalDateTime.now().toInstant(ZoneOffset.UTC));
    }
}
