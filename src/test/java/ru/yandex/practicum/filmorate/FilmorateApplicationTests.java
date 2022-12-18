package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.DbFilmStorage;
import ru.yandex.practicum.filmorate.storage.DbUserStorage;
import ru.yandex.practicum.filmorate.util.Genre;
import ru.yandex.practicum.filmorate.util.MPA;

import java.time.LocalDate;
import java.time.Period;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class FilmorateApplicationTests {
    @Autowired
    private final DbUserStorage userStorage;
    @Autowired
    private final DbFilmStorage filmStorage;

    LocalDate date1;
    LocalDate date2;
    LocalDate date3;
    LocalDate date3u;
    String name;
    String login;
    String email;

    User usr1;
    User usr2;
    User usr3;
    User usr3u;

    String nameF;
    String description;
    Integer duration;
    LocalDate releaseDate;
    LinkedHashSet<Genre> genres;
    Genre genre_;
    MPA mpa;
    Film film;
    Film film2;


    @BeforeEach
    public void setResources() {

        date1 = LocalDate.of(2012, 9, 12);
        date2 = LocalDate.of(2013, 9, 12);
        date3 = LocalDate.of(2014, 9, 12);
        date3u = LocalDate.of(2015, 9, 12);
        name = "name";
        login = "login";
        email = "em@atril";

        usr1 = User.builder().name(name)
                .birthday(date1)
                .email(email)
                .login(login)
                .id(1)
                .build();
        usr2 = User.builder().name(name + "2")
                .birthday(date2)
                .email(email + "2")
                .login(login + "2")
                .id(2)
                .build();
        usr3 = User.builder().name(name + "3")
                .birthday(date3)
                .email(email + "3")
                .login(login + "3")
                .id(3)
                .build();
        usr3u = User.builder().name(name + "4")
                .birthday(date3u)
                .email(email + "4")
                .login(login + "4")
                .id(3)
                .build();

        nameF = "name";
        description = "description";
        duration = 12;
        releaseDate = LocalDate.of(2012, 9, 12);
        genres = new LinkedHashSet<>();
        genre_ = new Genre(5, "Документальный");
        genres.add(genre_);
        mpa = new MPA(1, "G");
        film = Film.builder()
                .name(nameF)
                .mpa(mpa)
                .genres(genres)
                .description(description)
                .duration(duration)
                .releaseDate(releaseDate)
                .build();
        film2 = Film.builder()
                .name(nameF + "2")
                .mpa(mpa)
                .genres(genres)
                .description(description + "2")
                .duration(duration + 2)
                .releaseDate(releaseDate.minus(Period.of(0, 0, 2)))
                .build();
    }

    @Test
    public void testFindUserById() {

        userStorage.addUser(usr1);
        User user = userStorage.getUser(1);

        user = userStorage.getUser(1);
        assertThat(user).hasFieldOrPropertyWithValue("name", name)
                .hasFieldOrPropertyWithValue("id", 1)
                .hasFieldOrPropertyWithValue("login", login)
                .hasFieldOrPropertyWithValue("email", email)
                .hasFieldOrPropertyWithValue("birthday", date1);
    }

    @Test
    public void testFriends() {
        userStorage.addUser(usr1);
        userStorage.addUser(usr2);
        userStorage.addFriend(1, 2);

        List<User> userList = userStorage.getFriends(1);
        assertThat(userList).containsExactly(usr2);
        userList = userStorage.getFriends(2);
        assertThat(userList).doesNotContain(usr1);

    }

    @Test
    public void testCommonUpdate() {
        userStorage.addUser(usr1);
        userStorage.addUser(usr2);
        userStorage.addUser(usr3);

        userStorage.addFriend(1, 2);
        userStorage.addFriend(1, 3);
        userStorage.addFriend(3, 2);
        userStorage.addFriend(3, 1);
        userStorage.updateUser(usr3u);

        List<User> userList = userStorage.getCommonFriends(1, 3);
        assertThat(userList).containsExactly(usr2);
        userList = userStorage.getFriends(2);
        assertThat(userList).isEmpty();
        userList = userStorage.getFriends(3);
        assertThat(userList).contains(usr1, usr2);
        userList = userStorage.getFriends(1);
        assertThat(userList).contains(usr2, usr3u);
        User user = userStorage.getUser(3);
        assertThat(user).isEqualTo(usr3u);
        assertThat(user).isNotEqualTo(usr2);
    }

    @Test
    public void testFindFilmById() {
        filmStorage.addFilm(film);
        filmStorage.addFilm(film2);

        userStorage.addUser(usr1);

        filmStorage.addLike(1, 1);

        Film flm = filmStorage.getFilm(1);
        assertThat(flm).hasFieldOrPropertyWithValue("name", nameF)
                .hasFieldOrPropertyWithValue("id", 1)
                .hasFieldOrPropertyWithValue("description", description)
                .hasFieldOrPropertyWithValue("duration", duration)
                .hasFieldOrPropertyWithValue("releaseDate", releaseDate)
                .hasFieldOrPropertyWithValue("genres", genres)
                .hasFieldOrPropertyWithValue("mpa", mpa);
        flm = filmStorage.getPopularFilms(Optional.of(1)).get(0);
        assertThat(flm).isEqualTo(film);
        assertThat(flm).isNotEqualTo(film2);
        List<Film> flmList = filmStorage.getFilms();
        assertThat(flmList).contains(film, film2);
        flmList = filmStorage.getPopularFilms(Optional.empty());
        assertThat(flmList).hasSize(2);
    }

    @Test
    public void testDelete() {
        filmStorage.addFilm(film);
        film2.setId(film.getId());
        filmStorage.updateFilm(film2);

        userStorage.addUser(usr1);
        userStorage.addUser(usr2);
        userStorage.addUser(usr3);
        filmStorage.addLike(film.getId(), usr1.getId());
        filmStorage.addLike(film.getId(), usr2.getId());
        filmStorage.addLike(film.getId(), usr3.getId());
        filmStorage.removeLike(film.getId(), usr1.getId());
        filmStorage.removeLike(film.getId(), usr2.getId());
        usr1.setId(usr3.getId());
        usr1.setLogin("qqqqqqqqqq");
        userStorage.updateUser(usr1);
        userStorage.addFriend(usr2.getId(), usr2.getId());
        userStorage.addFriend(usr3.getId(), usr2.getId());
        userStorage.deleteFriend(usr3.getId(), usr2.getId());

        Film flm = filmStorage.getFilm(film.getId());
        assertThat(flm).hasFieldOrPropertyWithValue("name", film2.getName())
                .hasFieldOrPropertyWithValue("description", film2.getDescription())
                .hasFieldOrPropertyWithValue("duration", film2.getDuration())
                .hasFieldOrPropertyWithValue("releaseDate", film2.getReleaseDate());

        User user = userStorage.getUser(usr3.getId());
        assertThat(user).hasFieldOrPropertyWithValue("name", usr1.getName())
                .hasFieldOrPropertyWithValue("id", usr1.getId())
                .hasFieldOrPropertyWithValue("email", usr1.getEmail())
                .hasFieldOrPropertyWithValue("birthday", usr1.getBirthday());
        List<User> userList = userStorage.getCommonFriends(usr2.getId(), usr3.getId());
        assertThat(userList).isEmpty();
    }

    @Test
    public void testAlls() {

        filmStorage.addFilm(film);
        filmStorage.addFilm(film2);

        userStorage.addUser(usr1);
        userStorage.addUser(usr2);
        List<Film> filmList = filmStorage.getFilms();
        List<User> userList = userStorage.getUsers();
        List<Genre> genreList = filmStorage.getGenres();
        List<MPA> mpaList = filmStorage.getMPAs();
        assertThat(filmList).hasSize(2);
        assertThat(userList).hasSize(2);
        assertThat(genreList).hasSize(6);
        assertThat(mpaList).hasSize(5);
    }

    @Test
    public void testFindMPA() {
        filmStorage.addFilm(film);

        MPA mpa2 = filmStorage.getMPA(1);
        Genre genre = filmStorage.getGenre(5);
        assertThat(mpa2).isEqualTo(mpa);
        assertThat(genre).isEqualTo(genre_);
    }
}
