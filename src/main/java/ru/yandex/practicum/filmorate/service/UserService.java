package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.DataNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.UserDto;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserStorage userStorage;
    private Long latestId = 0L;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User addUser(UserDto userDto) {
        String name = validateUser(userDto);

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
            userStorage.addUser(user);
            return user;
    }

    public void editUser(UserDto userDto) {

        if (userDto.getId() != null) {
            Optional<User> maybeUser = userStorage.findUserById(userDto.getId());
            if (maybeUser.isPresent()) {
                String name = validateUser(userDto);
                LocalDate userBirthDay = LocalDate.parse(userDto.getBirthday());
                User user = User.builder()
                        .id(userDto.getId())
                        .email(userDto.getEmail())
                        .login(userDto.getLogin())
                        .name(name)
                        .birthday(userBirthDay)
                        .build();
                User userToAddFriends = maybeUser.get();
                user.setFriends(userToAddFriends.getFriends());
                userStorage.addUser(user);
            } else
                throw new DataNotFoundException("Пользователь с таким ID не найден.");
        } else {
            throw new ValidationException("Не указан ID");
        }
    }

    public User getUserById(Long id) {
        Optional<User> maybeUser = userStorage.findUserById(id);
        if (maybeUser.isPresent()) return maybeUser.get();
        throw new ValidationException("Пользователь не найден.");
    }

    public void addFriend(Long userId, Long friendId) {
        if (userId <= 0 || friendId <= 0) {
            throw new ValidationException("Невалидный ID");
        }

        Optional<User> maybeUser = userStorage.findUserById(userId);
        Optional<User> maybeFriend = userStorage.findUserById(friendId);
        if (maybeUser.isPresent() && maybeFriend.isPresent()) {
            User user = maybeUser.get();
            user.addFriend(friendId);
            userStorage.addUser(user);

            User friend = maybeFriend.get();
            friend.addFriend(userId);
            userStorage.addUser(friend);
        } else
            throw new DataNotFoundException("Пользователь не найден.");
    }

    public void deleteFriend(Long userId, Long friendId) {
        if (userId <= 0 || friendId <= 0) {
            throw new ValidationException("Невалидный ID");
        }

        Optional<User> maybeUser = userStorage.findUserById(userId);
        Optional<User> maybeFriend = userStorage.findUserById(friendId);
        if (maybeUser.isPresent() && maybeFriend.isPresent()) {
            User user = maybeUser.get();
            user.removeFriend(friendId);
            userStorage.addUser(user);

            User friend = maybeFriend.get();
            friend.removeFriend(userId);
            userStorage.addUser(friend);
        } else
            throw new DataNotFoundException("Пользователь не найден.");
    }

    public List<User> getUserFriends (Long userId) {
        if (userId <=0) throw new ValidationException("Невалидный ID");
        List<User> userFriends = new ArrayList<>();
        Optional<User> maybeUser = userStorage.findUserById(userId);
        if (maybeUser.isPresent()) {
            User user = maybeUser.get();
            for (Long id : user.getFriends()) {
                userFriends.add(userStorage.findUserById(id).get());
            }
            return userFriends;
        } else
            throw new DataNotFoundException("Пользователь не найден.");
    }

    public List<User> getCommonFriends(Long userId, Long otherId) {
        if (userId <= 0 || otherId <= 0) {
            throw new ValidationException("Невалидный ID");
        }
        Optional<User> maybeUser = userStorage.findUserById(userId);
        Optional<User> maybeOtherUser = userStorage.findUserById(otherId);
        List<User> userFriends = new ArrayList<>();
        if (maybeUser.isPresent() && maybeOtherUser.isPresent()) {

            User user = maybeUser.get();
            for (Long id : user.getFriends()) {
                userFriends.add(userStorage.findUserById(id).get());
            }

            List<User> otherUserFriends = new ArrayList<>();
            User otherUser = maybeOtherUser.get();
            for (Long id : otherUser.getFriends()) {
                otherUserFriends.add(userStorage.findUserById(id).get());
            }
            userFriends.retainAll(otherUserFriends);
            return userFriends;
        }
        throw new DataNotFoundException("Пользователь не найден.");
    }

    private String validateUser(UserDto userDto) {
        LocalDate userBirthDay = LocalDate.parse(userDto.getBirthday());
        if (userBirthDay.isAfter(LocalDate.now()))
            throw new IllegalArgumentException("Дата рождения не может быть в будущем.");
        if (userDto.getLogin().isBlank())
            throw new ValidationException("Логин не может быть пустым.");
        if (userDto.getLogin().contains(" "))
            throw new ValidationException("Логин не может содержать пробелы.");
        String name;
        if(userDto.getName() == null || userDto.getName().isBlank()) {
            name = userDto.getLogin();
        } else {
            name = userDto.getName();
        }
        return name;
    }

    private Long generateID() {
        latestId++;
        return latestId;
    }
}
