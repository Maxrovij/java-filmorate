package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final HashMap<Integer, Film> films = new HashMap<>();

    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    public void addFilm(Film film) {
        films.put(film.getId(), film);
    }

    public void editFilm(Film film) {
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
        } else {
            throw new ValidationException("фильм с ID " + film.getId() + "отсутствует в базе.");
        }
    }

    public Film findFilmById(Integer id) {
        if (films.containsKey(id)) return films.get(id);
        else throw new ValidationException("фильм с ID " + id + "отсутствует в базе.");
    }

}
