package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.services.ServiceFilm;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
public class FilmController {
    private static int baseId = 0;
    private static final Map<Integer, Film> films = new HashMap<>();

    public static Collection<Film> getData() {
        return films.values();
    }

    public static org.slf4j.Logger getLogger() {
        return log;
    }

    private static int getNewId() {
        return ++baseId;
    }

    public static void addFilm(Film film) {
        film.setId(getNewId());
        films.put(film.getId(), film);
    }

    public static void updateFilm(Film film) {
        films.put(film.getId(), film);
    }

    @GetMapping("/films")
    public ArrayList<Film> getFilms() {
        return new ArrayList<Film>(films.values());
    }

    @PostMapping("/films")
    public Film setFilm(@RequestBody Film film, HttpServletRequest request) {
        ServiceFilm.log(request);
        ServiceFilm.validate(film, request.getMethod());
        ServiceFilm.addFilm(film);
        return film;
    }

    @PutMapping("/films")
    public Film updateFilm(@RequestBody Film film, HttpServletRequest request) {
        ServiceFilm.log(request);
        ServiceFilm.validate(film, request.getMethod());
        ServiceFilm.updateFilm(film);
        return film;
    }

}