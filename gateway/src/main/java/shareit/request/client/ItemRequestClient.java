package shareit.request.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import shareit.BaseClient;
import shareit.item.dto.ItemDto;
import shareit.request.dto.ItemRequestDto;
import shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class ItemRequestClient extends BaseClient {
    private static final String API_PREFIX = "/requests";

    @Autowired
    public ItemRequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> getAllRequests(Long userId, int from, int size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get("/all?from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> getUserRequests(long userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> getById(long userId, long requestId) {
        return get("/" + requestId, userId);
    }


    public ResponseEntity<Object> create(ItemRequestDto itemRequestDto, long userId) {
        itemRequestDto.setId(-1L);
        itemRequestDto.setOwner(new User());
        itemRequestDto.setCreated(LocalDateTime.now());
        return post("", userId, itemRequestDto);
    }

    public ResponseEntity<Object> update(long itemId, long userId, ItemDto itemDto) {
        itemDto.setId(itemId);
        itemDto.setOwner(new User());
        return patch("/" + itemId, userId, itemDto);
    }
}