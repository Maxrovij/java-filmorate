package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {

    List<Film> getAllFilms();

    Film addFilm(Film film);

    Film editFilm(Film film);

    Optional<Film> findFilmById(Long id);

    List<Film> getPopular(Integer count);
}
