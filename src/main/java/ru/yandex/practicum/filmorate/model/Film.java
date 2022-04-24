package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import java.time.Duration;
import java.time.LocalDate;

// класс представляющий объект фильма
@Data
@Builder
public class Film {
    private final int id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Duration duration;
}
