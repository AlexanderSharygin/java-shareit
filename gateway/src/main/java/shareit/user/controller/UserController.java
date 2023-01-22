package shareit.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ru.practicum.shareit.user.dto.UserDto;
import shareit.user.client.UsersClient;

import javax.validation.Valid;

@RestController
public class UserController {

    private final UsersClient usersClient;

    @Autowired
    public UserController(UsersClient userService) {
        this.usersClient = userService;
    }

    @GetMapping("/users")
    public ResponseEntity<Object> getUsers() {
        return usersClient.getAll();
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<Object> getUserById(@PathVariable("id") Long usersId) {
        return usersClient.getById(usersId);
    }

    @PostMapping(value = "/users")
    public ResponseEntity<Object> create(@Valid @RequestBody UserDto userDto) {
        return usersClient.create(userDto);
    }

    @PatchMapping(value = "/users/{id}")
    public ResponseEntity<Object> update(@PathVariable("id") long usersId, @Valid @RequestBody UserDto userDto) {
        return usersClient.update(usersId, userDto);
    }

    @DeleteMapping(value = "/users/{id}")
    public void delete(@PathVariable("id") long usersId) {
        usersClient.delete(usersId);
    }
}
