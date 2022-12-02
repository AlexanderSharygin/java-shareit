package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.model.BadRequestException;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.user.daoImpl.UserDaoImpl;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {

    private final UserDaoImpl userDao;

    @Autowired
    public UserService(UserDaoImpl userDao) {
        this.userDao = userDao;
    }


    public List<UserDto> getAll() {
        List<User> users = userDao.findAll();
        return users.stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    public UserDto getById(long id) {
        return userDao.findById(id).map(UserMapper::toUserDto)
                .orElseThrow(() -> new NotFoundException("User with id " + id + " not exists in the DB"));
    }

    public UserDto getNewest() {
       return userDao.findNewest().map(UserMapper::toUserDto)
                .orElseThrow(() -> new NotFoundException("Something went wrong"));
    }


    public void create(UserDto userDto) {
        if (userDto.getEmail() == null || userDto.getEmail().isBlank()) {
            throw new BadRequestException("Email can't be null");
        }
        if (userDto.getName() == null || userDto.getName().isBlank()) {
            throw new BadRequestException("Email can't be null");
        }
        User user = UserMapper.fromUserDto(userDto);
        userDao.create(user);
    }

    public void update(long id, UserDto userDto) {
        User user = UserMapper.fromUserDto(userDto);
        user.setId(id);
        getById(id);
        userDao.update(user);
    }

    public void delete(long id) {
        getById(id);
        userDao.remove(id);
    }
}