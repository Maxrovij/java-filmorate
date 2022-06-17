package ru.yandex.practicum.filmorate.storage.dao;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserDao {
    Optional<User> findUserById(Long id);
    User addUser(User user);

    List<User> getAllUsers();

    User updateUser(User user);
}
