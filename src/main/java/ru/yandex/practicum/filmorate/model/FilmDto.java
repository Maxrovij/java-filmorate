package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class FilmDto {
    private final Long id;
    private final String name;
    private final String description;
    private final LocalDate releaseDate;
    private final Long duration;
    private final Long rate;
    private final MpaRating mpa;
    private final List<Genre> genres;
}
