package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@AllArgsConstructor
public class UserDto {
    @NonNull
    private Long id;

    @Pattern(regexp = "^\\S+$")
    private String name;

    @Email
    private String email;

    public UserDto() {

    }
}
