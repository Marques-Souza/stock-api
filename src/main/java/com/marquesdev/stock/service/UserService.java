package com.marquesdev.stock.service;

import com.marquesdev.stock.dto.PasswordRequestDto;
import com.marquesdev.stock.dto.UserRequestDto;
import com.marquesdev.stock.dto.UserResponseDto;
import com.marquesdev.stock.entity.User;
import com.marquesdev.stock.enums.Profile;
import com.marquesdev.stock.exception.EntityNotFoundException;
import com.marquesdev.stock.exception.PasswordInvalidException;
import com.marquesdev.stock.exception.UserEmailUniqueException;
import com.marquesdev.stock.jwt.JwtToken;
import com.marquesdev.stock.mapper.UserMapper;
import com.marquesdev.stock.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponseDto saveUser(UserRequestDto userRequestDto) {
        User user = userMapper.toUser(userRequestDto);

        try {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            User savedUser = userRepository.saveAndFlush(user);
            return userMapper.toDto(savedUser);
        }catch (org.springframework.dao.DataIntegrityViolationException ex){
            throw new UserEmailUniqueException("Email Already exists.");
        }
    }

    @Transactional(readOnly = true)
    public UserResponseDto getUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
        return userMapper.toDto(user);
    }

    @Transactional(readOnly = true)
    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toDto)
                .toList();
    }

    @Transactional
    public UserResponseDto updatePassword(UUID id, PasswordRequestDto passwordRequestDto) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id:" + id));

        if (!passwordRequestDto.newPassword().equals(passwordRequestDto.confirmPassword())){
            throw new PasswordInvalidException("New password and confirmation do not match.");
        }

        if (!passwordEncoder.matches(passwordRequestDto.currentPassword(), existingUser.getPassword())){
            throw new PasswordInvalidException("Current password is incorrect.");
        }
        existingUser.setPassword(passwordEncoder.encode(passwordRequestDto.newPassword()));
        return userMapper.toDto(existingUser);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email));
    }

    public Profile findProfileByEmail(String email) {
        return userRepository.findProfileByEmail(email);
    }
}
