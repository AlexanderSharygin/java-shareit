package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.dao.Dao;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ItemDaoImpl implements Dao<Item> {

    private final Map<Long, Item> items = new HashMap<>();
    private long idCounter = 1;


    @Override
    public List<Item> findAll() {
        log.info("Текущее количество предметов: {}", items.size());
        return new ArrayList<>(items.values());
    }


    public List<Item> findAllForUser(long userId) {
        return items.values().stream().filter(k -> k.getOwner().getId() == userId).collect(Collectors.toList());
    }

    public List<Item> findByNameOrDescription(String text) {
        return items.values().stream()
                .filter(k -> (k.getDescription().toLowerCase().contains(text.toLowerCase()) ||
                                      k.getName().toLowerCase().contains(text.toLowerCase())) &&
                        k.getAvailable())
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Item> findById(long id) {
        Optional<Item> item = Optional.ofNullable(items.get(id));
        if (item.isEmpty()) {
            return Optional.empty();
        }
        log.info("Найден предмет с id: {}", item.get().getId());

        return item;
    }

    @Override
    public Optional<Item> findNewest() {
        long newestId = new ArrayList<>(items.keySet()).get(items.keySet().size() - 1);
        return findById(newestId);
    }

    @Override
    public void create(Item item) {
        item.setId(idCounter);
        items.put(idCounter, item);
        idCounter++;
        log.info("Добавлен предмет с названием {}", item.getName());
    }

    @Override
    public void update(Item item) {
        Item existedItem = items.values().stream()
                .filter(k -> k.getId().equals(item.getId()))
                .findFirst().orElseThrow(()->new NotFoundException("Item with id " + item.getId() + " is not found"));
        if (item.getName() != null) {
            existedItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            existedItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            existedItem.setAvailable(item.getAvailable());
        }
        log.info("Обновлен предмет с id {}", item.getId());
    }

    @Override
    public void remove(long id) {
        items.remove(id);
        log.info("Удален предмет с id {}", id);
    }
}

