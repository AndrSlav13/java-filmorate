package ru.yandex.practicum.filmorate.errors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static ru.yandex.practicum.filmorate.util.AdaptersAndFormat.gson;

@ControllerAdvice
@Slf4j
public class GlobalDefaultExceptionHandler {

    @ExceptionHandler(value = HttpRequestUserException.class)
    @ResponseBody
    public String
    defaultErrorHandler(HttpServletRequest req, HttpServletResponse resp, HttpRequestUserException e) throws Exception {
        if (AnnotationUtils.findAnnotation
                (e.getClass(), ResponseStatus.class) != null)
            throw e;

        resp.setStatus(e.getCode());
        log.error("ERROR: " + e.getMessage() + "     CODE: " + e.getCode());

        return gson.toJson(e.getMessage());
    }

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public String
    defaultErrorHandler(HttpServletRequest req, HttpServletResponse resp, Exception e) throws Exception {
        if (AnnotationUtils.findAnnotation
                (e.getClass(), ResponseStatus.class) != null)
            throw e;

        resp.setStatus(500);
        log.error("ERROR: " + e.getMessage());

        return gson.toJson(e.getMessage());
    }
}