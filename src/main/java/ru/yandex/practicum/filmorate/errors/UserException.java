package ru.yandex.practicum.filmorate.errors;

public class UserException extends RuntimeException {
    public UserException(String message) {
        super(message);
    }
}
