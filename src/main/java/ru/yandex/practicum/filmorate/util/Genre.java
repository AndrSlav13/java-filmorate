package ru.yandex.practicum.filmorate.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Genre {
    private int id;
    private String name;

    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != this.getClass()) return false;
        if (obj == this || ((Genre) obj).id == this.id) return true;
        return false;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
