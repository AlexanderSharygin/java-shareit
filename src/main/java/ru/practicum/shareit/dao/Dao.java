package ru.practicum.shareit.dao;

import java.util.List;
import java.util.Optional;

public interface Dao<T> {

    List<T> findAll();

    Optional<T> findById(long id);

    Optional<T> create(T value);

    Optional<T> update(T value);

    void remove(long id);

}
