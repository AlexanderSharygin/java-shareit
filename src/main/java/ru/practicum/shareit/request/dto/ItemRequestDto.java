package ru.practicum.shareit.request.dto;

import lombok.Data;
import lombok.NonNull;
import org.hibernate.validator.constraints.Length;
import ru.practicum.shareit.user.model.User;

import java.time.Instant;


@Data
public class ItemRequestDto {
    @NonNull
    private User owner;

    @NonNull
    @Length(max = 200, min = 1)
    private String description;

    @NonNull
    private Instant createDateTime;

    public ItemRequestDto() {
    }
}
