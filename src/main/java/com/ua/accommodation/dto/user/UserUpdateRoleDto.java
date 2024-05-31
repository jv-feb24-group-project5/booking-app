package com.ua.accommodation.dto.user;

import com.ua.accommodation.model.Role;
import java.util.Set;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
public class UserUpdateRoleDto {
    private Long id;
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Role> roles;
}
