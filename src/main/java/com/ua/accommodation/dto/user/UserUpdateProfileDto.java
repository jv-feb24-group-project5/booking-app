package com.ua.accommodation.dto.user;

import lombok.Data;

@Data
public class UserUpdateProfileDto {
    private String email;
    private String firstName;
    private String lastName;
}
