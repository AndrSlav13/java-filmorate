package ru.yandex.practicum.filmorate.services;

import ru.yandex.practicum.filmorate.controllers.FilmController;
import ru.yandex.practicum.filmorate.errors.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;

public class ServiceFilm {
    public static void validate(Film film, String method) {
        if (method.equals("PUT"))
            if (film.getId() == null || !FilmController.getData().contains(film))
                throw new ValidationException(404, "incorrect film id");

        String name = film.getName();
        if (name == null || name.trim().isEmpty()) throw new ValidationException("film name is mandatory parameter");

        String description = film.getDescription();
        if (description.length() > Film.filmDescriptionMaxLength) throw new ValidationException("description too long");

        LocalDate date = film.getReleaseDate();
        if (date.isBefore(Film.theEarliestPossibleDate)) throw new ValidationException("too old film");

        int duration = film.getDuration();
        if (duration < 0) throw new ValidationException("duration is to be positive");

    }

    public static void log(HttpServletRequest request) {
        FilmController.getLogger().info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
    }

    public static void addFilm(Film film) {
        FilmController.addFilm(film);
    }

    public static void updateFilm(Film film) {
        FilmController.updateFilm(film);
    }
}
