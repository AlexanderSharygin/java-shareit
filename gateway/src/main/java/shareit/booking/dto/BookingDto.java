package shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;



import shareit.item.model.Item;
import shareit.user.model.User;
import shareit.booking.BookingStatus;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BookingDto {
    @NonNull
    private Long id;

    @NonNull
    private LocalDateTime start;

    @NonNull
    private LocalDateTime end;

    @NonNull
    private BookingStatus status;

    @NonNull
    private Long itemId;

    @NonNull
    private User booker;

    @NonNull
    private Item item;

    public BookingDto() {
    }
}