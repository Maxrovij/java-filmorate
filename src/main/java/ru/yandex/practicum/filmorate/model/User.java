package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

// класс представления пользователя
@Data
@Builder
public class User {
    private final Long id;
    private final String email;
    private final String login;
    private String name;
    private final LocalDate birthday;
}
