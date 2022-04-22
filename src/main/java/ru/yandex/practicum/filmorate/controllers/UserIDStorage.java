package ru.yandex.practicum.filmorate.controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

// класс генерирования Айди пользователя и хранение соответсвующего имейла
public class UserIDStorage {
    private static final Random r = new Random();
    private static Map<String, Integer> usersEmails = new HashMap<>();

    public static int generateID(String email) {
        int id = 1;
        while (usersEmails.containsValue(id)){
            id++;
        }
        usersEmails.put(email, id);
        return id;
    }

    // получение Айди по имейлу
    public static Integer getUserIDByEmail(String email) {
        return usersEmails.getOrDefault(email, -1);
    }
}
