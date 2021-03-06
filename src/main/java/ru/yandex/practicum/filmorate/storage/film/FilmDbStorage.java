package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.DataNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Component("filmDbStorage")
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Film> getAllFilms() {
        return jdbcTemplate.query(" select * from FILMS", (rs, rowNum) -> makeFilm(rs));
    }

    @Override
    public Film addFilm(Film film) {
        String sql = "INSERT INTO FILMS(ID, NAME, DESCRIPTION, RELEASE_DATE, DURATION, RATE, MPA_RATING_ID)" +
                "values (?,?,?,?,?,?,?)";
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

    @Override
    public Film editFilm(Film film) {
        String sql = "merge into FILMS key (ID) values (?,?,?,?,?,?,?)";
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

    @Override
    public Optional<Film> findFilmById(Long id) {
        String sql = "select * from FILMS where ID=?";
        String genreSql = "select * from FILM_GENRE where FILM_ID=?";
        Film film;
        List<Genre> genres;
        try {
            film = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> makeFilm(rs), id);
        } catch (DataAccessException e) {
            return Optional.empty();
        }
        try {
            genres = jdbcTemplate.query(
                    genreSql,
                    (rs, rowNum) -> getGenreById(rs.getInt("genre_id")),
                    id).stream().sorted(Comparator.comparingLong(Genre::getId)).collect(Collectors.toList());
            assert film != null;
            if (!genres.isEmpty()) {
                HashSet<Genre> g = new HashSet<>();
                g.addAll(genres);
                film.setGenres(g);
            } else {
                film.setGenres(null);
            }
        } catch (DataAccessException e) {
            assert film != null;
            film.setGenres(new HashSet<>());
        }
        return Optional.of(film);
    }

    @Override
    public Collection<Film> getPopular(Integer count) {
        String sql = "select * from FILMS order by RATE desc limit ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs), count);
    }

    @Override
    public void putLike(Long filmId, Long userId) {
        try {
            jdbcTemplate.queryForObject(
                    "select USER_ID from FILM_LIKES where FILM_ID=? and USER_ID=?",
                    Long.class,
                    filmId,
                    userId
            );
            throw new ValidationException("???????? ???????????????????????? ?????? ???????????????? ???????? ?????????? ????????????");
        } catch (IncorrectResultSizeDataAccessException e) {
            jdbcTemplate.update("insert into FILM_LIKES(FILM_ID, USER_ID) VALUES (?,?)", filmId, userId);
            jdbcTemplate.update("update FILMS set RATE=? where ID=?",
                    findFilmById(filmId).get().getRate() + 1, filmId);
        }
    }

    @Override
    public void deleteLike(Long filmId, Long userId) {
        String sql = "delete from FILM_LIKES where FILM_ID=? and USER_ID=?";
        jdbcTemplate.update(sql, filmId, userId);
        jdbcTemplate.update("update FILMS set RATE=? where ID=?",
                findFilmById(filmId).get().getRate() - 1, filmId);
    }

    @Override
    public MpaRating getMpaRatingById(int id) {
        try {
            return jdbcTemplate.queryForObject(
                    "select * from MPA_RATING where ID=?",
                    (rs, rowNum) -> new MpaRating(rs.getInt("ID"), rs.getString("NAME")),
                    id);
        } catch (DataAccessException e) {
            throw new DataNotFoundException("???????????? ???????????????? ?????? ?? ???????? ????????????.");
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
                "select * from GENRES", (rs, rowNum) -> new Genre(rs.getInt("ID"),
                        rs.getString("NAME"))
        );
    }

    @Override
    public Genre getGenreById(int id) {
        try {
            return jdbcTemplate.queryForObject(
                    "select * from GENRES where ID=?",
                    (rs, rowNum) -> new Genre(rs.getInt("ID"), rs.getString("NAME")),
                    id
            );
        } catch (DataAccessException e) {
            throw new DataNotFoundException("?????????? ?? ?????????? ID ?????? ?? ????????.");
        }
    }

    private Film makeFilm(ResultSet rs) throws SQLException {
        return Film.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(rs.getLong("duration"))
                .rate(rs.getLong("rate"))
                .mpa(getMpaRatingById(rs.getInt("mpa_rating_id")))
                .build();
    }
}
