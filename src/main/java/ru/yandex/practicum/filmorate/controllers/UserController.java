package ru.yandex.practicum.filmorate.controllers;

import com.google.gson.JsonObject;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.errors.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
@Slf4j
public class UserController {
    private int baseId = 0;
    private final Map<Integer, User> users = new HashMap<>();

    @GetMapping("/users")
    public ArrayList<User> getUsers(){
        return new ArrayList<User>(users.values());
    }

    @PostMapping("/users")
    public User setUser(@Valid @RequestBody User user, HttpServletRequest request, HttpServletResponse response){
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
        validate(user, request.getMethod());

        ++baseId;
        user.setId(baseId);

        users.put(baseId, user);
        return users.get(baseId);
    }

    @PutMapping("/users")
    public User updateUser(@Valid @RequestBody User user, HttpServletRequest request, HttpServletResponse response){
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
        validate(user, request.getMethod());

        Integer id = user.getId();

        users.put(id, user);
        return users.get(id);
    }

    private void validate(User user, String method){
        if(method.equals("PUT"))
            if(user.getId() == null || !users.containsKey(user.getId()))
                throw new ValidationException(404, "incorrect user id");

        String email = user.getEmail();
            if(email == null || email.trim().isEmpty() || !email.contains("@")) throw new ValidationException("wrong email format");

        String login = user.getLogin();
            if(login == null || login.trim().isEmpty() || login.contains(" ")) throw new ValidationException("wrong login format");

        String name = user.getName();
            if(name == null || name.trim().isEmpty()) user.setName(login);

        LocalDate birthday = user.getBirthday();
            if(birthday.isAfter(LocalDate.now())) throw new ValidationException("wrong birthday format");
    }
}