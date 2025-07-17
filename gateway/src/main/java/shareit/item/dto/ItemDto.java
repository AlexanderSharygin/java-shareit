package shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import org.hibernate.validator.constraints.Length;
import shareit.booking.dto.BookingInfo;
import shareit.user.model.User;

import java.util.List;

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

    private Long requestId;

    private BookingInfo lastBooking;

    private BookingInfo nextBooking;


    private List<CommentDto> comments;

    public ItemDto() {
    }
}
