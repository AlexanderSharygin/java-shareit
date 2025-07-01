package ru.practicum.shareit.base;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class BaseInMemoryRepository<T> {

    @Getter
    private final Map<Long, T> entities = new HashMap<>();

    private long idCounter = 1;


    public List<T> findAll() {
        log.info("Текущее количество элементов: {}", entities.size());
        return new ArrayList<>(entities.values());
    }

    public Optional<T> findById(long id) {
        Optional<T> entity = Optional.ofNullable(entities.get(id));
        if (entity.isEmpty()) {
            return Optional.empty();
        }
        log.info("Найден элемент: {}", entity.get());
        return entity;
    }

    public void remove(long id) {
        entities.remove(id);
        log.info("Удален элемент с id {}", id);
    }

    public Long create(T entity) {
        entities.put(idCounter, entity);
        log.info("Добавлен элемент {}", entity);
        return idCounter++;
    }

    public void update(T entity) {
        log.info("Обновлен элемент {}", entity);
    }
}
