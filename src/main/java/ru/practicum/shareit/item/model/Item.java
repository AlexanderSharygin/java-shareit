package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import org.hibernate.validator.constraints.Length;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

/**
 * TODO Sprint add-controllers.
 */
@Data
@AllArgsConstructor
public class Item {

    @NonNull
    private Long id;

    @Length(max = 50)
    private String name;

    @Length(max = 200)
    private String description;

    private Boolean available;

    @NonNull
    private User owner;

    private ItemRequest request;

    public Item() {
    }
}
