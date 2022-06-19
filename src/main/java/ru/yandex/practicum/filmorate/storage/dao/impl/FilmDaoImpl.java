package ru.yandex.practicum.filmorate.storage.dao.impl;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.dao.FilmDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
public class FilmDaoImpl implements FilmDao {

    private final JdbcTemplate jdbcTemplate;

    public FilmDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Film> getAllFilms() {
        //language=H2
        String sql = """
                select * from FILMS""";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs));
    }

    public Film addNewFilm(Film film) {
        String sql = """
                INSERT INTO FILMS(ID, NAME, DESCRIPTION, RELEASE_DATE, DURATION, RATE, MPA_RATING_ID)
                values (?,?,?,?,?,?,?)""";
        jdbcTemplate.update(
                sql,
                film.getId(),
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getRate(),
                film.getMpa().getId()
        );

        if (film.getGenres() != null) {
            String genreSql = "insert into FILM_GENRE(film_id, genre_id) values (?,?)";
            for (Genre g : film.getGenres()) {
                jdbcTemplate.update(genreSql, film.getId(), g.getId());
            }
        }
        return findFilmById(film.getId()).get();
    }

    public Film editFilm(Film film) {
        String sql = """
                merge into FILMS key (ID) values (?,?,?,?,?,?,?)""";
        jdbcTemplate.update(
                sql,
                film.getId(),
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getRate(),
                film.getMpa().getId()
        );

        jdbcTemplate.update("delete from FILM_GENRE where FILM_ID=?", film.getId());
        if (film.getGenres() != null) {
            if (film.getGenres().isEmpty()) {
                return film;
            }
            String genreSql = "insert into FILM_GENRE(film_id, genre_id) values (?,?)";
            for (Genre g : film.getGenres()) {
                jdbcTemplate.update(genreSql, film.getId(), g.getId());
            }
        }
        return findFilmById(film.getId()).get();
    }

    public Optional<Film> findFilmById(Long id) {
        String sql = "select * from FILMS where ID=?";
        String genreSql = "select * from FILM_GENRE where FILM_ID=?";
        Film film;
        List<Genre> genres;
        try {
            film = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> makeFilm(rs) , id);
        } catch (DataAccessException e) {
            return Optional.empty();
        }
        try {
            genres = jdbcTemplate.query(
                    genreSql,
                    (rs, rowNum) -> new Genre(rs.getInt("genre_id")),
                    id)
            ;
            assert film != null;
            if (!genres.isEmpty()) {
                HashSet<Genre> g = new HashSet<>();
                g.addAll(genres);
                film.setGenres(g);
            } else {
                film.setGenres(null);
            }
        }catch (DataAccessException e) {
            assert film != null;
            film.setGenres(new HashSet<>());
        }
        return Optional.of(film);
    }

    public Collection<Film> getPopular(Integer count) {
        String sql = "select * from FILMS order by RATE desc limit ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs), count);
    }

    public void putLike(Long filmId, Long userId) {
        try {
            jdbcTemplate.queryForObject(
                    "select USER_ID from FILM_LIKES where FILM_ID=? and USER_ID=?",
                    Long.class,
                    filmId,
                    userId
            );
            throw new ValidationException("Этот пользователь уже поставил лайк этому фильму");
        } catch (IncorrectResultSizeDataAccessException e) {
            jdbcTemplate.update("insert into FILM_LIKES(FILM_ID, USER_ID) VALUES (?,?)", filmId, userId);
            jdbcTemplate.update("update FILMS set RATE=? where ID=?",
                    findFilmById(filmId).get().getRate()+1, filmId);
        }
    }

    public void deleteLike(Long filmId, Long userId) {
        String sql = "delete from FILM_LIKES where FILM_ID=? and USER_ID=?";
        jdbcTemplate.update(sql,filmId,userId);
        jdbcTemplate.update("update FILMS set RATE=? where ID=?",
                findFilmById(filmId).get().getRate()-1, filmId);
    }

    private Film makeFilm(ResultSet rs) throws SQLException {
        return Film.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(rs.getLong("duration"))
                .rate(rs.getLong("rate"))
                .mpa(new MpaRating(rs.getInt("mpa_rating_id")))
                .build();
    }
}
