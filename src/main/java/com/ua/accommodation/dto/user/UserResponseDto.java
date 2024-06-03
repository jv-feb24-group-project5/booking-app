package com.ua.accommodation.dto.user;

import static com.ua.accommodation.model.Role.RoleName;

import java.util.HashSet;
import java.util.Set;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
public class UserResponseDto {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<RoleName> roles = new HashSet<>();
}
