package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.AlreadyExistException;
import ru.practicum.shareit.exceptions.NotExistException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@Slf4j
public class UserService {

    private final UserStorage userDao;

    @Autowired
    public UserService(UserStorage userDao) {
        this.userDao = userDao;
    }


    public List<UserDto> findAll() {
       List<User> users =  userDao.findAll();
        return users.stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    public UserDto findById(long id) {
        User user = userDao.findById(id)
                .orElseThrow(() -> new NotExistException("User with id " + id + " not exists in the DB"));
        return UserMapper.toUserDto(user);
    }

    public User add(UserDto userDto) {
        if (userDto.getEmail() == null) {
            throw new ValidationException("Email can't be null");
        }
        User user = UserMapper.fromUserDto(userDto);
        Optional<User> added = userDao.add(user);
        if (added.isEmpty()) {
            throw new AlreadyExistException("User already exists in the DB");
        }
        return added.get();
    }

    public User update(Long id, UserDto userDto) {
        User user = UserMapper.fromUserDto(userDto);
        user.setId(id);
        findById(user.getId());
        Optional<User> updatedUser = userDao.update(user);
        if (updatedUser.isEmpty()) {
            throw new AlreadyExistException("User with the same email already exists in the DB");
        }
        return updatedUser.get();
    }

    public void delete(long id) {
        findById(id);
        userDao.remove(id);

    }
}
