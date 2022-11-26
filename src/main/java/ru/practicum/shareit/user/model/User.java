package ru.practicum.shareit.user.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;

/**
 * TODO Sprint add-controllers.
 */
@Data
@AllArgsConstructor
public class User {
    @NonNull
    private Long id;

    @Pattern(regexp = "^\\S+$")
    private String name;

    @Email
    private String email;

    public User() {

    }
}
