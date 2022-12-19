package ru.yandex.practicum.filmorate.model;

import lombok.*;
import ru.yandex.practicum.filmorate.util.Genre;
import ru.yandex.practicum.filmorate.util.MPA;

import java.time.LocalDate;
import java.util.LinkedHashSet;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Film {
    protected Integer id;
    protected String name;
    protected String description;
    protected LocalDate releaseDate;
    protected Integer duration;
    protected LinkedHashSet<Genre> genres;
    protected MPA mpa;

    public void addGenre(Genre g) {
        if (genres == null) genres = new LinkedHashSet<>();
        genres.add(g);
    }

    public static final int filmDescriptionMaxLength = 200;
    public static final LocalDate theEarliestPossibleDate = LocalDate.of(1895, 12, 28);

    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != this.getClass()) return false;
        if (obj == this || ((Film) obj).id == this.id) return true;
        return false;
    }

}