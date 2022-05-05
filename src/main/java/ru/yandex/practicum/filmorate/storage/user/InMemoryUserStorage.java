package ru.yandex.practicum.filmorate.storage.user;


import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();

    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    public void addUser(User user) {
        users.put(user.getId(), user);
    }

    public void editUser(User user) {
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
        } else {
            throw new ValidationException("Неверный ID");
        }
    }

    public User findUserById(Integer id) {
        if (users.containsKey(id)) return users.get(id);
        else throw new ValidationException("Неверный ID");
    }
}
