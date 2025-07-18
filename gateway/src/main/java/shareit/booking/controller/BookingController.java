package shareit.booking.controller;


import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shareit.booking.client.BookingClient;
import shareit.booking.dto.BookingDto;
import shareit.exception.model.BadRequestException;

@RestController
public class BookingController {
    private final BookingClient bookingClient;

    @Autowired
    public BookingController(BookingClient bookingClient) {
        this.bookingClient = bookingClient;
    }


    @PostMapping(value = "/bookings")
    public ResponseEntity<Object> create(@Valid @RequestBody BookingDto bookingDto,
                                         @RequestHeader("X-Sharer-User-Id") long userId) {
        return bookingClient.create(userId, bookingDto);
    }

    @PatchMapping("/bookings/{id}")
    public ResponseEntity<Object> changeBookingStatus(@PathVariable("id") Long bookingId,
                                                      @RequestHeader("X-Sharer-User-Id") long userId,
                                                      @RequestParam Boolean approved) {
        return bookingClient.changeBookingStatus(bookingId, userId, approved);
    }

    @GetMapping("/bookings/{id}")
    public ResponseEntity<Object> getBooking(@RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId,
                                             @PathVariable("id") Long bookingId) {
        return bookingClient.getById(userId,bookingId);
    }

    @GetMapping("/bookings")
    public ResponseEntity<Object> getBookingsForUser(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                     @RequestParam(required = false, defaultValue = "ALL") String state,
                                                     @RequestParam(required = false, defaultValue = "0") int from,
                                                     @RequestParam(required = false, defaultValue = "100") int size) {
        if (from < 0 || size < 0) {
            throw new BadRequestException("Неверные параметры пагинации");
        }
        return bookingClient.getBookingForUser(userId, state, from, size);
    }

    @GetMapping("/bookings/owner")
    public ResponseEntity<Object> getBookingsForUserItems(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                          @RequestParam(required = false, defaultValue = "ALL") String state,
                                                          @RequestParam(required = false, defaultValue = "0") int from,
                                                          @RequestParam(required = false, defaultValue = "100") int size) {
        if (from < 0 || size < 0) {
            throw new BadRequestException("Неверные параметры пагинации");
        }

        return bookingClient.getBookingForUsersItems(userId, state, from, size);
    }
}