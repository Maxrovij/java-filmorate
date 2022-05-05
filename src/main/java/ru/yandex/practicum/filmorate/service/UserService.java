package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.UserDto;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Service
public class UserService {
    private final UserStorage userStorage;
    private Integer latestId = 0;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User addUser(UserDto userDto) {
        if (validateUser(userDto)) {
            for (User user : userStorage.getAllUsers()) {
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
                    .friends(new HashSet<>())
                    .build();
            userStorage.addUser(user);
            return user;
        } else {
            throw new ValidationException("Введенные данные не соответствуют требованиям валидации.");
        }
    }

    public void editUser(UserDto userDto) {
        if (userDto.getId() != null && validateUser(userDto)) {
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
                    .friends(userStorage.findUserById(userDto.getId()).getFriends())
                    .build();
            userStorage.editUser(user);
        } else {
            throw new ValidationException("Введенные данные не соответствуют требованиям валидации.");
        }
    }

    public User getUserById(Integer id) {
        return userStorage.findUserById(id);
    }

    public void addFriend(Integer userId, Integer friendId) {
        if (userId <= 0 || friendId <= 0) {
            throw new ValidationException("Неверный ID");
        } else {
            User user = userStorage.findUserById(userId);
            user.addFriend(friendId);
            userStorage.editUser(user);

            User friend = userStorage.findUserById(friendId);
            friend.addFriend(userId);
            userStorage.editUser(friend);
        }
    }

    public void deleteFriend(Integer userId, Integer friendId) {
        if (userId <= 0 || friendId <= 0) {
            throw new ValidationException("Неверный ID");
        } else {
            User user = userStorage.findUserById(userId);
            user.removeFriend(friendId);
            userStorage.editUser(user);

            User friend = userStorage.findUserById(friendId);
            friend.removeFriend(userId);
            userStorage.editUser(friend);
        }
    }

    public List<User> getUserFriends (Integer userId) {
        List<User> userFriends = new ArrayList<>();
        for (Integer id : userStorage.findUserById(userId).getFriends()) {
            userFriends.add(userStorage.findUserById(id));
        }
        return userFriends;
    }

    public List<User> getCommonFriends(Integer userId, Integer otherId) {
        List<User> userFriends = new ArrayList<>();
        for (Integer id : userStorage.findUserById(userId).getFriends()) {
            userFriends.add(userStorage.findUserById(id));
        }

        List<User> otherUserFriends = new ArrayList<>();
        for (Integer id : userStorage.findUserById(otherId).getFriends()) {
            otherUserFriends.add(userStorage.findUserById(id));
        }
        userFriends.retainAll(otherUserFriends);
        return userFriends;
    }

    private boolean validateUser(UserDto userDto) {
        LocalDate userBirthDay = LocalDate.parse(userDto.getBirthday());
        if (userBirthDay.isAfter(LocalDate.now()))
            throw new IllegalArgumentException("Дата рождения не может быть в будущем.");
        if (userDto.getLogin().isBlank() || userDto.getLogin().isEmpty()) return false;
        return !userDto.getLogin().contains(" ");
    }

    private int generateID() {
        latestId++;
        return latestId;
    }
}
