package ru.yandex.practicum.filmorate.errors;

public class ValidationException extends HttpRequestUserException {
    public ValidationException(String str) {
        super(str);
    }

    public ValidationException(int code, String str) {
        super(code, str);
    }
}
