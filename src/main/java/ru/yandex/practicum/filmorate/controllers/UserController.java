package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.services.ServiceUser;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
public class UserController {
    private static int baseId = 0;
    private static final Map<Integer, User> users = new HashMap<>();

    public static Collection<User> getData() {
        return users.values();
    }

    public static org.slf4j.Logger getLogger() {
        return log;
    }

    public static int getNewId() {
        return ++baseId;
    }

    public static void addUser(User user) {
        user.setId(getNewId());
        users.put(user.getId(), user);
    }

    public static void updateUser(User user) {
        users.put(user.getId(), user);
    }

    @GetMapping("/users")
    public ArrayList<User> getUsers() {
        return new ArrayList<User>(users.values());
    }

    @PostMapping("/users")
    public User setUser(@Valid @RequestBody User user, HttpServletRequest request) {
        ServiceUser.log(request);
        ServiceUser.validate(user, request.getMethod());
        ServiceUser.addUser(user);
        return user;
    }

    @PutMapping("/users")
    public User updateUser(@Valid @RequestBody User user, HttpServletRequest request) {
        ServiceUser.log(request);
        ServiceUser.validate(user, request.getMethod());
        ServiceUser.updateUser(user);
        return user;
    }


}