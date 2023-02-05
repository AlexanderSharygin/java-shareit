package shareit.booking.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import shareit.booking.client.BookingClient;
import shareit.booking.dto.BookingDto;
import shareit.exception.model.BadRequestException;


import javax.validation.Valid;

@RestController
public class BookingController {
    private final BookingClient bookingClient;

    @Autowired
    public BookingController(BookingClient bookingClient) {
        this.bookingClient = bookingClient;
    }

    @GetMapping("/bookings/{id}")
    public ResponseEntity<Object> getBookingById(@PathVariable("id") Long bookingId,
                                                 @RequestHeader("X-Sharer-User-Id") long userId) {

        return bookingClient.getById(userId, bookingId);
    }

    @GetMapping("/bookings")
    public ResponseEntity<Object> getBookingsForUser(@RequestParam(required = false, defaultValue = "ALL")
                                                     String state,
                                                     @RequestHeader("X-Sharer-User-Id") long userId,
                                                     @RequestParam(required = false, defaultValue = "0") int from,
                                                     @RequestParam(required = false, defaultValue = "100") int size) {
        if (from < 0 || size < 0) {
            throw new BadRequestException("Wrong pagination parameters");
        }
        return bookingClient.getBookingForUser(userId, state, from, size);
    }

    @GetMapping("/bookings/owner")
    public ResponseEntity<Object> getBookingsForUsersItems(@RequestParam(required = false, defaultValue = "ALL")
                                                           String state,
                                                           @RequestHeader("X-Sharer-User-Id") long userId,
                                                           @RequestParam(required = false, defaultValue = "0") int from,
                                                           @RequestParam(required = false, defaultValue = "100") int size) {
        if (from < 0 || size < 0) {
            throw new BadRequestException("Wrong pagination parameters");
        }

        return bookingClient.getBookingForUsersItems(userId, state, from, size);
    }

    @PostMapping(value = "/bookings")
    public ResponseEntity<Object> create(@Valid @RequestBody BookingDto bookingDto,
                                         @RequestHeader("X-Sharer-User-Id") long userId) {

        return bookingClient.create(userId, bookingDto);
    }

    @PatchMapping("/bookings/{id}")
    public ResponseEntity<Object> updateStatus(@PathVariable("id") Long bookingId,
                                               @RequestHeader("X-Sharer-User-Id") long userId,
                                               @RequestParam Boolean approved) {

        return bookingClient.changeBookingStatus(bookingId, userId, approved);
    }
}