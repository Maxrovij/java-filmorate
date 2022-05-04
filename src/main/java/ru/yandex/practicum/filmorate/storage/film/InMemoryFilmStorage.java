package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmDto;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final LocalDate cinemaBirthDay = LocalDate.of(1895, 12, 28);
    private HashMap<Integer, Film> films = new HashMap<>();

    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    public void addFilm(FilmDto filmDto) {
        if(validateFilm(filmDto)) {
            LocalDate filmDtoReleaseDate = LocalDate.parse(filmDto.getReleaseDate());
            String name = filmDto.getName();
            for (Film film : films.values()) {
                if (film.getName().equals(filmDto.getName()) && film.getReleaseDate().equals(filmDtoReleaseDate)) {
                    throw new ValidationException("Такой фильм уже есть в базе.");
                } else {
                    name = filmDto.getName() + " (" + filmDtoReleaseDate.getYear() + ")";
                }
            }

            Film film = Film.builder()
                    .id(generateID())
                    .name(name)
                    .description(filmDto.getDescription())
                    .releaseDate(filmDtoReleaseDate)
                    .duration(Duration.ofMillis(filmDto.getDuration()))
                    .build();

            films.put(film.getId(), film);
        } else {
            throw new ValidationException("Введенная информация о фильме не соответствует требованиям валидации.");
        }
    }

    public void editFilm(FilmDto filmDto) {
        if (filmDto.getId() != null && validateFilm(filmDto)) {
            if (films.containsKey(filmDto.getId())) {
                LocalDate filmDtoReleaseDay = LocalDate.parse(filmDto.getReleaseDate());
                Film film = Film.builder()
                        .id(filmDto.getId())
                        .name(filmDto.getName())
                        .description(filmDto.getDescription())
                        .releaseDate(filmDtoReleaseDay)
                        .duration(Duration.ofMillis(filmDto.getDuration()))
                        .build();
                films.put(film.getId(), film);
            } else {
                throw new ValidationException("фильм с ID " + filmDto.getId() + "отсутствует в базе.");
            }
        } else {
            throw new ValidationException("Введенная информация не соответствует требованиям валидации.");
        }
    }

    // метод валидации фильма по полям
    private boolean validateFilm(FilmDto filmDto) {
        LocalDate filmDtoReleaseDay = LocalDate.parse(filmDto.getReleaseDate());
        if (filmDto.getName().isBlank()) {
            return false;
        }
        if (filmDto.getDescription().length() > 200 || filmDto.getDescription().length() == 0) {
            return false;
        }
        if (filmDto.getDuration() <= 0) {
            return false;
        }
        if (filmDtoReleaseDay.isBefore(cinemaBirthDay)) {
            return false;
        }
        return true;
    }

    // генератор Айди
    private int generateID() {
        int id = 1;
        while (films.containsKey(id)){
            id++;
        }
        return id;
    }
}
