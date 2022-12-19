package ru.yandex.practicum.filmorate.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MPA {
    private int id;
    private String name;

    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != this.getClass()) return false;
        if (obj == this || ((MPA) obj).id == this.id) return true;
        return false;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
