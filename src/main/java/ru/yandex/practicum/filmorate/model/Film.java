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
    private String rating;
    private List<String> genre;
    private final Set<Long> likes = new HashSet<>();

    public void like(Long id) {
        likes.add(id);
    }
    public void unlike(Long id) {
        likes.remove(id);
    }
    public void setLikes(Set<Long> likes) {
        this.likes.addAll(likes);
    }

    public void setGenre(List<String> genre) {
        this.genre = genre;
    }
    public List<String> getGenre() {
        return genre;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }
    public String getRating() {
        return rating;
    }
}