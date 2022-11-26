package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import org.hibernate.validator.constraints.Length;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotBlank;

/**
 * TODO Sprint add-controllers.
 */
@Data
@AllArgsConstructor
public class Item {

    @NonNull
    private Long id;

    @NotBlank
    @Length(max = 50)
    private String name;

    @NotBlank
    @Length(max = 200, min = 1)
    private String Description;

    private Boolean available;

    @NonNull
    private User owner;

    private ItemRequest request;

    public Item() {
    }
}
