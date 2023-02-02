package shareit.booking.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import shareit.BaseClient;

import java.util.Map;

@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> getById(long userId, Long bookingId) {
        return get("/" + bookingId, userId);
    }

    public ResponseEntity<Object> getBookingForUser(long userId, String status, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "state", status,
                "from", from,
                "size", size
        );
        return get("?state={state}&from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> getBookingForUsersItems(long userId, String status, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "state", status,
                "from", from,
                "size", size
        );
        return get("/owner?state={state}&from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> create(long userId, BookingDto bookingDto) {
        bookingDto.setId(-1L);
        bookingDto.setStatus(BookingStatus.WAITING);
        bookingDto.setBooker(new User());
        bookingDto.setItem(new Item());
        return post("", userId, bookingDto);
    }

    public ResponseEntity<Object> changeBookingStatus(Long bookingId, long userId, Boolean approved) {
        return patch("/" + bookingId + "?approved=" + approved, userId);
    }
}
