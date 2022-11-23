package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.services.CommonUserInterface;

public interface UserStorageInterface extends CommonUserInterface {
    Integer getSize();

    Boolean isEmpty();

    Boolean contains(User user);

    Boolean contains(Integer userId);
}
