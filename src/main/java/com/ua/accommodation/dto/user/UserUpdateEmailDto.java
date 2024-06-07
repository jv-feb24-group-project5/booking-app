package com.ua.accommodation.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserUpdateEmailDto {
    @NotBlank
    private String newEmail;
}
