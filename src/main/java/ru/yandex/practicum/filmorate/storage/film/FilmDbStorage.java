package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.dao.FilmDao;
import ru.yandex.practicum.filmorate.storage.dao.ServiceInfoDao;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Component("filmDbStorage")
public class FilmDbStorage implements FilmStorage {

    private final FilmDao filmDao;
    private final ServiceInfoDao serviceInfoDao;

    @Autowired
    public FilmDbStorage(FilmDao filmDao, ServiceInfoDao serviceInfoDao) {
        this.filmDao = filmDao;
        this.serviceInfoDao = serviceInfoDao;
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
    public Collection<Film> getPopular(Integer count) {
        return filmDao.getPopular(count);
    }

    @Override
    public void putLike(Long filmId, Long userId) {
        filmDao.putLike(filmId, userId);
    }

    @Override
    public void deleteLike(Long filmId, Long userId) {
        filmDao.deleteLike(filmId, userId);
    }

    @Override
    public MpaRating getMpaRatingById(int id) {
        return serviceInfoDao.getMpaRatingById(id);
    }

    @Override
    public Collection<MpaRating> getAllMpa() {
        return serviceInfoDao.getAllMpa();
    }

    @Override
    public Collection<Genre> getAllGenres() {
        return serviceInfoDao.getAllGenres();
    }

    @Override
    public Genre getGenreById(int id) {
        return serviceInfoDao.getGenreById(id);
    }
}
