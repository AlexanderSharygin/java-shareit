package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.dao.Dao;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ItemDaoImpl implements Dao<Item> {

    private final HashMap<Long, Item> items = new HashMap<>();
    private long idCounter = 1;


    @Override
    public List<Item> findAll() {
        return null;
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
        log.info("Найден предмнет с id: {}", item.get().getId());
        return item;
    }

    @Override
    public Optional<Item> create(Item item) {
        item.setId(idCounter);
        items.put(idCounter, item);
        idCounter++;
        log.info("Добавлена вещь с названием {}", item.getName());
        return Optional.of(items.get(idCounter - 1));
    }

    @Override
    public Optional<Item> update(Item item) {
        Item existedItem = items.values().stream()
                .filter(k -> k.getId().equals(item.getId()))
                .findFirst().get();
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
        return Optional.of(items.get(item.getId()));
    }

    @Override
    public void remove(long id) {

    }
}

