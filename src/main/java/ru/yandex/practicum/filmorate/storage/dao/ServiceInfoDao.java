package ru.yandex.practicum.filmorate.storage.dao;

import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.Collection;

public interface ServiceInfoDao {
    MpaRating getMpaRatingById(int id);

    Collection<MpaRating> getAllMpa();

    Collection<Genre> getAllGenres();

    Genre getGenreById(int id);
}
