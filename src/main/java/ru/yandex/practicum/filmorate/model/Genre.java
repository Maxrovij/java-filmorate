package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class Genre {
    private int id;
    private String name;

    public Genre(){}

    public Genre(int id) {
        this.id = id;
        this.name = getGenre(id);
    }

    private String getGenre(int id) {
        return switch (id) {
            case 1 -> "Комедия";
            case 2 -> "Драма";
            case 3 -> "Мультфильм";
            case 4 -> "Ужасы";
            case 5 -> "Детектив";
            default -> "В таком жанре еще не снимают...";
        };
    }
}
