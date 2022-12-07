package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.services.FilmService;
import ru.yandex.practicum.filmorate.services.FilmServiceInterface;
import ru.yandex.practicum.filmorate.services.UserService;
import ru.yandex.practicum.filmorate.services.UserServiceInterface;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
public class FilmController {
    private final FilmServiceInterface filmService;
    private final UserServiceInterface userService;

    public static org.slf4j.Logger getLogger() {
        return log;
    }

    @Autowired
    public FilmController(FilmService filmService, UserService userService) {
        this.filmService = filmService;
        this.userService = userService;
    }

    @GetMapping("/films")
    public ArrayList<Film> getFilms(HttpServletRequest request) {
        filmService.log(request);
        return new ArrayList<>(filmService.getFilms());
    }

    @GetMapping("/films/{filmId}")
    public Film getUser(@PathVariable(value = "filmId") int id, HttpServletRequest request) {
        filmService.log(request);
        filmService.validateFilmExists(id);
        return filmService.getFilm(id);
    }

    @GetMapping("/films/popular")
    public List<Film> getPopularFilms(@RequestParam Optional<Integer> count, HttpServletRequest request) {
        filmService.log(request);
        return filmService.getPopularFilms(count);
    }

    @PostMapping("/films")
    public Film addFilm(@RequestBody Film film, HttpServletRequest request) {
        filmService.log(request);
        filmService.validateFilmFormat(film, request.getMethod());
        filmService.addFilm(film);
        return film;
    }

    @PutMapping("/films")
    public Film updateFilm(@RequestBody Film film, HttpServletRequest request) {
        filmService.log(request);
        filmService.validateFilmFormat(film, request.getMethod());
        filmService.updateFilm(film);
        return film;
    }

    @PutMapping("/films/{filmId}/like/{userId}")
    public List<User> addFilmLike(@PathVariable int filmId, @PathVariable int userId, HttpServletRequest request) {
        filmService.log(request);
        filmService.validateFilmExists(filmId);
        userService.validateUserExists(userId);
        return filmService.addLike(filmId, userId);
    }

    @DeleteMapping("/films/{filmId}/like/{userId}")
    public List<User> deleteFilmLike(@PathVariable int filmId, @PathVariable int userId, HttpServletRequest request) {
        filmService.log(request);
        filmService.validateFilmExists(filmId);
        userService.validateUserExists(userId);
        return filmService.removeLike(filmId, userId);
    }

}