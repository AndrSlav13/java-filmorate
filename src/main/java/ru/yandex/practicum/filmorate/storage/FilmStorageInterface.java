package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.services.CommonFilmInterface;

public interface FilmStorageInterface extends CommonFilmInterface {
    Integer getSize();

    Boolean isEmpty();

    Boolean contains(Film film);

    Boolean contains(Integer filmId);
}

