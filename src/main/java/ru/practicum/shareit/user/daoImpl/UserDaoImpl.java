package ru.practicum.shareit.user.daoImpl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.dao.Dao;
import ru.practicum.shareit.exception.model.ConflictException;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Component
@Slf4j
public class UserDaoImpl implements Dao<User> {
    private final Map<Long, User> users = new HashMap<>();
    private long idCounter = 1;

    @Override
    public List<User> findAll() {
        log.info("Текущее количество пользователей: {}", users.size());
        return new ArrayList<>(users.values());
    }

    @Override
    public Optional<User> findById(long id) {
        Optional<User> user = Optional.ofNullable(users.get(id));
        if (user.isEmpty()) {
            return Optional.empty();
        }
        log.info("Найден пользователь с id: {}", user.get().getId());
        return user;
    }

    @Override
    public Optional<User> findNewest() {
        long newestId = new ArrayList<>(users.keySet()).get(users.keySet().size() - 1);
        return findById(newestId);
    }

    @Override
    public void create(User user) {
        Optional<User> existedUser = findUserWithSameEmail(user);
        if (existedUser.isPresent()) {
            throw new ConflictException("User with the same email already exists in the DB");
        }
        user.setId(idCounter);
        users.put(idCounter, user);
        idCounter++;
        log.info("Добавлен пользователь с email {}", user.getEmail());
    }

    @Override
    public void update(User user) {
        User existedUser = users.values().stream()
                .filter(k -> k.getId().equals(user.getId()))
                .findFirst().orElseThrow(() -> new NotFoundException("User is not exist in the DB"));
        Optional<User> userWithSameEmail = findUserWithSameEmail(user);
        if (userWithSameEmail.isPresent()) {
            throw new ConflictException("User with the same email already exists!");
        }
        if (user.getEmail() != null) {
            existedUser.setEmail(user.getEmail());
        }
        if (user.getName() != null) {
            existedUser.setName(user.getName());
        }
        log.info("Обновлен пользователь с email {}", user.getEmail());
    }

    @Override
    public void remove(long id) {
        users.remove(id);
        log.info("Удален пользователь с id {}", id);
    }

    private Optional<User> findUserWithSameEmail(User user) {
        return users.values().stream()
                .filter(u -> u.getEmail().equals(user.getEmail()) && !u.getId().equals(user.getId()))
                .findFirst();
    }
}


