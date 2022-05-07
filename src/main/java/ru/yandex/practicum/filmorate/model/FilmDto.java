package ru.yandex.practicum.filmorate.model;

import lombok.Data;

// DTO для передачи информации о фильме
@Data
public class FilmDto {
    private final Long id;
    private final String name;
    private final String description;
    private final Long duration;
    private final String releaseDate;
}
