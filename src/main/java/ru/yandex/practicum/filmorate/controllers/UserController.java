package ru.yandex.practicum.filmorate.controllers;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.UserDto;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import javax.validation.Valid;
import java.util.*;

// класс для обработки запросов к пользователям
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserStorage userStorage = new InMemoryUserStorage();

    // метод для получения списка добавленных пользователей
    @GetMapping
    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    // метод обработки POST запросов для добавления нового пользователя
    @PostMapping
    public void addUser(@Valid @RequestBody UserDto userDto) {
        userStorage.addUser(userDto);
    }

    // метод обработки PUT запросов для редактирования пользователя
    @PutMapping
    public void editUser(@Valid @RequestBody UserDto userDto) {
        userStorage.editUser(userDto);
    }
}
