package ru.yandex.practicum.filmorate.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controllers.FilmController;
import ru.yandex.practicum.filmorate.errors.httpExceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorageInterface;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class FilmService implements FilmServiceInterface {
    private final FilmStorageInterface filmData;
    private static final org.slf4j.Logger log = FilmController.getLogger();

    @Autowired
    public FilmService(InMemoryFilmStorage filmData) {
        this.filmData = filmData;
    }

    @Override
    public void validateFilmFormat(Film film, String method) {
        if (method.equals("PUT"))
            if (film.getId() == null || !filmData.contains(film))
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

    @Override
    public void validateFilmExists(int filmId) {
        if (!filmData.contains(filmId))
            throw new ValidationException(404, "film id=" + filmId + " doesn't exist");
    }

    @Override
    public void log(HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
    }

    @Override
    public Film addFilm(Film film) {
        return filmData.addFilm(film);
    }

    @Override
    public Film updateFilm(Film film) {
        return filmData.updateFilm(film);
    }

    @Override
    public List<Film> getFilms() {
        return new ArrayList<>(filmData.getFilms());
    }

    @Override
    public Film getFilm(int id) {
        return filmData.getFilm(id);
    }

    @Override
    public List<User> addLike(Integer idFilm, Integer idUser) {
        return filmData.addLike(idFilm, idUser);
    }

    @Override
    public List<User> removeLike(Integer idFilm, Integer idUser) {
        return filmData.removeLike(idFilm, idUser);
    }

    @Override
    public List<Film> getPopularFilms(Optional<Integer> count) {
        return filmData.getPopularFilms(count);
    }
}
