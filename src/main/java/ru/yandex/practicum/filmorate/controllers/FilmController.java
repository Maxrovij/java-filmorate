package ru.yandex.practicum.filmorate.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmDto;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

// класс обработки запросов к фильмам
@RestController
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    // обработка GET запроса для получения списка добавленных фильмов
    @GetMapping
    public List<Film> getAllFilms() {
        return filmService.getAllFilms();
    }

    // обработка POST запроса для добавления фильма
    @PostMapping
    public Film addFilm(@RequestBody FilmDto filmDto) {
        return filmService.addFilm(filmDto);
    }

    // обработка PUT запроса для редактирования фильма
    @PutMapping
    public Film editFilm(@RequestBody FilmDto filmDto) {
        return filmService.editFilm(filmDto);
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable Integer id) {
        return filmService.getFilmById(id);
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


