package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.DataNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final LocalDate CINEMA_BIRTH_DAY = LocalDate.of(1895, 12, 28);
    private Long latestId = 0L;

    private final FilmStorage filmStorage;
    private final UserService userService;

    @Autowired
    public FilmService(FilmStorage filmDbStorage, UserService userService) {
        this.filmStorage = filmDbStorage;
        this.userService = userService;
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film addFilm(FilmDto filmDto) {
        if(validateFilm(filmDto)) {
            LocalDate filmDtoReleaseDate = filmDto.getReleaseDate();
            String name = filmDto.getName();
            for (Film film : filmStorage.getAllFilms()) {
                if (film.getName().equals(filmDto.getName()) && film.getReleaseDate().equals(filmDtoReleaseDate)) {
                    throw new ValidationException("Такой фильм уже есть в базе.");
                }
                if (film.getName().equals(filmDto.getName())){
                    name = name + " (" + filmDtoReleaseDate.getYear() + ")";
                }
            }

            Film film = Film.builder()
                    .id(getNewId())
                    .name(name)
                    .description(filmDto.getDescription())
                    .releaseDate(filmDtoReleaseDate)
                    .duration(filmDto.getDuration())
                    .rate(filmDto.getRate())
                    .mpa(new MpaRating(filmDto.getMpa().getId()))
                    .genres(createGenreList(filmDto))
                    .build();

            return filmStorage.addFilm(film);
        } else return null;
    }

    public Film editFilm(FilmDto filmDto) {
        if (filmDto.getId() != null && validateFilm(filmDto)) {
            Optional<Film> maybeFilm = filmStorage.findFilmById(filmDto.getId());
            if (maybeFilm.isPresent()) {
                LocalDate filmDtoReleaseDay = filmDto.getReleaseDate();
                String name = filmDto.getName();
                for (Film film : filmStorage.getAllFilms()) {
                    if (film.getName().equals(name) && film.getReleaseDate().equals(filmDtoReleaseDay)) {
                        if (!film.getId().equals(maybeFilm.get().getId()))
                            throw new ValidationException("Такой фильм уже есть в базе.");
                    }
                }

                Film film = Film.builder()
                        .id(filmDto.getId())
                        .name(name)
                        .description(filmDto.getDescription())
                        .releaseDate(filmDtoReleaseDay)
                        .duration(filmDto.getDuration())
                        .rate(filmDto.getRate())
                        .mpa(new MpaRating(filmDto.getMpa().getId()))
                        .genres(createGenreList(filmDto))
                        .build();

                return filmStorage.editFilm(film);
            } else
                throw new DataNotFoundException("Фильм не найден.");
        } else {
            throw new ValidationException("Введенная информация не соответствует требованиям валидации.");
        }
    }

    public Film getFilmById(Long id) {
        Optional<Film> maybeFilm = filmStorage.findFilmById(id);
        if (maybeFilm.isPresent()) return maybeFilm.get();
        throw new DataNotFoundException("Фильм не найден.");
    }

    public List<Film> getPopular(Integer count) {
        return filmStorage.getPopular(count);
    }

    /*
    public void likeFilm(Long filmId, Long userId) {
        if (filmId <= 0 || userId <= 0) {
            throw new ValidationException("Невалидный ID");
        }
        Optional<Film> maybeFilm = filmStorage.findFilmById(filmId);
        User user = userService.getUserById(userId);
        if (maybeFilm.isPresent()) {
            Film film = maybeFilm.get();
            film.like(user.getId());
            filmStorage.addFilm(film);
        } else
            throw new DataNotFoundException("Фильм не найден.");
    } */

    /*public void unlikeFilm(Long filmId, Long userId) {
        if (filmId <= 0 || userId <= 0) {
            throw new ValidationException("Невалидный ID");
        }
        Optional<Film> maybeFilm = filmStorage.findFilmById(filmId);
        User user = userService.getUserById(userId);
        if (maybeFilm.isPresent()) {
            Film film = maybeFilm.get();
            film.unlike(user.getId());
            filmStorage.addFilm(film);
        } else
            throw new DataNotFoundException("Фильм не найден.");
    }*/

    private List<Genre> createGenreList(FilmDto filmDto) {
        if (filmDto.getGenres() == null) return null;
        if (filmDto.getGenres().isEmpty())return List.of();
        List<Genre> genres = new ArrayList<>();
        for (Genre g : filmDto.getGenres()) {
            genres.add(new Genre(g.getId()));
        }
        return genres;
    }

    private boolean validateFilm(FilmDto filmDto) {
        if (filmDto.getName().isBlank())
            throw new ValidationException("Название не может быть пустым.");
        if (filmDto.getDescription().length() > 200 || filmDto.getDescription().length() == 0)
            throw new ValidationException("Описание фильма слишком длинное или его вовсе нет.");
        if (filmDto.getDuration() <= 0)
            throw new ValidationException("Продолжительность фильма маловата.");
        if (filmDto.getReleaseDate().isBefore(CINEMA_BIRTH_DAY))
            throw new ValidationException("Дата выхода фильма раньше даты изобретения кино.");
        if (filmDto.getMpa() == null)
            throw new ValidationException("Не указан рейтинг MPA.");

        return true;
    }

    private Long getNewId() {
        latestId++;
        return latestId;
    }
}