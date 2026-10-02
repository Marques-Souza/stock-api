package com.marquesdev.stock.jwt;

import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;

import java.util.UUID;

public class JwtUserDetails extends User {

    private com.marquesdev.stock.entity.User user;

    public JwtUserDetails(com.marquesdev.stock.entity.User user) {
        super(user.getEmail(), user.getPassword(), AuthorityUtils.createAuthorityList(user.getProfile().name()));
        this.user = user;
    }

    public UUID getId(){
        return this.user.getId();
    }

    public String getProfile(){
        return this.user.getProfile().name();
    }
}
