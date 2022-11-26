package ru.practicum.shareit.exception.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exception.model.*;

import java.util.List;
import java.util.logging.Level;

@RestControllerAdvice
@Slf4j
public class ExceptionApiHandler {

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse customValidation(ValidationException exception) {
        log.warn(exception.getMessage());

        return new ErrorResponse(exception.getMessage(), "Validation error");
    }

    @ExceptionHandler(AlreadyExistException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse entityIsAlreadyExist(AlreadyExistException exception) {
        log.warn(exception.getMessage());

        return new ErrorResponse(exception.getMessage(), "Entity is already exist!");
    }

    @ExceptionHandler(NotExistException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse entityIsNotExist(NotExistException exception) {
        log.warn(exception.getMessage());

        return new ErrorResponse(exception.getMessage(), "Entity is not found!");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse commonValidation(MethodArgumentNotValidException e) {
        List<FieldError> items = e.getBindingResult().getFieldErrors();
        String message = items.stream()
                .map(FieldError::getField)
                .findFirst().get() + " - "
                + items.stream()
                .map(FieldError::getDefaultMessage)
                .findFirst().get();
        log.warn(message);

        return new ErrorResponse(message, "Validation error");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleOtherExceptions(final Throwable e) {
        log.warn(e.getMessage());

        return new ErrorResponse(e.getMessage(), "Unknown error");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIncorrectParameterException(final BadRequestException e) {
        log.warn(e.getMessage());

        return new ErrorResponse(e.getMessage(), "Invalid parameter - " + e.getParameter());
    }
}