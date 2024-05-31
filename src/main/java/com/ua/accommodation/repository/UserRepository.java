package com.ua.accommodation.repository;

import com.ua.accommodation.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
