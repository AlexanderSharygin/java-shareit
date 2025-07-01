package ru.practicum.shareit.booking.dto;

public enum BookingDtoStatus {
    ALL("ALL"),
    CURRENT("CURRENT"),
    PAST("PAST"),
    FUTURE("FUTURE"),
    WAITING("WAITING"),
    REJECTED("REJECTED");

    private final String name;

    BookingDtoStatus(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
