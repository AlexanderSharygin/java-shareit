package ru.practicum.shareit.exception.model;

import lombok.Getter;

@Getter
public class BadRequestException extends RuntimeException {
    private final String parameter;

    public BadRequestException(String parameter) {
        this.parameter = parameter;
    }
}
