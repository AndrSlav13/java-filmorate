package ru.yandex.practicum.filmorate.errors.httpExceptions;

public class ValidationException extends HttpRequestUserException {
    public ValidationException(String str) {
        super(400, str);
    }

    public ValidationException(int code, String str) {
        super(code, str);
    }
}
