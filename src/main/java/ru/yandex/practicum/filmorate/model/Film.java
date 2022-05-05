package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.util.Set;

// класс представляющий объект фильма
@Data
@Builder
public class Film {
    private final int id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Long duration;
    private final Set<Integer> likes;

    public void like(Integer id) {
        likes.add(id);
    }
    public void unlike(Integer id) {
        likes.remove(id);
    }
}