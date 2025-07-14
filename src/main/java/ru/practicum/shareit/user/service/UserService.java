package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.model.BadRequestException;
import ru.practicum.shareit.exception.model.ConflictException;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository repository) {
        this.userRepository = repository;
    }

    public List<UserDto> getAll() {
        return userRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    public UserDto getById(long id) {
        return userRepository.findById(id)
                .map(UserMapper::toUserDto)
                .orElseThrow(() -> new NotFoundException("User с " + id + " не найден"));
    }

    public UserDto create(UserDto userDto) {
        if (userDto.getEmail() == null || userDto.getEmail().isBlank()) {
            throw new BadRequestException("Поле Email обязательное и не может быть пустым");
        }
        if (userDto.getName() == null || userDto.getName().isBlank()) {
            throw new BadRequestException("Поле Name обязательное и не может быть пустым");
        }
        User user = UserMapper.fromUserDto(userDto);

        try {
            return UserMapper.toUserDto(userRepository.save(user));
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Пользователь с указанным email уже существует");
        }
    }

    public UserDto update(long id, UserDto userDto) {
        User existedUser = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User с " + id + " не найден"));
        if (userDto.getEmail() == null || userDto.getEmail().isBlank()) {
            userDto.setEmail(existedUser.getEmail());
        }
        if (userDto.getName() == null || userDto.getName().isBlank()) {
            userDto.setName(existedUser.getName());
        }
        User user = UserMapper.fromUserDto(userDto);
        user.setId(id);
        try {
            return UserMapper.toUserDto(userRepository.save(user));
        } catch (DataIntegrityViolationException ex) {
            throw new ConflictException("Пользователь с указанным email уже существует");
        }
    }

    public void delete(long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User с " + id + " не найден"));
        userRepository.delete(user);
    }
}