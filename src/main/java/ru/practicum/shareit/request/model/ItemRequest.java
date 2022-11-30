package ru.practicum.shareit.request.model;

import lombok.Data;
import ru.practicum.shareit.user.model.User;

import java.time.Instant;

/**
 * TODO Sprint add-item-requests.
 */
@Data
public class ItemRequest {

    private Long id;

    private User owner;

    private String description;

    private Instant createDateTime;

    public ItemRequest() {
    }
}
