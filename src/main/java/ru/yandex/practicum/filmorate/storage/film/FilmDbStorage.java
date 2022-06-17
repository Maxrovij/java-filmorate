package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.dao.FilmDao;

import java.util.List;
import java.util.Optional;

@Component("filmDbStorage")
public class FilmDbStorage implements FilmStorage {

    private final FilmDao filmDao;

    @Autowired
    public FilmDbStorage(FilmDao filmDao) {
        this.filmDao = filmDao;
    }

    @Override
    public List<Film> getAllFilms() {
        return filmDao.getAllFilms();
    }

    @Override
    public Film addFilm(Film film) {
        return filmDao.addNewFilm(film);
    }

    @Override
    public Film editFilm(Film film) {
        return filmDao.editFilm(film);
    }

    @Override
    public Optional<Film> findFilmById(Long id) {
        return filmDao.findFilmById(id);
    }

    @Override
    public List<Film> getPopular(Integer count) {
        return filmDao.getPopular(count);
    }
}
