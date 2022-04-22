package ru.yandex.practicum.filmorate.controllers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.UserDto;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {
    UserController userController = new UserController();

    @Test
    void shouldAddNewUser() throws ValidationException {
        UserDto userDto = new UserDto(
                1,
                "karmaig87@yandex.ru",
                "Maxrovij",
                "Maxim",
                "1987-12-10"
        );
        userController.addUser(userDto);
        User user = userController.getAllUsers().get(0);
        Assertions.assertTrue(user.getId() ==1 && user.getEmail().equals("karmaig87@yandex.ru"));
    }

    @Test
    void shouldThrowExceptionIfEmailIsBusy() throws ValidationException {
        UserDto userDto = new UserDto(
                1,
                "karmaig87@yandex.ru",
                "Maxrovij",
                "Maxim",
                "1987-12-10"
        );
        userController.addUser(userDto);
        Assertions.assertThrows(ValidationException.class, () -> userController.addUser(userDto));
    }

    @Test
    void shouldThrowExceptionIfBirthdayIsInFuture() {
        UserDto userDto = new UserDto(
                1,
                "karmaig87@yandex.ru",
                "Maxrovij",
                "Maxim",
                "2025-12-10"
        );
        Assertions.assertThrows(ValidationException.class, () -> userController.addUser(userDto));
    }

    @Test
    void shouldThrowExceptionIfLoginContainSpaces() {
        UserDto userDto = new UserDto(
                1,
                "karmaig87@yandex.ru",
                "Max rovij",
                "Maxim",
                "1987-12-10"
        );
        Assertions.assertThrows(ValidationException.class, () -> userController.addUser(userDto));
    }

    @Test
    void shouldUseLoginIfNameIsEmpty() throws ValidationException {
        UserDto userDto = new UserDto(
                1,
                "karmaig87@yandex.ru",
                "Maxrovij",
                "",
                "1987-12-10"
        );
        userController.addUser(userDto);
        User user = userController.getAllUsers().get(0);
        Assertions.assertTrue(user.getName().equals("Maxrovij"));
    }

    @Test
    void shouldEditUser() throws ValidationException {
        UserDto userDto = new UserDto(
                1,
                "karmaig87@yandex.ru",
                "Maxrovij",
                "Maxim",
                "1987-12-10"
        );
        userController.addUser(userDto);
        User notEditedUser = userController.getAllUsers().get(0);
        Assertions.assertTrue(notEditedUser.getId() == 1 &&
                notEditedUser.getEmail().equals("karmaig87@yandex.ru") &&
                notEditedUser.getLogin().equals("Maxrovij") &&
                notEditedUser.getName().equals("Maxim"));

        UserDto userDto1 = new UserDto(
                1,
                "karmaig87@gmail.com",
                "M@xrovij",
                "Maxim Kartsev",
                "1987-12-10"
        );
        userController.editUser(userDto1);
        User editedUser = userController.getAllUsers().get(0);
        Assertions.assertTrue(editedUser.getId() == 1 &&
                editedUser.getEmail().equals("karmaig87@gmail.com") &&
                editedUser.getLogin().equals("M@xrovij") &&
                editedUser.getName().equals("Maxim Kartsev"));
    }
}