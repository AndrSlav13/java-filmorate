package ru.yandex.practicum.filmorate.errors;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static ru.yandex.practicum.filmorate.util.AdaptersAndFormat.gson;
import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class GlobalDefaultExceptionHandler {
    //public static final String DEFAULT_ERROR_VIEW = "error";

    @ExceptionHandler(value = HttpRequestUserException.class)
    //public ModelAndView
    @ResponseBody
    public String
    defaultErrorHandler(HttpServletRequest req, HttpServletResponse resp, HttpRequestUserException e) throws Exception {
        // If the exception is annotated with @ResponseStatus rethrow it and let
        // the framework handle it - like the OrderNotFoundException example
        // at the start of this post.
        // AnnotationUtils is a Spring Framework utility class.
        if (AnnotationUtils.findAnnotation
                (e.getClass(), ResponseStatus.class) != null)
            throw e;

        // Otherwise setup and send the user to a default error-view.
        //ModelAndView mav = new ModelAndView();
        //mav.addObject("exception", e);
        //mav.addObject("url", req.getRequestURL());
        //mav.setViewName(DEFAULT_ERROR_VIEW);
        //return mav;
        resp.setStatus(e.getCode());
        log.error("ERROR: " + e.getMessage() + "     CODE: " + e.getCode());

        return gson.toJson(e.getMessage());
    }

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public String
    defaultErrorHandler(HttpServletRequest req, HttpServletResponse resp, Exception e) throws Exception {
        // If the exception is annotated with @ResponseStatus rethrow it and let
        // the framework handle it - like the OrderNotFoundException example
        // at the start of this post.
        // AnnotationUtils is a Spring Framework utility class.
        if (AnnotationUtils.findAnnotation
                (e.getClass(), ResponseStatus.class) != null)
            throw e;

        // Otherwise setup and send the user to a default error-view.
        //ModelAndView mav = new ModelAndView();
        //mav.addObject("exception", e);
        //mav.addObject("url", req.getRequestURL());
        //mav.setViewName(DEFAULT_ERROR_VIEW);
        //return mav;
        resp.setStatus(500);
        log.error("ERROR: " + e.getMessage());

        return gson.toJson(e.getMessage());
    }
}