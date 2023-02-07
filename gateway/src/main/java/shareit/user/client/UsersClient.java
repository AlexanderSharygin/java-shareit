package shareit.user.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import shareit.BaseClient;
import shareit.user.dto.UserDto;

@Service
public class UsersClient extends BaseClient {
    private static final String API_PREFIX = "/users";

    @Autowired
    public UsersClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> getAll() {
        return get("");
    }

    public ResponseEntity<Object> getById(long userId) {
        return get("/" + userId);
    }

    public ResponseEntity<Object> create(UserDto userDto) {
        userDto.setId(-1L);
        return post("", userDto);
    }

    public ResponseEntity<Object> update(long userId, UserDto userDto) {
        userDto.setId(userId);
        return patch("/" + userId, userDto);
    }

    public ResponseEntity<Object> delete(long userId) {
        return delete("/" + userId);
    }
}