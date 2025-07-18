package shareit.request.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import shareit.user.model.User;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class ItemRequest {


    private Long id;


    private User owner;


    private String description;


    private Instant createDateTime;
}