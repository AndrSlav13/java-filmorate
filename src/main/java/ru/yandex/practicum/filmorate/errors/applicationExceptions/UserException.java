package ru.yandex.practicum.filmorate.errors.applicationExceptions;

public class UserException extends RuntimeException {
    public UserException(String message) {
        super(message);
    }
}
