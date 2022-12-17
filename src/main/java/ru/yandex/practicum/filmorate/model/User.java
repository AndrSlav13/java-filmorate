package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    private Integer id;
    private String email;
    @NotNull
    @Pattern(regexp = "[a-zA-Z][_a-zA-Z0-9]{4,}", message = "wrong login format")
    @NotBlank(message = "login is mandatory")
    private String login;
    private String name;
    private LocalDate birthday;

    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != this.getClass()) return false;
        if (obj == this || ((User) obj).id == this.id) return true;
        return false;
    }
}