package ru.yandex.practicum.filmorate.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.UserDto;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.*;

// класс для обработки запросов к пользователям
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // метод для получения списка добавленных пользователей
    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    // метод обработки POST запросов для добавления нового пользователя
    @PostMapping
    public User addUser(@Valid @RequestBody UserDto userDto) {
        return userService.addUser(userDto);
    }

    // метод обработки PUT запросов для редактирования пользователя
    @PutMapping
    public void editUser(@Valid @RequestBody UserDto userDto) {
        userService.editUser(userDto);
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @PutMapping("{userId}/friends/{friendId}")
    public void addNewFriend(@PathVariable Long userId, @PathVariable Long friendId) {
        userService.addFriendRequest(userId, friendId);
    }

    @DeleteMapping("{userId}/friends/{friendId}")
    public void deleteFriend(@PathVariable Long userId, @PathVariable Long friendId) {
        userService.deleteFriend(userId, friendId);
    }

    @GetMapping("{userId}/friends")
    public List<User> findFriends(@PathVariable Long userId) {
        return userService.getUserFriends(userId);
    }

    @GetMapping("{id}/friends/common/{otherId}")
    public List<User> findCommonFriends(@PathVariable Long id, @PathVariable Long otherId) {
        return userService.getCommonFriends(id, otherId);
    }
}