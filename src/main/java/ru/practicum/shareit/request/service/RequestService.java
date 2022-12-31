package ru.practicum.shareit.request.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.model.BadRequestException;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RequestService {
    private final ItemRepository itemRepository;
    private final ItemRequestRepository itemRequestRepository;


    private final UserRepository userRepository;


    @Autowired
    public RequestService(UserRepository userRepository,
                          ItemRequestRepository itemRequestRepository,
                          ItemRepository itemRepository) {

        this.userRepository = userRepository;

        this.itemRequestRepository = itemRequestRepository;
        this.itemRepository = itemRepository;
    }


    public ItemRequestDto create(ItemRequestDto itemRequestDto, long userId) {

        if (itemRequestDto.getDescription() == null) {
            throw new BadRequestException("Description can't be null");
        }
        User owner = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(
                "User with id " + userId + " not exists in the DB"));
        ItemRequest itemRequest = ItemRequestMapper.fromItemRequestDto(itemRequestDto);
        itemRequest.setOwner(owner);

        return ItemRequestMapper.toItemRequestDto(itemRequestRepository.save(itemRequest));
    }

    public List<ItemRequestDto> getUserRequests(long userId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException(
                "User with id " + userId + " not exists in the DB"));
        List<ItemRequest> requests = itemRequestRepository.findByOwner_IdOrderByCreateDateTimeDesc(userId);
        return setItemsToRequests(requests);
    }

    public List<ItemRequestDto> getAllRequests(long userId, int from, int size) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException(
                "User with id " + userId + " not exists in the DB"));
        Pageable paging = PageRequest.of(from, size);
        List<ItemRequest> requests = itemRequestRepository.findByOwner_IdNotOrderByCreateDateTimeDesc(userId, paging);
        return setItemsToRequests(requests);
    }

    public ItemRequestDto getRequestById(long userId, long requestId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException(
                "User with id " + userId + " not exists in the DB"));
        ItemRequest request = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException(
                        "Request with id " + requestId + " not exists in the DB"));
        List<Item> items = itemRepository.findByItemRequest_IdIn(List.of(requestId));
        ItemRequestDto requestDto = ItemRequestMapper.toItemRequestDto(request);
        List<ItemDto> itemsDto = items.stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
        requestDto.setItems(itemsDto);
        return requestDto;
    }

    private List<ItemRequestDto> setItemsToRequests(List<ItemRequest> requests) {
        List<Long> requestsIds = requests.stream().map(ItemRequest::getId).collect(Collectors.toList());
        List<Item> items = itemRepository.findByItemRequest_IdIn(requestsIds);
        List<ItemRequestDto> result = new ArrayList<>();
        for (ItemRequest request : requests) {
            ItemRequestDto requestDto = ItemRequestMapper.toItemRequestDto(request);
            List<Item> requestItems = items.stream()
                    .filter(k -> Objects.equals(k.getItemRequest().getId(), request.getId()))
                    .collect(Collectors.toList());
            requestDto.setItems(requestItems
                    .stream()
                    .map(ItemMapper::toItemDto)
                    .collect(Collectors.toList()));
            result.add(requestDto);
        }
        return result;
    }
}