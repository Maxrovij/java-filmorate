package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.UserDto;

import java.util.List;

public interface UserStorage {

    List<User> getAllUsers();

    void addUser(UserDto userDto);

    void editUser(UserDto userDto);
}
