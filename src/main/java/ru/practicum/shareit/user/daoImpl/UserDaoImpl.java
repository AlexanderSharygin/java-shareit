package ru.practicum.shareit.user.daoImpl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.dao.Dao;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class UserDaoImpl implements Dao<User> {
    private final HashMap<Long, User> users = new HashMap<>();
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
    public Optional<User> create(User user) {
        Optional<User> existedUser = getUserWithSameEmail(user);
        if (existedUser.isEmpty()) {
            user.setId(idCounter);
            users.put(idCounter, user);
            idCounter++;
            log.info("Добавлен пользователь с email {}", user.getEmail());
            return Optional.of(users.get(idCounter - 1));
        }
        return Optional.empty();

    }

    @Override
    public Optional<User> update(User user) {
        Optional<User> userWithSameEmail = getUserWithSameEmail(user);
        if (userWithSameEmail.isPresent()) {
            return Optional.empty();
        }
        User existedUser = users.values().stream()
                .filter(k -> k.getId().equals(user.getId()))
                .findFirst().get();
        if (user.getEmail() != null) {
            existedUser.setEmail(user.getEmail());
        }
        if (user.getName() != null) {
            existedUser.setName(user.getName());
        }
        log.info("Обновлен пользователь с email {}", user.getEmail());
        return Optional.of(users.get(user.getId()));
    }

    @Override
    public void remove(long id) {
        users.remove(id);
        log.info("Удален пользователь с id {}", id);
    }

    private Optional<User> getUserWithSameEmail(User user) {
        return users.values().stream()
                .filter(u -> u.getEmail().equals(user.getEmail()) && !u.getId().equals(user.getId()))
                .findFirst();
    }
}


