package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.model.BadRequestException;
import ru.practicum.shareit.exception.model.ConflictException;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;

    public List<UserDto> getAll() {
        return repository.findAll()
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    public UserDto getById(long id) {
        return repository.findById(id)
                .map(UserMapper::toUserDto)
                .orElseThrow(() -> new NotFoundException("User with id " + id + " not exists in the DB"));
    }

    public UserDto create(UserDto userDto) {
        if (userDto.getEmail() == null || userDto.getEmail().isBlank()) {
            throw new BadRequestException("Email can't be null");
        }
        if (userDto.getName() == null || userDto.getName().isBlank()) {
            throw new BadRequestException("Name can't be null");
        }
        User user = UserMapper.fromUserDto(userDto);

        try {
            return UserMapper.toUserDto(repository.save(user));
        } catch (DataIntegrityViolationException ex) {
            throw new ConflictException(ex.getCause().getCause().getMessage());
        }
    }

    public UserDto update(long id, UserDto userDto) {
        User existedUser = repository.findById(id).orElseThrow(() -> new NotFoundException(
                "User with id " + id + " not exists in the DB"));
        if (userDto.getEmail() == null || userDto.getEmail().isBlank()) {
            userDto.setEmail(existedUser.getEmail());
        }
        if (userDto.getName() == null || userDto.getName().isBlank()) {
            userDto.setName(existedUser.getName());
        }
        User user = UserMapper.fromUserDto(userDto);
        user.setId(id);

        try {
            return UserMapper.toUserDto(repository.save(user));
        } catch (DataIntegrityViolationException ex) {
            throw new ConflictException(ex.getCause().getCause().getMessage());
        }
    }

    public void delete(long id) {
        User user = repository.findById(id).orElseThrow(() -> new NotFoundException(
                "User with id " + id + " not exists in the DB"));
        repository.delete(user);
    }
}