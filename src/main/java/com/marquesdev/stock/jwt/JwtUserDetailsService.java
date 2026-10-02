package com.marquesdev.stock.jwt;

import com.marquesdev.stock.entity.User;
import com.marquesdev.stock.enums.Profile;
import com.marquesdev.stock.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtUserDetailsService implements UserDetailsService {


    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userService.findByEmail(email);
        return new JwtUserDetails(user);
    }

    public JwtToken getTokenAuthenticated(String email){
        Profile profile = userService.findProfileByEmail(email);
        return JwtUtils.createToken(email, profile.name());
    }
}
