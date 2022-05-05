package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmDto;

import java.util.List;

public interface FilmStorage {

    List<Film> getAllFilms();

    void addFilm(Film film);

    void editFilm(Film film);

    Film findFilmById(Integer id);
}