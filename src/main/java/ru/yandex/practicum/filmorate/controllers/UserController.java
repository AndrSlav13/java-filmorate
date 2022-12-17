package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.services.UserService;
import ru.yandex.practicum.filmorate.services.UserServiceInterface;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
public class UserController {
    private final UserServiceInterface userService;

    public static org.slf4j.Logger getLogger() {
        return log;
    }

    @Autowired
    public UserController(UserService userService, HttpServletRequest request) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public ArrayList<User> getUsers(HttpServletRequest request) {
        userService.log(request);
        return new ArrayList<>(userService.getUsers());
    }

    @GetMapping("/users/{userId}")
    public User getUser(@PathVariable(value = "userId") int id, HttpServletRequest request) {
        userService.log(request);
        userService.validateUserExists(id);
        return userService.getUser(id);
    }

    @GetMapping("/users/{userId}/friends")
    public List<User> getFriends(@PathVariable(value = "userId") int id, HttpServletRequest request) {
        userService.log(request);
        return userService.getFriends(id);
    }

    @GetMapping("/users/{user1Id}/friends/common/{user2Id}")
    public List<User> getCommonFriends(@PathVariable int user1Id, @PathVariable int user2Id, HttpServletRequest request) {
        userService.log(request);
        return userService.getCommonFriends(user1Id, user2Id);
    }

    @PostMapping("/users")
    public User addUser(@Valid @RequestBody User user, HttpServletRequest request) {
        userService.log(request);
        userService.validateUserFormat(user, request.getMethod());
        userService.addUser(user);
        return user;
    }

    @PutMapping("/users")
    public User updateUser(@Valid @RequestBody User user, HttpServletRequest request) {
        userService.log(request);
        userService.validateUserFormat(user, request.getMethod());
        userService.updateUser(user);
        return user;
    }

    @PutMapping("/users/{userId}/friends/{friendId}")
    public User addFriend(@PathVariable int userId, @PathVariable int friendId, HttpServletRequest request) {
        userService.log(request);
        userService.validateUserExists(userId);
        userService.validateUserExists(friendId);
        return userService.addFriend(userId, friendId);
    }

    @DeleteMapping("/users/{userId}/friends/{friendId}")
    public List<User> deleteFriend(@PathVariable int userId, @PathVariable int friendId, HttpServletRequest request) {
        userService.log(request);
        return userService.deleteFriend(userId, friendId);
    }


}