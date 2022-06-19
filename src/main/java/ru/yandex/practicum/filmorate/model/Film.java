package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.util.*;

// класс представляющий объект фильма
@Data
@Builder
public class Film {
    private final Long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Long duration;
    private Long rate;
    private MpaRating mpa;
    private HashSet<Genre> genres;
}