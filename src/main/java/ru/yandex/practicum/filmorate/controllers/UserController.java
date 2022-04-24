package ru.yandex.practicum.filmorate.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.UserDto;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.*;

// класс для обработки запросов к пользователям
@RestController
@RequestMapping("/users")
public class UserController {
    private final static Logger log = LoggerFactory.getLogger(UserController.class);
    private Map<Integer, User> users = new HashMap<>();

    // метод для получения списка добавленных пользователей
    @GetMapping
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    // метод обработки POST запросов для добавления нового пользователя
    @PostMapping
    public void addUser(@Valid @RequestBody UserDto userDto) {
        if (validateUser(userDto)) {
            for (User user : users.values()) {
                if (user.getEmail().equals(userDto.getEmail()) || user.getLogin().equals(userDto.getLogin())) {
                    log.debug("Имейл или логин уже уже используются.");
                    throw new ValidationException("Имейл или логин уже уже используются.");
                }
            }
            // если имя не указано, то используем логин
            String name;
            if(userDto.getName() == null || userDto.getName().isBlank()) {
                name = userDto.getLogin();
            } else {
                name = userDto.getName();
            }

            LocalDate userBirthDay = LocalDate.parse(userDto.getBirthday());
            User user = User.builder()
                    .id(generateID())
                    .email(userDto.getEmail())
                    .login(userDto.getLogin())
                    .name(name)
                    .birthday(userBirthDay)
                    .build();

            users.put(user.getId(), user);
            log.trace("Обработант POST запрос на /users");
        } else {
            throw new ValidationException("Введенные данные не соответствуют требованиям валидации.");
        }
    }

    // метод обработки PUT запросов для редактирования пользователя
    @PutMapping
    public void editUser(@Valid @RequestBody UserDto userDto) {
        if (userDto.getId() != null && users.containsKey(userDto.getId())) {
            if (validateUser(userDto)) {
                String name;
                if(userDto.getName().isBlank() || userDto.getName() == null) {
                    name = userDto.getLogin();
                } else {
                    name = userDto.getName();
                }
                LocalDate userBirthDay = LocalDate.parse(userDto.getBirthday());
                User user = User.builder()
                        .id(userDto.getId())
                        .email(userDto.getEmail())
                        .login(userDto.getLogin())
                        .name(name)
                        .birthday(userBirthDay)
                        .build();
                users.put(user.getId(), user);
                log.trace("Обработант PUT запрос на /users");
            } else {
                throw new ValidationException("Введенные данные не соответствуют требованиям валидации.");
            }
        } else {
            log.debug("Неверный ID");
            throw new ValidationException("Неверный ID");
        }
    }

    private boolean validateUser(UserDto userDto) {
        LocalDate userBirthDay = LocalDate.parse(userDto.getBirthday());
        if (userBirthDay.isAfter(LocalDate.now())) {
            log.debug("Валидация не пройдена. Дата рождения в будущем.");
            return false;
        }
        if (userDto.getLogin().isBlank() || userDto.getLogin().isEmpty()) {
            log.debug("Валидация не пройдена. Логин пустой.");
            return false;
        }
        if (userDto.getLogin().contains(" ")) {
            log.debug("Валидация не пройдена. Логин содержит пробелы.");
            return false;
        }
        return true;
    }

    private int generateID() {
        int id = 1;
        while (users.containsKey(id)){
            id++;
        }
        return id;
    }
}
