package ru.yandex.practicum.filmorate.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.UserDto;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

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
    public User addUser(@RequestBody UserDto userDto) {
        return userService.addUser(userDto);
    }

    // метод обработки PUT запросов для редактирования пользователя
    @PutMapping
    public User editUser(@RequestBody UserDto userDto) {
        return userService.editUser(userDto);
    }

    // метод получения пользователя по id
    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    // метод добавления нового друга к пользователю
    @PutMapping("{userId}/friends/{friendId}")
    public void addNewFriend(@PathVariable Long userId, @PathVariable Long friendId) {
        userService.addFriend(userId, friendId);
    }

    //метод удаления дружбы
    @DeleteMapping("{userId}/friends/{friendId}")
    public void deleteFriend(@PathVariable Long userId, @PathVariable Long friendId) {
        userService.deleteFriend(userId, friendId);
    }

    // метод нахождения друзей пользователя
    @GetMapping("{userId}/friends")
    public List<User> findFriends(@PathVariable Long userId) {
        return userService.getUserFriends(userId);
    }

    @GetMapping("{id}/friends/common/{otherId}")
    public List<User> findCommonFriends(@PathVariable Long id, @PathVariable Long otherId) {
        return userService.getCommonFriends(id, otherId);
    }
}