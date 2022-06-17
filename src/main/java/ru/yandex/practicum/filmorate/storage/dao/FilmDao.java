package ru.yandex.practicum.filmorate.storage.dao;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmDao {
    List<Film> getAllFilms();

    Film addNewFilm(Film film);

    Film editFilm(Film film);

    Optional<Film> findFilmById(Long id);

    List<Film> getPopular(Integer count);
}
