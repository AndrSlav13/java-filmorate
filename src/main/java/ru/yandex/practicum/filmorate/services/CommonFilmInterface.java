package ru.yandex.practicum.filmorate.services;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface CommonFilmInterface {
    Film addFilm(Film film);

    Film updateFilm(Film film);

    List<Film> getFilms();

    Film getFilm(int id);

    List<User> addLike(Integer idFilm, Integer idUser);

    List<User> removeLike(Integer idFilm, Integer idUser);

    List<Film> getPopularFilms(Optional<Integer> count);
}
