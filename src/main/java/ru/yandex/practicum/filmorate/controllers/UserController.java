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
    public void addUser(@Valid @RequestBody UserDto userDto) throws ValidationException {
        LocalDate userBirthDay = LocalDate.parse(userDto.getBirthday());
        if (users.containsKey(UserIDStorage.getUserIDByEmail(userDto.getEmail()))) {
            log.debug("Валидация не пройдена. Имейл занят.");
            throw new ValidationException("Этот адрес электронной почты уже используется.");
        }
        if (userBirthDay.isAfter(LocalDate.now())) {
            log.debug("Валидация не пройдена. Дата рождения в будущем.");
            throw new ValidationException("Дата рождения не может быть в будущем.");
        }
        if (userDto.getLogin().contains(" ")) {
            log.debug("Валидация не пройдена. Логин содержит пробелы.");
            throw new ValidationException("Логин не может содержать пробелы.");
        }

        // если имя не указано, то используем логин
        String name;
        if(userDto.getName() == null || userDto.getName().isBlank()) {
            name = userDto.getLogin();
        } else {
            name = userDto.getName();
        }

        User user = User.builder()
                .id(UserIDStorage.generateID(userDto.getEmail()))
                .email(userDto.getEmail())
                .login(userDto.getLogin())
                .name(name)
                .birthday(userBirthDay)
                .build();

        users.put(user.getId(), user);
        log.trace("Обработант POST запрос на /users");
    }

    // метод обработки PUT запросов для редактирования пользователя
    @PutMapping
    public void editUser(@Valid @RequestBody UserDto userDto) throws ValidationException {
        if (userDto.getId() != null && users.containsKey(userDto.getId())) {
            LocalDate userBirthDay = LocalDate.parse(userDto.getBirthday());
            if (userBirthDay.isAfter(LocalDate.now())) {
                log.debug("Валидация не пройдена. Дата рождения в будущем.");
                throw new ValidationException("Дата рождения не может быть в будущем.");
            }
            if (userDto.getLogin().contains(" ")) {
                log.debug("Валидация не пройдена. Логин содержит пробелы.");
                throw new ValidationException("Логин не может содержать пробелы.");
            }
            String name;
            if(userDto.getName().isBlank() || userDto.getName() == null) {
                name = userDto.getLogin();
            } else {
                name = userDto.getName();
            }
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
            log.debug("Валидация не пройдена. Неверный ID");
            throw new ValidationException("Неверный ID");
        }
    }
}
