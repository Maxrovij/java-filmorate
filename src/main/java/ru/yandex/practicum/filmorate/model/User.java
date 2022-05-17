package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

// класс представления пользователя
@Data
@Builder
public class User {
    private final Long id;
    private final String email;
    private final String login;
    private String name;
    private final LocalDate birthday;
    private final Map<Long, Boolean> friends = new HashMap<>();

    public void addFriendRequest(Long friendId) {
        friends.put(friendId, false);
    }

    public void confirmFriend(Long friendId) {
        friends.put(friendId, true);
    }

    public void removeFriend(Long friendId) {
        friends.remove(friendId);
    }

    public void setFriends(Map<Long, Boolean> friends) {
        this.friends.putAll(friends);
    }
}
