package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.services.ServiceFilm;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

@RestController
@Slf4j
public class FilmController {
    public static org.slf4j.Logger getLogger() {
        return log;
    }

    @GetMapping("/films")
    public ArrayList<Film> getFilms() {
        return ServiceFilm.getFilms();
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