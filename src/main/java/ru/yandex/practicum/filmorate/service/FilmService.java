package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.DataNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmDto;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.time.LocalDate;
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
    public FilmService(FilmStorage filmStorage, UserService userService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film addFilm(FilmDto filmDto) {
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
                    .duration(filmDto.getDuration())
                    .build();
            filmStorage.addFilm(film);
            return film;
        } else return null;
    }

    public Film editFilm(FilmDto filmDto) {
        if (filmDto.getId() != null && validateFilm(filmDto)) {
            Optional<Film> maybeFilm = filmStorage.findFilmById(filmDto.getId());
            if (maybeFilm.isPresent()) {
                LocalDate filmDtoReleaseDay = LocalDate.parse(filmDto.getReleaseDate());
                Film film = Film.builder()
                        .id(filmDto.getId())
                        .name(filmDto.getName())
                        .description(filmDto.getDescription())
                        .releaseDate(filmDtoReleaseDay)
                        .duration(filmDto.getDuration())
                        .build();
                film.setLikes(maybeFilm.get().getLikes());
                filmStorage.addFilm(film);
                return film;
            } else
                throw new DataNotFoundException("Фильм не найден.");
        } else {
            throw new ValidationException("Введенная информация не соответствует требованиям валидации.");
        }
    }

    public Film getFilmById(Long id) {
        Optional<Film> maybeFilm = filmStorage.findFilmById(id);
        if (maybeFilm.isPresent()) return maybeFilm.get();
        throw new ValidationException("Фильм не найден.");
    }

    public void likeFilm(Long filmId, Long userId) {
        if (filmId <= 0 || userId <= 0) {
            throw new IllegalArgumentException("Невалидный ID");
        }
        Optional<Film> maybeFilm = filmStorage.findFilmById(filmId);
        User user = userService.getUserById(userId);
        if (maybeFilm.isPresent()) {
            Film film = maybeFilm.get();
            film.like(user.getId());
            filmStorage.addFilm(film);
        } else
            throw new DataNotFoundException("Фильм не найден.");
    }

    public void unlikeFilm(Long filmId, Long userId) {
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
    }

    public List<Film> getPopular(Integer count) {
        return filmStorage.getAllFilms().stream()
                .sorted((f1, f2) -> -Integer.compare(f1.getLikes().size(), f2.getLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
    }

    private boolean validateFilm(FilmDto filmDto) {
        LocalDate filmDtoReleaseDay = LocalDate.parse(filmDto.getReleaseDate());
        if (filmDto.getName().isBlank())
            throw new IllegalArgumentException("Название не может быть пустым.");
        if (filmDto.getDescription().length() > 200 || filmDto.getDescription().length() == 0)
            throw new IllegalArgumentException("Описание фильма слишком длинное или его вовсе нет.");
        if (filmDto.getDuration() <= 0)
            throw new IllegalArgumentException("Продолжительность фильма маловата.");
        if (filmDtoReleaseDay.isBefore(CINEMA_BIRTH_DAY))
            throw new IllegalArgumentException("Дата выхода фильма раньше даты изобретения кино");

        return true;
    }

    private Long getNewId() {
        latestId++;
        return latestId;
    }
}
