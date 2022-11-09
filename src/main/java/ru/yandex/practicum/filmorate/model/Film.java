package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.LocalDate;

@Data
public class Film {
    private Integer id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Integer duration;

    public static final int filmDescriptionMaxLength = 200;
    public static final LocalDate theEarliestPossibleDate = LocalDate.of(1895, 12, 28);

    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != this.getClass()) return false;
        if (obj == this || ((Film) obj).id == this.id) return true;
        return false;
    }

}