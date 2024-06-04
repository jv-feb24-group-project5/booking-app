package com.ua.accommodation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.ua.accommodation.model.User;
import com.ua.accommodation.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UserRepositoryTest {

    @Mock
    private UserRepository userRepository;

    private User testUser;
    private User testInvalidUser;

    @BeforeEach
    public void setUp() {
        testUser = new User();
        testUser.setEmail("test@example.com");
        testInvalidUser = new User();
        testInvalidUser.setEmail("notexisting@example.com");
    }

    @Test
    void findByEmail_ExistingEmail_TestUser() {
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));

        Optional<User> foundUser = userRepository.findByEmail(testUser.getEmail());

        assertThat(foundUser).isPresent();
        assertThat(foundUser.get()).isEqualTo(testUser);
    }

    @Test
    void findByEmail_NotExistingEmail_EmptyOptional() {

        when(userRepository.findByEmail(testInvalidUser.getEmail())).thenReturn(Optional.empty());

        Optional<User> foundUser = userRepository.findByEmail(testInvalidUser.getEmail());

        assertThat(foundUser).isEmpty();
    }
}
