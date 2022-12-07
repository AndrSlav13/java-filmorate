package ru.yandex.practicum.filmorate.errors.httpExceptions;

public class StorageException extends HttpRequestUserException {
    public StorageException(int code, String msg) {
        super(code, msg);
    }

    public StorageException(String msg) {
        super(409, msg);
    }
}
