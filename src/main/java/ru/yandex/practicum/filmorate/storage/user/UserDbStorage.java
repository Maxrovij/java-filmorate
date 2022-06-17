package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dao.FriendshipTableDao;
import ru.yandex.practicum.filmorate.storage.dao.UserDao;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component("userDbStorage")
public class UserDbStorage implements UserStorage {

    private final UserDao userDao;
    private final FriendshipTableDao friendshipTableDao;

    @Autowired
    public UserDbStorage(UserDao userDao, FriendshipTableDao friendshipTableDao) {
        this.userDao = userDao;
        this.friendshipTableDao = friendshipTableDao;
    }

    @Override
    public List<User> getAllUsers() {
        return userDao.getAllUsers();
    }

    @Override
    public User addUser(User user) {
        return userDao.addUser(user);
    }

    @Override
    public Optional<User> findUserById(Long id) {
        return userDao.findUserById(id);
    }

    @Override
    public User updateUser(User user) {
        return userDao.updateUser(user);
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        friendshipTableDao.addFriend(userId, friendId);
    }
    @Override
    public void deleteFriend(Long userId, Long friendId) {
        friendshipTableDao.deleteFriend(userId,friendId);
    }
    @Override
    public List<User> getUserFriends(Long userId) {
        List<Long> friendsIds = friendshipTableDao.getUserFriendsIds(userId);
        if (friendsIds.isEmpty()) {
            return List.of();
        } else {
            List<User> userFriends = new ArrayList<>();
            for (Long id : friendsIds) {
                userFriends.add(userDao.findUserById(id).get());
            }
            return userFriends;
        }
    }
}
