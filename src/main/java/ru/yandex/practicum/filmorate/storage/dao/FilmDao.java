package ru.yandex.practicum.filmorate.storage.dao;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface FilmDao {
    List<Film> getAllFilms();

    Film addNewFilm(Film film);

    Film editFilm(Film film);

    Optional<Film> findFilmById(Long id);

    Collection<Film> getPopular(Integer count);

    void putLike(Long filmId, Long userId);

    void deleteLike(Long filmId, Long userId);
}
