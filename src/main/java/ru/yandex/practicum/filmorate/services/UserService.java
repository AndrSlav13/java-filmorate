package ru.yandex.practicum.filmorate.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controllers.UserController;
import ru.yandex.practicum.filmorate.errors.httpExceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorageInterface;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserService implements UserServiceInterface {
    private final UserStorageInterface userData;
    private static final org.slf4j.Logger log = UserController.getLogger();

    @Autowired
    public UserService(InMemoryUserStorage userData) {
        this.userData = userData;
    }

    @Override
    public void validateUserFormat(User user, String method) {
        if (method.equals("PUT"))
            if (user.getId() == null || !userData.contains(user))
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

    @Override
    public void validateUserExists(int userId) {
        if (!userData.contains(userId))
            throw new ValidationException(404, "user id=" + userId + " doesn't exist");
    }

    public void log(HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
    }

    @Override
    public User addUser(User user) {
        userData.addUser(user);
        return userData.getUser(user.getId());
    }

    @Override
    public User updateUser(User user) {
        userData.updateUser(user);
        return userData.getUser(user.getId());
    }

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(userData.getUsers());
    }

    @Override
    public User getUser(int id) {
        return userData.getUser(id);
    }

    @Override
    public User addFriend(int idUser, int idFriend) {
        return userData.addFriend(idUser, idFriend);
    }

    @Override
    public List<User> deleteFriend(int idUser, int idFriend) {
        return userData.deleteFriend(idUser, idFriend);
    }

    @Override
    public List<User> getFriends(int idUser) {
        return userData.getFriends(idUser);
    }

    @Override
    public List<User> getCommonFriends(int idUser1, int idUser2) {
        return userData.getCommonFriends(idUser1, idUser2);
    }
}