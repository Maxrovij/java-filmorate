package ru.yandex.practicum.filmorate.storage.dao.impl;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.dao.ServiceInfoDao;

import java.util.Collection;

@Repository
public class ServiceInfoDaoImpl implements ServiceInfoDao {

    private final JdbcTemplate jdbcTemplate;

    public ServiceInfoDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    @Override
    public MpaRating getMpaRatingById(int id) {
        try {
            return jdbcTemplate.queryForObject(
                    "select * from MPA_RATING where ID=?",
                    (rs, rowNum) -> new MpaRating(rs.getInt("ID"), rs.getString("NAME")),
                    id);
        } catch (DataAccessException e) {
            throw new DataNotFoundException("Такого рейтинга нет в базе данных.");
        }
    }

    @Override
    public Collection<MpaRating> getAllMpa() {
        return jdbcTemplate.query(
                "select * from MPA_RATING",
                (rs, rowNum) -> new MpaRating(rs.getInt("ID"), rs.getString("NAME"))
        );
    }

    @Override
    public Collection<Genre> getAllGenres() {
        return jdbcTemplate.query(
        "select * from GENRES",(rs, rowNum) -> new Genre(rs.getInt("ID"),
            rs.getString("NAME"))
        );
    }

    @Override
    public Genre getGenreById(int id) {
        try {
            return jdbcTemplate.queryForObject(
                    "select * from GENRES where ID=?" ,
                    (rs, rowNum) -> new Genre(rs.getInt("ID"), rs.getString("NAME")),
                    id
            );
        } catch (DataAccessException e) {
            throw new DataNotFoundException("Жанра с таким ID нет в базе.");
        }
    }
}
