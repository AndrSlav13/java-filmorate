package ru.yandex.practicum.filmorate.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controllers.FilmController;
import ru.yandex.practicum.filmorate.errors.httpExceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.DbFilmStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorageInterface;
import ru.yandex.practicum.filmorate.util.Genre;
import ru.yandex.practicum.filmorate.util.MPA;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class FilmService implements FilmServiceInterface {
    private final FilmStorageInterface filmData;
    private static final org.slf4j.Logger log = FilmController.getLogger();

    @Override
    public Boolean contains(Integer userId) {
        log.debug("contains(Integer userId)");
        return filmData.contains(userId);
    }

    @Autowired
    public FilmService(DbFilmStorage filmData) {
        log.debug("FilmService(DbFilmStorage filmData)");
        this.filmData = filmData;
    }

    @Override
    public void validateFilmFormat(Film film, String method) {
        log.debug("validateFilmFormat(Film film, String method)");
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
        log.debug("validateFilmExists(int filmId)");
        if (!filmData.contains(filmId))
            throw new ValidationException(404, "film id=" + filmId + " doesn't exist");
    }

    @Override
    public void log(HttpServletRequest request) {
        log.debug("log(HttpServletRequest request)");
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
    }

    @Override
    public Film addFilm(Film film) {
        log.debug("addFilm(Film film)");
        return filmData.addFilm(film);
    }

    @Override
    public Film updateFilm(Film film) {
        log.debug("updateFilm(Film film)");
        return filmData.updateFilm(film);
    }

    @Override
    public List<Film> getFilms() {
        log.debug("getFilms()");
        return new ArrayList<>(filmData.getFilms());
    }

    @Override
    public Film getFilm(int id) {
        log.debug("getFilm(int id)");
        return filmData.getFilm(id);
    }

    @Override
    public List<User> addLike(Integer idFilm, Integer idUser) {
        log.debug("addLike(Integer idFilm, Integer idUser)");
        return filmData.addLike(idFilm, idUser);
    }

    @Override
    public List<User> removeLike(Integer idFilm, Integer idUser) {
        log.debug("removeLike(Integer idFilm, Integer idUser)");
        return filmData.removeLike(idFilm, idUser);
    }

    @Override
    public List<Film> getPopularFilms(Optional<Integer> count) {
        log.debug("getPopularFilms(Optional<Integer> count)");
        return filmData.getPopularFilms(count);
    }

    @Override
    public List<Genre> getGenres() {
        log.debug("getGenres()");
        return filmData.getGenres();
    }

    @Override
    public Genre getGenre(int id) {
        log.debug("getGenre(int id)");
        return filmData.getGenre(id);
    }

    @Override
    public List<MPA> getMPAs() {
        log.debug("getMPAs()");
        return filmData.getMPAs();
    }

    @Override
    public MPA getMPA(int id) {
        log.debug("getMPA(int id)");
        return filmData.getMPA(id);
    }
}
