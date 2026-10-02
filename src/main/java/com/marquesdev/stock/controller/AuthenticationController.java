package com.marquesdev.stock.controller;

import com.marquesdev.stock.dto.UserLoginDto;
import com.marquesdev.stock.exception.ErrorMessage;
import com.marquesdev.stock.jwt.JwtToken;
import com.marquesdev.stock.jwt.JwtUserDetailsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AuthenticationController {

    private final JwtUserDetailsService detailsService;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/auth")
    public ResponseEntity<?> authenticate(@RequestBody @Valid UserLoginDto userDto, HttpServletRequest request){
        log.info("authentication process by the user {}", userDto.email());

        try {
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(userDto.email(), userDto.password());
            authenticationManager.authenticate(authenticationToken);
            JwtToken token = detailsService.getTokenAuthenticated(userDto.email());
            return ResponseEntity.ok(token);
        }catch (AuthenticationException ex){
            log.warn("Authentication falid for user {}: {}", userDto.email(), ex.getMessage());
        }
        return ResponseEntity.badRequest().body(new ErrorMessage(request, HttpStatus.BAD_REQUEST, "Invalid credentials"));
    }
}
