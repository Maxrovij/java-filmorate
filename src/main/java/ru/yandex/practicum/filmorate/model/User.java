package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

// класс представления пользователя
@Data
@Builder
public class User {
    private final int id;
    private final String email;
    private final String login;
    private String name;
    private final LocalDate birthday;
    private final Set<Integer> friends;

    public void addFriend(Integer friendId) {
        friends.add(friendId);
    }

    public void removeFriend(Integer friendId) {
        friends.remove(friendId);
    }
}
