package ru.yandex.practicum.filmorate.controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

// служебный класс для генерирования ID и хранения соответсвующего названия фильма
public class FilmIDStorage {
    private static final Random r = new Random();
    private static Map<String, Integer> filmIds = new HashMap<>();

    // генератор Айди
    public static int generateID(String filmName) {
        int id = 1;
        while (filmIds.containsValue(id)){
            id++;
        }
        filmIds.put(filmName, id);
        return id;
    }

    // получаем Айди фильма по названию
    public static Integer getFilmIdByName(String name) {
        return filmIds.getOrDefault(name, -1);
    }
}
