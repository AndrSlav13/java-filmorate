package ru.yandex.practicum.filmorate.services;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface CommonUserInterface {
    Boolean contains(Integer userId);

    User addUser(User user);

    User updateUser(User user);

    List<User> getUsers();

    User getUser(int id);

    User addFriend(int idUser, int idFriend);

    List<User> deleteFriend(int idUser, int idFriend);

    List<User> getFriends(int idUser);

    List<User> getCommonFriends(Integer idUser1, Integer idUser2);
}
