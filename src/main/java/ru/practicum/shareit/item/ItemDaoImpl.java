package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.dao.Dao;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class ItemDaoImpl implements Dao<Item> {

    private final HashMap<Long, Item> items = new HashMap<>();
    private long idCounter = 1;


    @Override
    public List<Item> findAll() {
        return null;
    }

    @Override
    public Optional<Item> findById(long id) {
        return Optional.empty();
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
    public Optional<Item> update(Item value) {
        return Optional.empty();
    }

    @Override
    public void remove(long id) {

    }
}

