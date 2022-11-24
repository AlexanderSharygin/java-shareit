package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */


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
    public User create(@Valid @RequestBody UserDto userDto) {
        return userService.add(userDto);
    }

    @PatchMapping(value = "/users/{id}")
    public User update(@PathVariable("id") Long usersId, @Valid @RequestBody UserDto userDto) {
        return userService.update(usersId,userDto);
    }
}
