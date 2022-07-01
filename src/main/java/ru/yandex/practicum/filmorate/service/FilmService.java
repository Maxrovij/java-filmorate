package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.comparator.Comparators;
import ru.yandex.practicum.filmorate.exceptions.DataNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.time.LocalDate;
import java.util.*;

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

    public Film addFilm(Film film) {
        if(validateFilm(film)) {
            LocalDate filmReleaseDate = film.getReleaseDate();
            String name = film.getName();
            for (Film f : filmStorage.getAllFilms()) {
                if (f.getName().equals(film.getName()) && f.getReleaseDate().equals(filmReleaseDate)) {
                    throw new ValidationException("Такой фильм уже есть в базе.");
                }
                if (f.getName().equals(film.getName())){
                    name = name + " (" + filmReleaseDate.getYear() + ")";
                }
            }

            Film filmToAdd = Film.builder()
                    .id(getNewId())
                    .name(name)
                    .description(film.getDescription())
                    .releaseDate(filmReleaseDate)
                    .duration(film.getDuration())
                    .rate(film.getRate())
                    .mpa(filmStorage.getMpaRatingById(film.getMpa().getId()))
                    .genres(createGenreList(film))
                    .build();

            return filmStorage.addFilm(filmToAdd);
        } else return null;
    }

    public Film editFilm(Film film) {
        if (film.getId() != null && validateFilm(film)) {
            Optional<Film> maybeFilm = filmStorage.findFilmById(film.getId());
            if (maybeFilm.isPresent()) {
                LocalDate filmDtoReleaseDay = film.getReleaseDate();
                String name = film.getName();
                for (Film f : filmStorage.getAllFilms()) {
                    if (f.getName().equals(name) && f.getReleaseDate().equals(filmDtoReleaseDay)) {
                        if (!f.getId().equals(maybeFilm.get().getId()))
                            throw new ValidationException("Такой фильм уже есть в базе.");
                    }
                }

                Film filmToEdit = Film.builder()
                        .id(film.getId())
                        .name(name)
                        .description(film.getDescription())
                        .releaseDate(filmDtoReleaseDay)
                        .duration(film.getDuration())
                        .rate(film.getRate())
                        .mpa(filmStorage.getMpaRatingById(film.getMpa().getId()))
                        .genres(createGenreList(film))
                        .build();

                return filmStorage.editFilm(filmToEdit);
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

    public Collection<Film> getPopular(Integer count) {
        return filmStorage.getPopular(count);
    }

    public void likeFilm(Long filmId, Long userId) {
        Optional<Film> maybeFilm = filmStorage.findFilmById(filmId);
        userService.getUserById(userId);
        if (maybeFilm.isPresent()) {
            filmStorage.putLike(filmId, userId);
        } else
            throw new DataNotFoundException("Фильм не найден.");
    }

    public void unlikeFilm(Long filmId, Long userId) {
        Optional<Film> maybeFilm = filmStorage.findFilmById(filmId);
        userService.getUserById(userId);
        if (maybeFilm.isPresent()) {
            filmStorage.deleteLike(filmId, userId);
        } else throw new DataNotFoundException("Фильм не найден.");
    }

    public MpaRating getMpaRatingById(int id) {
        return filmStorage.getMpaRatingById(id);
    }

    public Collection<MpaRating> getAllMpa() {
        return filmStorage.getAllMpa();
    }

    public Collection<Genre> getAllGenres() {
        return filmStorage.getAllGenres();
    }

    public Genre getGenreById(int id) {
        return filmStorage.getGenreById(id);
    }

    private HashSet<Genre> createGenreList(Film film) {
        if (film.getGenres() == null) return null;
        if (film.getGenres().isEmpty())return new HashSet<>();
        HashSet<Genre> genres = new HashSet<>();
        for (Genre g : film.getGenres()) {
            genres.add(filmStorage.getGenreById(g.getId()));
        }
        return genres;
    }

    private boolean validateFilm(Film film) {
        if (film.getName().isBlank())
            throw new ValidationException("Название не может быть пустым.");
        if (film.getDescription().length() > 200 || film.getDescription().length() == 0)
            throw new ValidationException("Описание фильма слишком длинное или его вовсе нет.");
        if (film.getDuration() <= 0)
            throw new ValidationException("Продолжительность фильма маловата.");
        if (film.getReleaseDate().isBefore(CINEMA_BIRTH_DAY))
            throw new ValidationException("Дата выхода фильма раньше даты изобретения кино.");
        if (film.getMpa() == null)
            throw new ValidationException("Не указан рейтинг MPA.");

        return true;
    }

    private Long getNewId() {
        latestId++;
        return latestId;
    }
}