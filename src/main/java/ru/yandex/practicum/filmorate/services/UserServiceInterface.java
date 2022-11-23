package ru.yandex.practicum.filmorate.services;

import ru.yandex.practicum.filmorate.model.User;

import javax.servlet.http.HttpServletRequest;

public interface UserServiceInterface extends CommonUserInterface {
    void validateUserFormat(User user, String method);

    void validateUserExists(int userId);

    void log(HttpServletRequest request);
}
