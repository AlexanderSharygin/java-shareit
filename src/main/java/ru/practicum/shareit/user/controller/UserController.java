package ru.practicum.shareit.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
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
        return userService.findAll();
    }

    @GetMapping("/users/{id}")
    public UserDto getUserById(@PathVariable("id") Long usersId) {
        return userService.findById(usersId);
    }

    @PostMapping(value = "/users")
    public UserDto create(@Valid @RequestBody UserDto userDto) {
        return userService.create(userDto);
    }

    @PatchMapping(value = "/users/{id}")
    public UserDto update(@PathVariable("id") Long usersId, @Valid @RequestBody UserDto userDto) {
        userService.update(usersId, userDto);
        return userService.findById(usersId);
    }

    @DeleteMapping(value = "/users/{id}")
    public void delete(@PathVariable("id") Long usersId) {
        userService.delete(usersId);
    }
}
