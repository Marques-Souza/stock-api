package com.marquesdev.stock.mapper;

import com.marquesdev.stock.ResourceUrlBuilder.ResourceUrlBuilder;
import com.marquesdev.stock.dto.UserRequestDto;
import com.marquesdev.stock.dto.UserResponseDto;
import com.marquesdev.stock.entity.User;
import com.marquesdev.stock.enums.Profile;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {


    private final ResourceUrlBuilder resourceUrlBuilder;

    @Value("${app.api.endpoints.users}")
    private String userEndpoint;

    public User toUser(UserRequestDto dto){
        User user = new User();
        user.setEmail(dto.email());
        user.setPassword(dto.password());
        user.setProfile(Profile.valueOf(dto.profile()));

        return user;

    }

    public UserResponseDto toDto(User user){
       return new UserResponseDto(
               user.getId(),
               user.getEmail(),
               user.getProfile().name(),
               resourceUrlBuilder.build(userEndpoint, user.getId())
       );
    }
}
