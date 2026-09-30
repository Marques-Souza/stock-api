package com.marquesdev.stock.service;

import com.marquesdev.stock.dto.PasswordRequestDto;
import com.marquesdev.stock.dto.UserRequestDto;
import com.marquesdev.stock.dto.UserResponseDto;
import com.marquesdev.stock.entity.User;
import com.marquesdev.stock.exception.EntityNotFoundException;
import com.marquesdev.stock.exception.PasswordInvalidException;
import com.marquesdev.stock.exception.UserEmailUniqueException;
import com.marquesdev.stock.mapper.UserMapper;
import com.marquesdev.stock.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional
    public UserResponseDto saveUser(UserRequestDto userRequestDto) {
        User user = userMapper.toUser(userRequestDto);

        try {
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
                .orElseThrow(() -> new RuntimeException("User not found with id:" + id));

        if (!passwordRequestDto.newPassword().equals(passwordRequestDto.confirmPassword())){
            throw new PasswordInvalidException("New password and confirmation do not match.");
        }

        if (!existingUser.getPassword().equals(passwordRequestDto.currentPassword())){
            throw new PasswordInvalidException("Current password is incorrect.");
        }
        existingUser.setPassword(passwordRequestDto.newPassword());
        return userMapper.toDto(existingUser);
    }
}
