package ru.yandex.practicum.filmorate;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.time.LocalDate;

public class TestFilms {
    public static Film film1 = Film.builder()
            .id(1L)
            .name("film1name")
            .description("film1description")
            .duration(1000L)
            .rate(2L)
            .releaseDate(LocalDate.of(2000, 1, 2))
            .mpa(new MpaRating(1, "G"))
            .build();

    public static Film film1update = Film.builder()
            .id(1L)
            .name("film1nameUpdated")
            .description("film1descriptionUpdated")
            .duration(2000L)
            .rate(5L)
            .releaseDate(LocalDate.of(2001, 2, 3))
            .mpa(new MpaRating(4, "R"))
            .build();

    public static Film film2 = Film.builder()
            .id(2L)
            .name("film2name")
            .description("film2description")
            .duration(3000L)
            .rate(2L)
            .releaseDate(LocalDate.of(1999, 2, 3))
            .mpa(new MpaRating(3, "PG-13"))
            .build();
}
