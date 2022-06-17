package ru.yandex.practicum.filmorate.storage.dao.impl;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dao.UserDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class UserDaoImpl implements UserDao {
    private final JdbcTemplate jdbcTemplate;

    public UserDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<User> findUserById(Long id) {
        //language=H2
        String sql = """
                select *
                from USERS
                where ID=?""";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, (rs, rowNum) -> makeUser(rs) , id));
        } catch (DataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public User addUser(User user) {
        String sql = """
                insert into USERS(id, email, login, name, birthdate)
                VALUES (?,?,?,?,?)""";
        jdbcTemplate.update(sql, user.getId(), user.getEmail(), user.getLogin(),user.getName(), user.getBirthday());

        return jdbcTemplate.queryForObject(
                "select * from users where id = " + user.getId(),
                (rs, rowNum) -> makeUser(rs)
        );
    }

    @Override
    public List<User> getAllUsers() {
        String sql = """
                select *
                from USERS""";
        return jdbcTemplate.query(sql, (rs, rowNum)-> makeUser(rs));
    }

    @Override
    public User updateUser(User user) {
        //language=H2
        String sql = """
                update USERS
                set EMAIL=?, LOGIN=?, NAME=?, BIRTHDATE=?
                where ID=?""";
        jdbcTemplate.update(sql, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
        return user;
    }

    private User makeUser(ResultSet rs) throws SQLException {
        return User.builder()
                .id(rs.getLong("id"))
                .email(rs.getString("email"))
                .login(rs.getString("login"))
                .name(rs.getString("name"))
                .birthday(rs.getDate("birthdate").toLocalDate())
                .build();
    }
}
