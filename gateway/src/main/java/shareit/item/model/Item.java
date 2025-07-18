package shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import shareit.request.model.ItemRequest;
import shareit.user.model.User;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class Item {


    private Long id;


    private String name;


    private String description;


    private Boolean available;


    private User owner;


    private ItemRequest request;
}
