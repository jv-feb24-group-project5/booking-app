package com.ua.accommodation;

import static com.ua.accommodation.model.Role.RoleName;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ua.accommodation.dto.user.UserResponseDto;
import com.ua.accommodation.dto.user.UserUpdateProfileDto;
import com.ua.accommodation.dto.user.UserUpdateRoleDto;
import com.ua.accommodation.mapper.UserMapper;
import com.ua.accommodation.model.Role;
import com.ua.accommodation.model.User;
import com.ua.accommodation.repository.UserRepository;
import com.ua.accommodation.service.impl.UserServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private User nonExistingUser;
    private UserResponseDto userResponseDto;
    private UserUpdateRoleDto userUpdateRoleDto;
    private UserUpdateProfileDto userUpdateProfileDto;
    private UserResponseDto userUpdatedResponseDto;
    private Set<Role> roles;
    private Set<RoleName> roleNames;

    @BeforeEach
    void setUp() {
        roles = new HashSet<>();
        Role userRole = new Role();
        userRole.setId(2L);
        userRole.setName(RoleName.USER);
        userRole.setDeleted(false);
        Role adminRole = new Role();
        adminRole.setId(1L);
        adminRole.setName(RoleName.ADMIN);
        adminRole.setDeleted(false);
        roles.add(userRole);
        roles.add(adminRole);

        roleNames = new HashSet<>();
        roleNames.add(userRole.getName());

        user = new User();
        user.setId(2L);
        user.setRoles(roles);
        user.setEmail("user@example.com");
        user.setFirstName("John");
        user.setLastName("Deer");
        user.setPassword("StrongPassword123");
        user.setDeleted(false);

        nonExistingUser = new User();
        nonExistingUser.setId(3L);
        nonExistingUser.setRoles(roles);
        nonExistingUser.setEmail("soap@example.com");
        nonExistingUser.setFirstName("John");
        nonExistingUser.setLastName("MacTavish");
        nonExistingUser.setPassword("StrongPassword123");
        nonExistingUser.setDeleted(false);

        userResponseDto = new UserResponseDto();
        userResponseDto.setId(2L);
        userResponseDto.setEmail("user@example.com");
        userResponseDto.setFirstName("John");
        userResponseDto.setLastName("Deer");
        userResponseDto.setRoles(roleNames);

        userUpdateRoleDto = new UserUpdateRoleDto();
        Role roleUser = new Role();
        roleUser.setId(2L);
        roleUser.setName(RoleName.USER);
        roleUser.setDeleted(false);
        Set<Role> role = new HashSet<>();
        role.add(roleUser);
        userUpdateRoleDto.setRoles(role);

        userUpdateProfileDto = new UserUpdateProfileDto();
        userUpdateProfileDto.setFirstName("New Name");
        userUpdateProfileDto.setLastName("New LastName");

        userUpdatedResponseDto = new UserResponseDto();
        userUpdatedResponseDto.setFirstName("New Name");
        userUpdatedResponseDto.setLastName("New LastName");
    }

    @Test
    void getUser_ExistingUser_Success() {
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(userMapper.toResponseDto(any(User.class))).thenReturn(userResponseDto);

        UserResponseDto response = userService.getUser("user@example.com");

        assertEquals("user@example.com", response.getEmail());
        verify(userRepository, times(1)).findByEmail("user@example.com");
        verify(userMapper, times(1)).toResponseDto(any(User.class));
    }

    @Test
    void getUser_NonExistingUser_NotFound() {
        when(userRepository.findByEmail("soap@example.com")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.getUser("soap@example.com"));
        verify(userRepository, times(1)).findByEmail("soap@example.com");
    }

    @Test
    void updateRoles_OnlyUserRole_Success() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));
        when(userMapper.toResponseDto(any(User.class))).thenReturn(userResponseDto);

        UserResponseDto response = userService.updateRoles(2L, userUpdateRoleDto);

        assertEquals(1, response.getRoles().size());
        verify(userRepository, times(1)).findById(2L);
        verify(userRepository, times(1)).save(any(User.class));
        verify(userMapper, times(1)).toResponseDto(any(User.class));
    }

    @Test
    void updateProfile_ChangeNameAndLastName_Success() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));
        when(userMapper.toResponseDto(any(User.class))).thenReturn(userUpdatedResponseDto);

        UserResponseDto response = userService.updateProfile(2L, userUpdateProfileDto);

        assertEquals("New Name", response.getFirstName());
        assertEquals("New LastName", response.getLastName());
        verify(userRepository, times(1)).findById(2L);
        verify(userRepository, times(1)).save(any(User.class));
        verify(userMapper, times(1)).toResponseDto(any(User.class));
    }
}
