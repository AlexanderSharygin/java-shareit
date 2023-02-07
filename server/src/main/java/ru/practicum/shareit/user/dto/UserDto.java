package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;

@Data
@AllArgsConstructor
public class UserDto {
    @NonNull
    private Long id;

    @Pattern(regexp = "^\\S+$")
    private String name;

    @Email
    @Length(max = 200)
    private String email;

    public UserDto() {

    }
}
