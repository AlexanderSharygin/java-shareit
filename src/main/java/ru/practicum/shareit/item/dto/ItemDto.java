package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import org.hibernate.validator.constraints.Length;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

@Data
@AllArgsConstructor
public class ItemDto {

    @NonNull
    private Long id;

    @Length(max = 50)
    private String name;

    @Length(max = 200)
    private String description;

    private Boolean available;

    @NonNull
    private User owner;

    private ItemRequest itemRequest;

    public ItemDto() {
    }
}
