package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;

/**
 * TODO Sprint add-controllers.
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@Entity
@Table(name="items")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;
    
    private Boolean available;

    @ManyToOne
    @JoinColumn(name= "owner_id")
    private User owner;

    @OneToOne
    private ItemRequest itemRequest;


    public Item() {
    }
}
