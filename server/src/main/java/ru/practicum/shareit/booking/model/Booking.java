package ru.practicum.shareit.booking.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.Instant;

@Getter
@Setter
@ToString
@AllArgsConstructor
@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Instant startDateTime;

    private Instant endDateTime;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;


    @ManyToOne
    private Item item;

    @ManyToOne
    @JoinColumn(name = "booker_id")
    private User booker;

    public Booking() {
    }
}

