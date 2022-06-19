package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface FilmStorage {

    List<Film> getAllFilms();

    Film addFilm(Film film);

    Film editFilm(Film film);

    Optional<Film> findFilmById(Long id);

    Collection<Film> getPopular(Integer count);

    void putLike(Long filmId, Long userId);

    void deleteLike(Long filmId, Long userId);

    MpaRating getMpaRatingById(int id);

    Collection<MpaRating> getAllMpa();

    Collection<Genre> getAllGenres();

    Genre getGenreById(int id);
}
