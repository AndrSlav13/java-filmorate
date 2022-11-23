package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.services.UserService;
import ru.yandex.practicum.filmorate.services.UserServiceInterface;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryFilmStorage implements FilmStorageInterface {
    private final Map<Integer, Film> films = new HashMap<>();
    private final Map<Integer, HashSet<Integer>> likes = new HashMap<>();   //idFilm -> likes
    private final UserServiceInterface userService;
    private static int baseId = 0;
    private static int numFilmsDefault = 10;

    @Autowired
    public InMemoryFilmStorage(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Integer getSize() {
        return films.size();
    }

    @Override
    public Boolean isEmpty() {
        return films.isEmpty();
    }

    @Override
    public Boolean contains(Film film) {
        return films.containsValue(film);
    }

    @Override
    public Boolean contains(Integer filmId) {
        return films.containsKey(filmId);
    }

    @Override
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film getFilm(int id) {
        return films.get(id);
    }

    @Override
    public Film addFilm(Film film) {
        films.put(++baseId, film);
        film.setId(baseId);
        return films.get(baseId);
    }

    @Override
    public Film updateFilm(Film film) {
        films.put(film.getId(), film);
        return films.get(film.getId());
    }

    @Override
    public List<User> addLike(Integer idFilm, Integer idUser) {
        if (!likes.containsKey(idFilm)) likes.put(idFilm, new HashSet<>());
        likes.get(idFilm).add(idUser);
        return likes.get(idFilm).stream()
                .map(id -> userService.getUser(id))
                .collect(Collectors.toList());
    }

    @Override
    public List<User> removeLike(Integer idFilm, Integer idUser) {
        if (likes.containsKey(idFilm)) likes.get(idFilm).remove(idUser);
        return likes.get(idFilm).stream()
                .map(id -> userService.getUser(id))
                .collect(Collectors.toList());
    }

    private int comp(Set<Integer> i1, Set<Integer> i2) {
        if (i1 == null) return 1;
        if (i2 == null) return -1;
        return i2.size() - i1.size();
    }

    @Override
    public List<Film> getPopularFilms(@PathVariable Optional<Integer> count) {
        int num;
        if (count.isPresent()) {
            num = count.get();
        } else {
            num = numFilmsDefault;
        }
        num = Integer.min(num, films.size());

        return films.values().stream()
                .sorted((f1, f2) -> comp(likes.get(f1.getId()), likes.get(f2.getId())))
                .limit(num)
                .collect(Collectors.toList());
    }
}
