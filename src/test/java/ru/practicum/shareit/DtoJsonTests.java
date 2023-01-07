package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingInfo;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class DtoJsonTests {
    @Autowired
    private JacksonTester<UserDto> jsoUserDto;

    @Autowired
    private JacksonTester<ItemDto> jsonItemDto;

    @Autowired
    private JacksonTester<BookingDto> jsonBookingDto;

    @Autowired
    private JacksonTester<ItemRequestDto> requestDto;

    @Test
    void testUserDto() throws Exception {
        UserDto userDto = new UserDto(
                1L,
                "Test",
                "Test.tester@mail.com");
        JsonContent<UserDto> result = jsoUserDto.write(userDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Test");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("Test.tester@mail.com");
    }

    @Test
    void testItemDtoFullJson() throws Exception {
        User user = new User(111L, "Name", "wer@qwe.cd");
        LocalDateTime lastBookingStartTime = LocalDateTime.now().minusDays(2);
        LocalDateTime lastBookingEndTime = LocalDateTime.now().minusDays(1);
        LocalDateTime nextBookingStartTime = LocalDateTime.now().plusDays(1);
        LocalDateTime nextBookingEndTime = LocalDateTime.now().plusDays(2);
        BookingInfo lastBooking = new BookingInfo(11L, lastBookingStartTime, lastBookingEndTime, 23L);
        BookingInfo nextBooking = new BookingInfo(11L, nextBookingStartTime, nextBookingEndTime, 24L);
        CommentDto comment1 = new CommentDto(1L, "Comment1", "user", LocalDateTime.now().minusHours(3));
        CommentDto comment2 = new CommentDto(2L, "Comment2", "user", LocalDateTime.now().minusHours(2));
        ItemDto itemDto = new ItemDto(1L, "Test", "Description", true, user,
                25L, lastBooking, nextBooking, List.of(comment1, comment2));

        JsonContent<ItemDto> result = jsonItemDto.write(itemDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Test");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Description");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.owner.id").isEqualTo(111);
        assertThat(result).extractingJsonPathStringValue("$.owner.name").isEqualTo("Name");
        assertThat(result).extractingJsonPathStringValue("$.owner.email").isEqualTo("wer@qwe.cd");
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(25);
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.id").isEqualTo(11);
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.bookerId").isEqualTo(23);
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.id").isEqualTo(11);
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.bookerId").isEqualTo(24);
        assertThat(result).extractingJsonPathNumberValue("$.comments.[0].id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.comments.[0].text").isEqualTo("Comment1");
        assertThat(result).extractingJsonPathStringValue("$.comments.[0].authorName").isEqualTo("user");
        assertThat(result).extractingJsonPathNumberValue("$.comments.[1].id").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.comments.[1].text").isEqualTo("Comment2");
        assertThat(result).extractingJsonPathStringValue("$.comments.[1].authorName").isEqualTo("user");
    }

    @Test
    void testItemDtoShortJson() throws Exception {
        User user = new User(111L, "Name", "wer@qwe.cd");
        ItemDto itemDto = new ItemDto(1L, "Test", "Description", true, user, null,
                null, null, new ArrayList<>());

        JsonContent<ItemDto> result = jsonItemDto.write(itemDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Test");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Description");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.owner.id").isEqualTo(111);
        assertThat(result).extractingJsonPathStringValue("$.owner.name").isEqualTo("Name");
        assertThat(result).extractingJsonPathStringValue("$.owner.email").isEqualTo("wer@qwe.cd");
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(null);
        assertThat(result).extractingJsonPathStringValue("$.lastBooking").isEqualTo(null);
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking").isEqualTo(null);
    }

    @Test
    void testBookingDtoFullJson() throws Exception {
        User user = new User(111L, "Name", "wer@qwe.cd");
        ItemRequest itemRequest = new ItemRequest(23L, user, "Ir", Instant.now());
        Item item = new Item(3L, "Name", "Desc", true, user, itemRequest);
        LocalDateTime bookingStartTime = LocalDateTime.now().plusDays(1);
        LocalDateTime bookingEndTime = LocalDateTime.now().plusDays(2);

        BookingDto bookingDto = new BookingDto(1L, bookingStartTime, bookingEndTime, BookingStatus.WAITING, 3L,
                user, item);
        JsonContent<BookingDto> result = jsonBookingDto.write(bookingDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("WAITING");
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(3);
        assertThat(result).extractingJsonPathNumberValue("$.booker.id").isEqualTo(111);
        assertThat(result).extractingJsonPathStringValue("$.booker.name").isEqualTo("Name");
        assertThat(result).extractingJsonPathStringValue("$.booker.email").isEqualTo("wer@qwe.cd");
        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo(3);
        assertThat(result).extractingJsonPathStringValue("$.item.name").isEqualTo("Name");
        assertThat(result).extractingJsonPathStringValue("$.item.description").isEqualTo("Desc");
        assertThat(result).extractingJsonPathBooleanValue("$.item.available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.item.owner.id").isEqualTo(111);
        assertThat(result).extractingJsonPathStringValue("$.item.owner.name").isEqualTo("Name");
        assertThat(result).extractingJsonPathStringValue("$.item.owner.email").isEqualTo("wer@qwe.cd");
        assertThat(result).extractingJsonPathNumberValue("$.item.itemRequest.id").isEqualTo(23);
        assertThat(result).extractingJsonPathStringValue("$.item.itemRequest.description").isEqualTo("Ir");
    }

    @Test
    void testRequestDtoJson() throws Exception {
        User user = new User(111L, "Name", "wer@qwe.cd");
        LocalDateTime requestTime = LocalDateTime.now().minusDays(2);
        LocalDateTime lastBookingEndTime = LocalDateTime.now().minusDays(1);
        LocalDateTime nextBookingStartTime = LocalDateTime.now().plusDays(1);
        LocalDateTime nextBookingEndTime = LocalDateTime.now().plusDays(2);
        BookingInfo lastBooking = new BookingInfo(11L, requestTime, lastBookingEndTime, 23L);
        BookingInfo nextBooking = new BookingInfo(11L, nextBookingStartTime, nextBookingEndTime, 24L);
        CommentDto comment1 = new CommentDto(1L, "Comment1", "user", LocalDateTime.now().minusHours(3));
        CommentDto comment2 = new CommentDto(2L, "Comment2", "user", LocalDateTime.now().minusHours(2));
        ItemDto itemDto = new ItemDto(1L, "Test", "Description", true, user,
                25L, lastBooking, nextBooking, List.of(comment1, comment2));

        ItemRequestDto itemRequestDto = new ItemRequestDto(1L, user, "Desc", requestTime, List.of(itemDto));
        JsonContent<ItemRequestDto> result = requestDto.write(itemRequestDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.owner.id").isEqualTo(111);
        assertThat(result).extractingJsonPathStringValue("$.owner.name").isEqualTo("Name");
        assertThat(result).extractingJsonPathStringValue("$.owner.email").isEqualTo("wer@qwe.cd");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Desc");
        assertThat(result).extractingJsonPathNumberValue("$.items.[0].id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.items.[0].name").isEqualTo("Test");
        assertThat(result).extractingJsonPathStringValue("$.items.[0].description").isEqualTo("Description");
        assertThat(result).extractingJsonPathBooleanValue("$.items.[0].available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.items.[0].owner.id").isEqualTo(111);
        assertThat(result).extractingJsonPathStringValue("$.items.[0].owner.name").isEqualTo("Name");
        assertThat(result).extractingJsonPathStringValue("$.items.[0].owner.email").isEqualTo("wer@qwe.cd");
        assertThat(result).extractingJsonPathNumberValue("$.items.[0].requestId").isEqualTo(25);
        assertThat(result).extractingJsonPathNumberValue("$.items.[0].lastBooking.id").isEqualTo(11);
        assertThat(result).extractingJsonPathNumberValue("$.items.[0].lastBooking.bookerId").isEqualTo(23);
        assertThat(result).extractingJsonPathNumberValue("$.items.[0].nextBooking.id").isEqualTo(11);
        assertThat(result).extractingJsonPathNumberValue("$.items.[0].nextBooking.bookerId").isEqualTo(24);
        assertThat(result).extractingJsonPathNumberValue("$.items.[0].comments.[0].id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.items.[0].comments.[0].text").isEqualTo("Comment1");
        assertThat(result).extractingJsonPathStringValue("$.items.[0].comments.[0].authorName").isEqualTo("user");
        assertThat(result).extractingJsonPathNumberValue("$.items.[0].comments.[1].id").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.items.[0].comments.[1].text").isEqualTo("Comment2");
        assertThat(result).extractingJsonPathStringValue("$.items.[0].comments.[1].authorName").isEqualTo("user");
    }
}