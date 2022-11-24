package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class UserStorage {
    private final HashMap<Long, User> users = new HashMap<>();
    private long idCounter = 1;

    public List<User> findAll() {
        log.info("Текущее количество пользователей: {}", users.size());
        return new ArrayList<>(users.values());
    }

    public Optional<User> findById(long id) {
        Optional<User> user = Optional.ofNullable(users.get(id));
        if (user.isEmpty()) {
            return Optional.empty();
        }
        log.info("Найден пользователь с id: {}", user.get().getId());
        return user;
    }

    public Optional<User> add(User user) {
        if (users.values().stream()
                .filter(u -> u.getEmail().equals(user.getEmail()))
                .findFirst().isEmpty()) {
            user.setId(idCounter);
            users.put(idCounter, user);
            idCounter++;
            log.info("Добавлен пользователь с email {}", user.getEmail());
            return Optional.of(users.get(idCounter-1));
        }
        return Optional.empty();

    }

    public Optional<User> update(User user) {
        Optional<User> withSameEmail=users.values().stream().filter(u -> u.getEmail().equals(user.getEmail())).findFirst();
        if (withSameEmail.isEmpty()) {
            Optional<User> existedUser = users.values().stream().filter(k -> k.getId().equals(user.getId())).findFirst();
            if (user.getEmail() != null) {
                existedUser.get().setEmail(user.getEmail());
            }
            if (user.getName() != null) {
                existedUser.get().setName(user.getName());
            }
            log.info("Обновлен пользователь с email {}", user.getEmail());
            return Optional.of(users.get(user.getId()));
        }
        return Optional.empty();
    }

    public void remove(long id) {
        users.remove(id);
        log.info("Удален пользователь с id {}", id);
    }
}


