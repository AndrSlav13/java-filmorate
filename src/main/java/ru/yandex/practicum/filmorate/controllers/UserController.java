package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.services.ServiceUser;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;

@RestController
@Slf4j
public class UserController {
    public static org.slf4j.Logger getLogger() {
        return log;
    }

    @GetMapping("/users")
    public ArrayList<User> getUsers() {
        return ServiceUser.getUsers();
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