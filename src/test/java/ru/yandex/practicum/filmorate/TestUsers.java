package ru.yandex.practicum.filmorate;

import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

public class TestUsers {
    public static User user1 = User.builder()
            .id(1L)
            .email("user1@ya.ru")
            .login("user1login")
            .name("user1name")
            .birthday(LocalDate.parse("1987-12-10"))
            .build();

    public static User user1Update = User.builder()
            .id(1L)
            .email("user1@mail.ru")
            .login("user1loginUpdate")
            .name("user1UpdateName")
            .birthday(LocalDate.parse("1988-10-12"))
            .build();

    public static User user2 = User.builder()
            .id(2L)
            .email("user2@ya.ru")
            .login("user2login")
            .name("user2name")
            .birthday(LocalDate.parse("1996-11-11"))
            .build();
}
