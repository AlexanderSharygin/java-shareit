package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import ru.practicum.shareit.booking.dto.BookingInfo;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {


    private Long id;

    @Length(max = 50)
    private String name;

    @Length(max = 200)
    private String description;

    private Boolean available;

    private User owner;

    private ItemRequest request;

    private List<CommentDto> comments;

    private BookingInfo lastBooking;

    private BookingInfo nextBooking;

}
