package com.ua.accommodation.dto.user;

import com.ua.accommodation.model.Role;
import jakarta.validation.constraints.NotNull;
import java.util.Set;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
public class UserUpdateRoleDto {
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @NotNull
    private Set<Role> roles;
}
