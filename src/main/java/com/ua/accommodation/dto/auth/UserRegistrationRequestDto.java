package com.ua.accommodation.dto.auth;

import com.ua.accommodation.validation.FieldMatch;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@FieldMatch(field = "password",
        fieldMatch = "repeatPassword",
        message = "The password fields must match")
@Accessors(chain = true)
public class UserRegistrationRequestDto {
    @NotBlank
    private String email;
    @NotBlank
    @Size(min = 8)
    private String password;
    @NotBlank
    private String repeatPassword;
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
}
