package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.DataNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.UserDto;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;


import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserStorage userStorage;
    private Long latestId = 0L;

    @Autowired
    public UserService(UserStorage userDbStorage) {
        this.userStorage = userDbStorage;
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User addUser(UserDto userDto) {
        validateUser(userDto);
        String name;
        if(userDto.getName() == null || userDto.getName().isBlank()) {
            name = userDto.getLogin();
        } else {
            name = userDto.getName();
        }

            for (User user : userStorage.getAllUsers()) {
                if (user.getEmail().equals(userDto.getEmail()) || user.getLogin().equals(userDto.getLogin())) {
                    throw new ValidationException("Имейл или логин уже уже используются.");
                }
            }

            LocalDate userBirthDay = LocalDate.parse(userDto.getBirthday());
            User user = User.builder()
                    .id(generateID())
                    .email(userDto.getEmail())
                    .login(userDto.getLogin())
                    .name(name)
                    .birthday(userBirthDay)
                    .build();

            return userStorage.addUser(user);
    }

    public User editUser(UserDto userDto) {

        if (userDto.getId() != null) {
            Optional<User> maybeUser = userStorage.findUserById(userDto.getId());
            if (maybeUser.isPresent()) {
                validateUser(userDto);
                String name;
                if(userDto.getName() == null || userDto.getName().isBlank()) {
                    name = userDto.getLogin();
                } else {
                    name = userDto.getName();
                }
                if (!maybeUser.get().getEmail().equals(userDto.getEmail()) ||
                        !maybeUser.get().getLogin().equals(userDto.getLogin())) {
                    for (User user : userStorage.getAllUsers()) {
                        if (user.getEmail().equals(userDto.getEmail()) || user.getLogin().equals(userDto.getLogin())) {
                            if (!user.getId().equals(maybeUser.get().getId()))
                                throw new ValidationException("Имейл или логин уже используется.");
                        }
                    }
                }
                LocalDate userBirthDay = LocalDate.parse(userDto.getBirthday());
                User user = User.builder()
                        .id(userDto.getId())
                        .email(userDto.getEmail())
                        .login(userDto.getLogin())
                        .name(name)
                        .birthday(userBirthDay)
                        .build();
                return userStorage.updateUser(user);
            } else
                throw new DataNotFoundException("Пользователь с таким ID не найден.");
        } else {
            throw new ValidationException("Не указан ID");
        }
    }

    public User getUserById(Long id) {
        Optional<User> maybeUser = userStorage.findUserById(id);
        if (maybeUser.isPresent()) return maybeUser.get();
        throw new DataNotFoundException("Пользователь не найден.");
    }

    public void addFriend(Long userId, Long friendId) {
        if (userId <= 0 || friendId <= 0) {
            throw new DataNotFoundException("Невалидный ID");
        }
        userStorage.addFriend(userId, friendId);
    }

    public void deleteFriend(Long userId, Long friendId) {
        if (userId <= 0 || friendId <= 0) {
            throw new ValidationException("Невалидный ID");
        }
        userStorage.deleteFriend(userId,friendId);
    }

    public List<User> getUserFriends (Long userId) {
        if (userId <=0) throw new ValidationException("Невалидный ID");
        return userStorage.getUserFriends(userId);
    }

    public List<User> getCommonFriends(Long userId, Long otherId) {
        if (userId <= 0 || otherId <= 0) {
            throw new ValidationException("Невалидный ID");
        }
        Optional<User> maybeUser = userStorage.findUserById(userId);
        Optional<User> maybeOtherUser = userStorage.findUserById(otherId);
        if (maybeUser.isPresent() && maybeOtherUser.isPresent()) {
            List<User> userFriends = getUserFriends(userId);
            List<User> otherUserFriends = getUserFriends(otherId);
            if (!userFriends.isEmpty()) {
                userFriends.retainAll(otherUserFriends);
                return userFriends;
            }
            return List.of();
        }
        throw new DataNotFoundException("Пользователь не найден.");
    }

    private boolean validateUser(UserDto userDto) {
        if (userDto.getLogin().isBlank())
            throw new ValidationException("Логин не может быть пустым.");
        if (userDto.getLogin().contains(" "))
            throw new ValidationException("Логин не может содержать пробелы.");
        if (LocalDate.parse(userDto.getBirthday()).isAfter(LocalDate.now()))
            throw new ValidationException("Дата рождения не может быть в будущем.");
        return true;
    }

    private Long generateID() {
        latestId++;
        return latestId;
    }
}
