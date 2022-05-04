package ru.yandex.practicum.filmorate.controllers;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmDto;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;

import java.util.List;

// класс обработки запросов к фильмам
@RestController
@RequestMapping("/films")
public class FilmController {

    private final FilmStorage filmStorage = new InMemoryFilmStorage();

    // обработка GET запроса для получения списка добавленных фильмов
    @GetMapping
    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    // обработка POST запроса для добавления фильма
    @PostMapping
    public void addFilm(@RequestBody FilmDto filmDto) {
        filmStorage.addFilm(filmDto);
    }

    // обработка PUT запроса для редактирования фильма
    @PutMapping
    public void editFilm(@RequestBody FilmDto filmDto) {
        filmStorage.editFilm(filmDto);
    }
}


