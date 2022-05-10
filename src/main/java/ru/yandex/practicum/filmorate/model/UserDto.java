package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.*;

// DTO для передаячи информации о пользователе
@Data
public class UserDto {
    private final Long id;

    @Email
    private final String email;

    @NotNull
    @NotBlank
    private final String login;

    private final String name;

    private final String birthday;
}
