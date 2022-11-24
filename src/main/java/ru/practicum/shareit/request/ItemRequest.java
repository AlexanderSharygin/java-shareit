package ru.practicum.shareit.request;

import lombok.Data;
import lombok.NonNull;
import org.hibernate.validator.constraints.Length;
import ru.practicum.shareit.user.User;

import java.time.Instant;

/**
 * TODO Sprint add-item-requests.
 */
@Data
public class ItemRequest {

    @NonNull
    private Long id;

    @NonNull
    private User owner;

    @NonNull
    @Length(max = 200, min = 1)
    private String description;

    @NonNull
    private Instant createDateTime;

    public ItemRequest() {
    }

}
