package ru.yandex.practicum.filmorate.services;

import ru.yandex.practicum.filmorate.controllers.UserController;
import ru.yandex.practicum.filmorate.errors.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;

public class ServiceUser {
    public static void validate(User user, String method) {
        if (method.equals("PUT"))
            if (user.getId() == null || !UserController.getData().contains(user))
                throw new ValidationException(404, "incorrect user id");

        String email = user.getEmail();
        if (email == null || email.trim().isEmpty() || !email.contains("@"))
            throw new ValidationException("wrong email format");

        String login = user.getLogin();
        if (login == null || login.trim().isEmpty() || login.contains(" "))
            throw new ValidationException("wrong login format");

        String name = user.getName();
        if (name == null || name.trim().isEmpty()) user.setName(login);

        LocalDate birthday = user.getBirthday();
        if (birthday.isAfter(LocalDate.now())) throw new ValidationException("wrong birthday format");
    }

    public static void log(HttpServletRequest request) {
        UserController.getLogger().info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
    }

    public static void addUser(User user) {
        UserController.addUser(user);
    }

    public static void updateUser(User user) {
        UserController.updateUser(user);
    }
}