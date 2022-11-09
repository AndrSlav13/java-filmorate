package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.errors.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
public class FilmController {
    private int baseId = 0;
    private final Map<Integer, Film> films = new HashMap<>();

    @GetMapping("/films")
    public ArrayList<Film> getFilms() {
        return new ArrayList<Film>(films.values());
    }

    @PostMapping("/films")
    public Film setFilm(@RequestBody Film film, HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
        validate(film, request.getMethod());

        ++baseId;
        film.setId(baseId);

        films.put(baseId, film);
        return films.get(baseId);
    }

    @PutMapping("/films")
    public Film updateFilm(@RequestBody Film film, HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
        validate(film, request.getMethod());

        Integer id = film.getId();

        films.put(id, film);
        return films.get(id);
    }

    private void validate(Film film, String method) {
        if (method.equals("PUT"))
            if (film.getId() == null || !films.containsKey(film.getId()))
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
}