package ru.practicum.shareit.exceptions;

public class NotExistException extends RuntimeException {
    public NotExistException(String message) {
        super(message);
    }
}