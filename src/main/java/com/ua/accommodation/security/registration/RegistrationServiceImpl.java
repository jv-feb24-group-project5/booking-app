package com.ua.accommodation.security.registration;

import com.ua.accommodation.dto.UserRegistrationRequestDto;
import com.ua.accommodation.dto.UserRegistrationResponseDto;
import com.ua.accommodation.exception.RegistrationException;
import com.ua.accommodation.mapper.UserMapper;
import com.ua.accommodation.model.Role.RoleName;
import com.ua.accommodation.model.User;
import com.ua.accommodation.repository.RoleRepository;
import com.ua.accommodation.repository.UserRepository;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegistrationServiceImpl implements RegistrationService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Override
    public UserRegistrationResponseDto register(UserRegistrationRequestDto requestDto) {
        if (userRepository.findByEmail(requestDto.getEmail()).isPresent()) {
            throw new RegistrationException("Can't register user with this email: "
                    + requestDto.getEmail() + " is already exists");

        }
        User user = userMapper.toModel(requestDto);
        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        user.setRoles(roleRepository.getAllByNameIn(Set.of(RoleName.USER)));
        return userMapper.toDto(userRepository.save(user));
    }
}
