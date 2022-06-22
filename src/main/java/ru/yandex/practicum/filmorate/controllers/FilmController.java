package ru.yandex.practicum.filmorate.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;
import java.util.List;

@RestController
public class FilmController {

    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    // обработка GET запроса для получения списка добавленных фильмов
    @GetMapping("/films")
    public List<Film> getAllFilms() {
        return filmService.getAllFilms();
    }

    // обработка POST запроса для добавления фильма
    @PostMapping("/films")
    public Film addFilm(@RequestBody Film film) {
        return filmService.addFilm(film);
    }

    // обработка PUT запроса для редактирования фильма
    @PutMapping("/films")
    public Film editFilm(@RequestBody Film film) {
        return filmService.editFilm(film);
    }

    @GetMapping("/films/{id}")
    public Film getFilmById(@PathVariable Long id) {
        return filmService.getFilmById(id);
    }

    @GetMapping("/films/popular")
    public Collection<Film> getFilmsSortedByLikes(
            @RequestParam(name = "count", defaultValue = "10", required = false) Integer count) {
        return filmService.getPopular(count);
    }

    @PutMapping("/films/{filmId}/like/{userId}")
    public void like(@PathVariable Long filmId, @PathVariable Long userId) {
        filmService.likeFilm(filmId, userId);
    }


    @DeleteMapping("/films/{filmId}/like/{userId}")
    public void unlike(@PathVariable Long filmId, @PathVariable Long userId) {
        filmService.unlikeFilm(filmId, userId);
    }

    @GetMapping("/mpa")
    public Collection<MpaRating> getAllMpa() {
        return filmService.getAllMpa();
    }

    @GetMapping("/mpa/{id}")
    public MpaRating getMpaById(@PathVariable int id) {
        return filmService.getMpaRatingById(id);
    }

    @GetMapping("/genres")
    public Collection<Genre> getAllGenres() {
        return filmService.getAllGenres();
    }

    @GetMapping("/genres/{id}")
    public Genre getGenreById(@PathVariable int id) {
        return filmService.getGenreById(id);
    }
}


