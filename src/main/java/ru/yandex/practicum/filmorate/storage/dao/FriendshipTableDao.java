package ru.yandex.practicum.filmorate.storage.dao;

import java.util.List;

public interface FriendshipTableDao {
    void addFriend(Long userId, Long friendId);

    void deleteFriend(Long userId, Long friendId);

    List<Long> getUserFriendsIds(Long userId);
}
