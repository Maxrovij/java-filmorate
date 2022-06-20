package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.annotation.Order;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_=@Autowired)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FilmorateApplicationTests {
    private final UserDbStorage userDbStorage;
    private final FilmDbStorage filmDbStorage;

    @Test
    @Order(1)
    public void addUser() {
        Assertions.assertEquals(userDbStorage.addUser(TestUsers.user1).getId(), 1);
    }

    @Test
    @Order(2)
    public void editUser1() {
        User u = userDbStorage.updateUser(TestUsers.user1Update);
        Assertions.assertEquals(u.getId(),1);
        Assertions.assertEquals(u.getEmail(),"user1@mail.ru");
        Assertions.assertEquals(u.getLogin(), "user1loginUpdate");
        Assertions.assertEquals(u.getName(),"user1UpdateName");
        Assertions.assertEquals(u.getBirthday(), LocalDate.of(1988,10,12));
    }

    @Test
    @Order(3)
    public void findUserById() {
        Optional<User> maybeUser = userDbStorage.findUserById(1L);
        Assertions.assertTrue(maybeUser.isPresent());
        Assertions.assertEquals(maybeUser.get().getId(), 1);
    }

    @Test
    @Order(4)
    public void getAllUsers() {
        userDbStorage.addUser(TestUsers.user2);
        List<User> users = userDbStorage.getAllUsers();
        Assertions.assertEquals(2, users.size());
        Assertions.assertTrue(users.contains(TestUsers.user1Update));
        Assertions.assertTrue(users.contains(TestUsers.user2));
    }

    @Test
    @Order(5)
    public void addFriend() {
        userDbStorage.addFriend(1L, 2L);
        Assertions.assertTrue(userDbStorage.getUserFriends(1L).contains(TestUsers.user2) );
    }

    @Test
    @Order(6)
    public void deleteFriend(){
        userDbStorage.deleteFriend(1L, 2L);
        Assertions.assertFalse(userDbStorage.getUserFriends(1L).contains(TestUsers.user2));
        Assertions.assertEquals(0, userDbStorage.getUserFriends(1L).size());
    }

    @Test
    @Order(7)
    public void addFilm() {
        Assertions.assertEquals(filmDbStorage.addFilm(TestFilms.film1), TestFilms.film1);
    }

    @Test
    @Order(8)
    public void editFilm() {
        Assertions.assertEquals(filmDbStorage.editFilm(TestFilms.film1update), TestFilms.film1update);
    }

    @Test
    @Order(9)
    public void findById() {
        Optional<Film> mayBeFilm = filmDbStorage.findFilmById(1L);
        Assertions.assertTrue(mayBeFilm.isPresent());
        Assertions.assertEquals(mayBeFilm.get().getId(),1);
    }

    @Test
    @Order(10)
    public void putLike() {
        filmDbStorage.putLike(1L, 1l);
        Assertions.assertEquals(filmDbStorage.findFilmById(1L).get().getRate(), 6);
    }

    @Test
    @Order(11)
    public void deleteLike() {
        filmDbStorage.deleteLike(1L, 1l);
        Assertions.assertEquals(filmDbStorage.findFilmById(1L).get().getRate(), 5);
    }

    @Test
    @Order(12)
    public void getPopular() {
        filmDbStorage.addFilm(TestFilms.film2);
        Collection<Film> popular = filmDbStorage.getPopular(10);
        Assertions.assertTrue(popular.stream().findFirst().get().getRate() == 5);
    }

    @Test
    @Order(13)
    public void getAll() {
        Assertions.assertTrue(filmDbStorage.getAllFilms().size() == 2);
    }

    @Test
    @Order(14)
    public void getMpaRatingById() {
        Assertions.assertTrue(filmDbStorage.getMpaRatingById(1).getName().equals("G"));
    }

    @Test
    @Order(15)
    public void getAllMpa() {
        Assertions.assertTrue(filmDbStorage.getAllMpa().size() == 5);
    }

    @Test
    @Order(16)
    public void getAllGenres() {
        Assertions.assertTrue(filmDbStorage.getAllGenres().size() == 6);
    }

    @Test
    @Order(17)
    public void getGenreById() {
        Assertions.assertTrue(filmDbStorage.getGenreById(1).getName().equals("Комедия"));
    }
}
