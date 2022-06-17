package ru.yandex.practicum.filmorate.storage.dao.impl;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.FriendshipTable;
import ru.yandex.practicum.filmorate.storage.dao.FriendshipTableDao;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Repository
public class FriendshipTableDaoImpl implements FriendshipTableDao {

    private final JdbcTemplate jdbcTemplate;

    public FriendshipTableDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        Optional<FriendshipTable> ft = getFriendshipTableOrNull(userId,friendId);
        if (ft.isEmpty()) {
            jdbcTemplate.update(
                    "insert into FRIENDSHIP_TABLE(first_user_id, second_user_id, status) VALUES (?,?,?)",
                    userId, friendId, false);
            return;
        }
        FriendshipTable friendshipTable = ft.get();
        if (friendshipTable.getFirstUserId().equals(friendId)) {
            if (!friendshipTable.getStatus()) {
                jdbcTemplate.update(
                        "update FRIENDSHIP_TABLE set STATUS=true where FIRST_USER_ID=? and SECOND_USER_ID=?",
                        friendId, userId);
            } else {
                throw new DuplicateKeyException("Already friends!");
            }
        } else {
            throw new DuplicateKeyException("Request already exists!");
        }
    }

    @Override
    public void deleteFriend(Long userId, Long friendId) {
        Optional<FriendshipTable> ft = getFriendshipTableOrNull(userId,friendId);
        if (ft.isEmpty()) {
            throw new DataNotFoundException("There is no such friendship!");
        }
        FriendshipTable friendshipTable = ft.get();
        jdbcTemplate.update(
                "delete from FRIENDSHIP_TABLE where FIRST_USER_ID=? and SECOND_USER_ID=?",
                friendshipTable.getFirstUserId(), friendshipTable.getSecondUserId());
    }

    @Override //нужны скобки в запросе!
    public List<Long> getUserFriendsIds(Long userId) {
        //language=H2
        String sql = """
                select * from FRIENDSHIP_TABLE where FIRST_USER_ID=? or SECOND_USER_ID=? and STATUS=true""";
        List<FriendshipTable> friendshipTables = jdbcTemplate.query(
                sql,
                (rs, rowNum)-> FriendshipTable.builder()
                        .firstUserId(rs.getLong("first_user_id"))
                        .secondUserId(rs.getLong("second_user_id"))
                        .status(rs.getBoolean("status")).build(),
                userId, userId);

        if (friendshipTables.isEmpty()) {
            return List.of();
        }

        List<Long> friendsIds = new ArrayList<>();

        for (FriendshipTable ft : friendshipTables) {
            if (ft.getFirstUserId().equals(userId)) friendsIds.add(ft.getSecondUserId());
            else friendsIds.add(ft.getFirstUserId());
        }
        return friendsIds;
    }

    private Optional<FriendshipTable> getFriendshipTableOrNull(Long userId, Long friendId) {
        //language=H2
        String sql = """
                select * from FRIENDSHIP_TABLE
                where FIRST_USER_ID in(?,?) and SECOND_USER_ID in (?,?)""";
        try {
            FriendshipTable ft = jdbcTemplate.queryForObject(sql,
                    (rs,rowNum)-> FriendshipTable.builder()
                            .firstUserId(rs.getLong("FIRST_USER_ID"))
                            .secondUserId(rs.getLong("SECOND_USER_ID"))
                            .status(rs.getBoolean("STATUS")).build(),
                    userId,friendId,userId,friendId);
            return Optional.ofNullable(ft);
        }catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}
