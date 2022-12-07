package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.errors.httpExceptions.StorageException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class InMemoryUserStorage implements UserStorageInterface {
    private static int baseId = 0;
    private final Map<Integer, User> users = new HashMap<>();
    private final Map<Integer, HashSet<Integer>> friends = new HashMap<>();

    @Override
    public Integer getSize() {
        return users.size();
    }

    @Override
    public Boolean isEmpty() {
        return users.isEmpty();
    }

    @Override
    public Boolean contains(User user) {
        return users.containsValue(user);
    }

    @Override
    public Boolean contains(Integer userId) {
        return users.containsKey(userId);
    }

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getUser(int id) {
        return users.get(id);
    }

    @Override
    public User addUser(User user) {
        if (contains(user)) throw new StorageException("user with those credentials just exists");
        users.put(++baseId, user);
        user.setId(baseId);
        return users.get(baseId);
    }

    @Override
    public User updateUser(User user) {
        users.put(user.getId(), user);
        return users.get(user.getId());
    }

    @Override
    public User addFriend(int idUser, int idFriend) {
        if (!friends.containsKey(idUser)) friends.put(idUser, new HashSet<>());
        friends.get(idUser).add(idFriend);
        if (friends.get(idUser).contains(idFriend))
            return users.get(idFriend);
        throw new StorageException("friend wasn't added");
    }

    @Override
    public List<User> deleteFriend(int idUser, int idFriend) {
        if (friends.containsKey(idUser)) friends.get(idUser).remove(idFriend);
        return friends.get(idUser).stream().map(id -> users.get(id)).collect(Collectors.toList());
    }

    @Override
    public List<User> getFriends(int idUser) {
        if (friends.get(idUser) == null) return List.of();
        return friends.get(idUser).stream().map(id -> users.get(id)).collect(Collectors.toList());
    }

    @Override
    public List<User> getCommonFriends(int idUser1, int idUser2) {
        if (friends.get(idUser1) == null || friends.get(idUser2) == null) return List.of();
        Stream<Integer> stream = friends.get(idUser1).stream();
        List<Integer> lst = friends.get(idUser2).stream().collect(Collectors.toList());

        return
                stream.flatMap(
                                userId -> {
                                    return lst.contains(userId) ? Stream.of(userId) : Stream.of();
                                }
                        )
                        .map(id -> users.get(id))
                        .collect(Collectors.toList());

    }
}
