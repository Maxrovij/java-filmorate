package ru.yandex.practicum.filmorate.storage.user;


import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.UserDto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {
    private Map<Integer, User> users = new HashMap<>();

    // метод для получения списка добавленных пользователей
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    // метод обработки POST запросов для добавления нового пользователя
    public void addUser(UserDto userDto) {
        if (validateUser(userDto)) {
            for (User user : users.values()) {
                if (user.getEmail().equals(userDto.getEmail()) || user.getLogin().equals(userDto.getLogin())) {
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
        } else {
            throw new ValidationException("Введенные данные не соответствуют требованиям валидации.");
        }
    }

    // метод обработки PUT запросов для редактирования пользователя

    public void editUser(UserDto userDto) {
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
            } else {
                throw new ValidationException("Введенные данные не соответствуют требованиям валидации.");
            }
        } else {
            throw new ValidationException("Неверный ID");
        }
    }

    private boolean validateUser(UserDto userDto) {
        LocalDate userBirthDay = LocalDate.parse(userDto.getBirthday());
        if (userBirthDay.isAfter(LocalDate.now())) {
            return false;
        }
        if (userDto.getLogin().isBlank() || userDto.getLogin().isEmpty()) {
            return false;
        }
        if (userDto.getLogin().contains(" ")) {
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
