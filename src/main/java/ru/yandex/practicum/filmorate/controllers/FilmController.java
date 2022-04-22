package ru.yandex.practicum.filmorate.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmDto;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

// класс обработки запросов к фильмам
@RestController
@RequestMapping("/films")
public class FilmController {
    private final static Logger log = LoggerFactory.getLogger(FilmController.class);
    private final LocalDate cinemaBirthDay = LocalDate.of(1895, 12, 28);
    private HashMap<Integer, Film> films = new HashMap<>();

    // обработка GET запроса для получения списка добавленных фильмов
    @GetMapping
    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    // обработка POST запроса для добавления фильма
    @PostMapping
    public void addFilm(@RequestBody FilmDto filmDto) throws ValidationException {
        LocalDate filmDtoReleaseDay = LocalDate.parse(filmDto.getReleaseDate());

        if(validateFilm(filmDtoReleaseDay, filmDto)) {
            Integer mayBeId = FilmIDStorage.getFilmIdByName(filmDto.getName());
            if (films.containsKey(mayBeId)
                    && films.get(mayBeId).getReleaseDate().getYear() == filmDtoReleaseDay.getYear()) {
                log.debug("Валидация не пройдена. Фильм уже есть в базе.");
                throw new ValidationException("Такой фильм уже присутствует в базе.");
            }

            // иногда названия фильмов повторяются(например если фильм пересняли), но даты выпуска разные
            // тогда к названию фильма прибавляю год выпуска
            String name;
            if (films.containsKey(mayBeId)) {
                name = filmDto.getName() + " (" + filmDtoReleaseDay.getYear() + ")";
            }else {
                name = filmDto.getName();
            }

            int id = FilmIDStorage.generateID(name);

            Film film = Film.builder()
                    .id(id)
                    .name(name)
                    .description(filmDto.getDescription())
                    .releaseDate(filmDtoReleaseDay)
                    .duration(Duration.ofMillis(filmDto.getDuration()))
                    .build();

            films.put(film.getId(), film);
            log.trace("Обработант POST запрос на /films");
        } else {
            throw new ValidationException("Введенная информация о фильме не соответствует требованиям валидации.");
        }

    }

    // обработка PUT запроса для редактирования фильма
    @PutMapping
    public void editFilm(@RequestBody FilmDto filmDto) throws ValidationException {
        LocalDate filmDtoReleaseDay = LocalDate.parse(filmDto.getReleaseDate());
        if (validateFilm(filmDtoReleaseDay, filmDto) && filmDto.getId() != null) {
            if (films.containsKey(filmDto.getId())) {
                Film film = Film.builder()
                        .id(filmDto.getId())
                        .name(filmDto.getName())
                        .description(filmDto.getDescription())
                        .releaseDate(filmDtoReleaseDay)
                        .duration(Duration.ofMillis(filmDto.getDuration()))
                        .build();
                films.put(film.getId(), film);
                log.trace("Обработант PUT запрос на /films");
            } else {
                log.debug("Валидация не пройдена. Неверный ID.");
                throw new ValidationException("фильм с ID " + filmDto.getId() + "отсутствует в базе.");
            }
        } else {
            throw new ValidationException("Введенная информация не соответствует требованиям валидации.");
        }
    }

    // метод валидации фильма по полям
    private boolean validateFilm(LocalDate release, FilmDto filmDto) {
        if (filmDto.getName().isBlank()) {
            log.debug("Валидация не пройдена. Не указано название фильма.");
            return false;
        }
        if (filmDto.getDescription().length() > 200 || filmDto.getDescription().length() == 0) {
            log.debug("Валидация не пройдена. Описание больше 200 символов.");
            return false;
        }
        if (filmDto.getDuration() <= 0) {
            log.debug("Валидация не пройдена. Продолжительность фильма равно 0 или имеет отрицательное значение.");
            return false;
        }
        if (release.isBefore(cinemaBirthDay)) {
            log.debug("Валидация не пройдена. Дата выпуска фильма раньше, чем придумали кино.");
            return false;
        }
        return true;
    }
}


