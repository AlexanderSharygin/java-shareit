package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.model.AlreadyExistException;
import ru.practicum.shareit.exception.model.NotExistException;
import ru.practicum.shareit.exception.model.ValidationException;
import ru.practicum.shareit.user.daoImpl.UserDaoImpl;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@Slf4j
public class UserService {

    private final UserDaoImpl userDao;

    @Autowired
    public UserService(UserDaoImpl userDao) {
        this.userDao = userDao;
    }


    public List<UserDto> findAll() {
        List<User> users = userDao.findAll();
        return users.stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    public UserDto findById(long id) {
        User user = userDao.findById(id)
                .orElseThrow(() -> new NotExistException("User with id " + id + " not exists in the DB"));
        return UserMapper.toUserDto(user);
    }

    public UserDto create(UserDto userDto) {
        if (userDto.getEmail() == null) {
            throw new ValidationException("Email can't be null");
        }
        User user = UserMapper.fromUserDto(userDto);
        Optional<User> addedUser = userDao.create(user);
        if (addedUser.isEmpty()) {
            throw new AlreadyExistException("User already exists in the DB");
        }
        return UserMapper.toUserDto(addedUser.get());
    }

    public void update(Long id, UserDto userDto) {
        User user = UserMapper.fromUserDto(userDto);
        user.setId(id);
        findById(id);
        Optional<User> updatedUser = userDao.update(user);
        if (updatedUser.isEmpty()) {
            throw new AlreadyExistException("User with the same email already exists in the DB");
        }
    }

    public void delete(long id) {
        findById(id);
        userDao.remove(id);
    }
}