package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import org.springframework.util.comparator.Comparators;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmDto;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.time.Duration;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final LocalDate cinemaBirthDay = LocalDate.of(1895, 12, 28);
    private Integer latestId = 0;

    private final FilmStorage filmStorage;

    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public void addFilm(FilmDto filmDto) {
        if(validateFilm(filmDto)) {
            LocalDate filmDtoReleaseDate = LocalDate.parse(filmDto.getReleaseDate());
            String name = filmDto.getName();
            for (Film film : filmStorage.getAllFilms()) {
                if (film.getName().equals(filmDto.getName()) && film.getReleaseDate().equals(filmDtoReleaseDate)) {
                    throw new ValidationException("Такой фильм уже есть в базе.");
                }
                if (film.getName().equals(filmDto.getName())){
                    name = filmDto.getName() + " (" + filmDtoReleaseDate.getYear() + ")";
                }
            }

            Film film = Film.builder()
                    .id(getNewId())
                    .name(name)
                    .description(filmDto.getDescription())
                    .releaseDate(filmDtoReleaseDate)
                    .duration(Duration.ofMillis(filmDto.getDuration()))
                    .likes(new HashSet<>())
                    .build();
            filmStorage.addFilm(film);
        } else {
            throw new ValidationException("Введенная информация о фильме не соответствует требованиям валидации.");
        }
    }

    public void editFilm(FilmDto filmDto) {
        if (filmDto.getId() != null && validateFilm(filmDto)) {
            LocalDate filmDtoReleaseDay = LocalDate.parse(filmDto.getReleaseDate());
            Film film = Film.builder()
                    .id(filmDto.getId())
                    .name(filmDto.getName())
                    .description(filmDto.getDescription())
                    .releaseDate(filmDtoReleaseDay)
                    .duration(Duration.ofMillis(filmDto.getDuration()))
                    .likes(filmStorage.findFilmById(filmDto.getId()).getLikes())
                    .build();
            filmStorage.editFilm(film);
        } else {
            throw new ValidationException("Введенная информация не соответствует требованиям валидации.");
        }
    }

    public void likeFilm(Integer filmId, Integer userId) {
        Film film = filmStorage.findFilmById(filmId);
        film.like(userId);
        filmStorage.editFilm(film);
    }

    public void unlikeFilm(Integer filmId, Integer userId) {
        Film film = filmStorage.findFilmById(filmId);
        film.unlike(userId);
        filmStorage.editFilm(film);
    }

    public List<Film> getPopular(Integer count) {
        return filmStorage.getAllFilms().stream()
                .sorted((f1, f2) -> -Integer.compare(f1.getLikes().size(), f2.getLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
    }

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
        return !filmDtoReleaseDay.isBefore(cinemaBirthDay);
    }

    private Integer getNewId() {
        latestId++;
        return latestId;
    }
}
