package ru.practicum.shareit.archive.dao;

import java.util.List;
import java.util.Optional;

public interface Dao<T> {

    List<T> findAll();

    Optional<T> findById(long id);

    Optional<T> findNewest();

    void create(T value);

    void update(T value);

    void remove(long id);

}
