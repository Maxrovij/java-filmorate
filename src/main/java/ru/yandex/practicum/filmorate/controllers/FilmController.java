package ru.yandex.practicum.filmorate.controllers;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmDto;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;

import java.util.List;

// класс обработки запросов к фильмам
@RestController
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService = new FilmService(new InMemoryFilmStorage());

    // обработка GET запроса для получения списка добавленных фильмов
    @GetMapping
    public List<Film> getAllFilms() {
        return filmService.getAllFilms();
    }

    // обработка POST запроса для добавления фильма
    @PostMapping
    public void addFilm(@RequestBody FilmDto filmDto) {
        filmService.addFilm(filmDto);
    }

    // обработка PUT запроса для редактирования фильма
    @PutMapping
    public void editFilm(@RequestBody FilmDto filmDto) {
        filmService.editFilm(filmDto);
    }

    @PutMapping("/{filmId}/like/{userId}")
    public void like(@PathVariable Integer filmId, @PathVariable Integer userId) {
        filmService.likeFilm(filmId, userId);
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    public void unlike(@PathVariable Integer filmId, @PathVariable Integer userId) {
        filmService.unlikeFilm(filmId, userId);
    }

    @GetMapping("/popular")
    public List<Film> getFilmsSortedByLikes(
            @RequestParam(name = "count", defaultValue = "10", required = false) Integer count) {
        return filmService.getPopular(count);
    }
}


