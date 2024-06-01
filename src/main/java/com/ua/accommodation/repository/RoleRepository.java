package com.ua.accommodation.repository;

import com.ua.accommodation.model.Role;
import com.ua.accommodation.model.Role.RoleName;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Set<Role> getAllByNameIn(Set<RoleName> roleNames);
}
