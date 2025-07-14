package ru.practicum.shareit.user.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@RestController
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public List<UserDto> getUsers() {
        return userService.getAll();
    }

    @GetMapping("/users/{id}")
    public UserDto getUserById(@PathVariable("id") Long usersId) {
        return userService.getById(usersId);
    }

    @PostMapping(value = "/users")
    public UserDto create(@Valid @RequestBody UserDto userDto) {
        return userService.create(userDto);
    }

    @PatchMapping(value = "/users/{id}")
    public UserDto update(@PathVariable("id") long usersId, @Valid @RequestBody UserDto userDto) {
        return userService.update(usersId, userDto);
    }

    @DeleteMapping(value = "/users/{id}")
    public void delete(@PathVariable("id") long usersId) {
        userService.delete(usersId);
    }
}