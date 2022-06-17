package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;

@Data
public class MpaRating {
    private int id;
    private String name;

    public MpaRating(int id) {
        this.id = id;
        this.name = getRating(id);
    }

    public MpaRating(){}

    private String getRating(int id) {
        return switch (id) {
            case 1 -> "G";
            case 2 -> "PG";
            case 3 -> "PG-13";
            case 4 -> "R";
            case 5 -> "NC-17";
            default -> "Рейтинг не определен или определен неверно";
        };
    }
}
