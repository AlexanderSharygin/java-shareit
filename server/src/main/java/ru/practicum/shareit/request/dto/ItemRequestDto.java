package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import org.hibernate.validator.constraints.Length;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;


@Data
@AllArgsConstructor
public class ItemRequestDto {

    @NonNull
    private Long id;

    @NonNull
    private User owner;


    @Length(max = 200)
    private String description;

    @NonNull
    private LocalDateTime created;

    private List<ItemDto> items;


    public ItemRequestDto() {
    }
}
