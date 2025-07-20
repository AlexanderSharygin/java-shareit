package shareit.item.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import shareit.booking.dto.BookingInfo;
import shareit.user.model.User;

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

    @NotNull
    private Boolean available;

    private User owner;

    private Long requestId;

    private List<CommentDto> comments;

    private BookingInfo lastBooking;

    private BookingInfo nextBooking;

}
