package ru.yandex.practicum.filmorate.controllers;

import lombok.Builder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmDto;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {
    private FilmController filmController = new FilmController();

    @Test
    void shouldAddFilm() throws ValidationException {
        FilmDto filmDto = new FilmDto(
                null,
                "The Matrix",
                "Film",
                90000L,
                "2000-01-01");
        filmController.addFilm(filmDto);
        Film film = filmController.getAllFilms().get(0);
        Assertions.assertTrue(film.getName().equals("The Matrix") && film.getId() == 1);
    }

    @Test
    void shouldEditFilm() throws ValidationException {
        FilmDto film = new FilmDto(
                null,
                "Die Hard",
                "Film Die Hard",
                90000L,
                "1995-01-01");
        filmController.addFilm(film);
        Film filmBefore = filmController.getAllFilms().get(0);
        Assertions.assertTrue(
                filmBefore.getDescription().equals("Film Die Hard") &&
                        filmBefore.getId() == 2);

        FilmDto filmEdited = new FilmDto(
                2,
                "Die Hard",
                "Yippee Ki Yay, motherfucker",
                90000L,
                "2000-01-01"
        );
        filmController.editFilm(filmEdited);
        Film filmAfter = filmController.getAllFilms().get(0);
        Assertions.assertTrue(
                filmAfter.getDescription().equals("Yippee Ki Yay, motherfucker") &&
                        filmAfter.getId() == 2);
    }

    @Test
    void shouldThrowExceptionIfReleaseDateIsBeforeCinemaBirthDate() {
        FilmDto filmDto = new FilmDto(
                null,
                "The Matrix",
                "Film",
                90000L,
                "1890-01-01");
        Assertions.assertThrows(ValidationException.class, () -> filmController.addFilm(filmDto));
    }

    @Test
    void shouldThrowExceptionIfThisFilmIsAlreadyExists() throws ValidationException {
        FilmDto matrix = new FilmDto(
                null,
                "The Matrix",
                "Film",
                90000L,
                "2000-01-01");
        filmController.addFilm(matrix);
        Assertions.assertThrows(ValidationException.class, () -> filmController.addFilm(matrix));
    }

    @Test
    void shouldThrowExceptionIfFilmNameIsEmpty() {
        FilmDto filmDto = new FilmDto(
                null,
                "",
                "Film",
                90000L,
                "2000-01-01");
        Assertions.assertThrows(ValidationException.class, () -> filmController.addFilm(filmDto));
    }

    @Test
    void shouldThrowExceptionIfFilmDurationEqualsZero() {
        FilmDto filmDto = new FilmDto(
                null,
                "Film",
                "Film",
                0L,
                "2000-01-01");
        Assertions.assertThrows(ValidationException.class, () -> filmController.addFilm(filmDto));
    }
}