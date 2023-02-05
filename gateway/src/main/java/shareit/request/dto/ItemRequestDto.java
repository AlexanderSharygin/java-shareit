package shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import org.hibernate.validator.constraints.Length;
import shareit.user.model.User;
import shareit.item.dto.ItemDto;


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
