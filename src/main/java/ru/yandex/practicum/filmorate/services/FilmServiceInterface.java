package ru.yandex.practicum.filmorate.services;

import ru.yandex.practicum.filmorate.model.Film;

import javax.servlet.http.HttpServletRequest;

public interface FilmServiceInterface extends CommonFilmInterface {
    void validateFilmFormat(Film film, String method);

    void validateFilmExists(int filmId);

    void log(HttpServletRequest request);
}
