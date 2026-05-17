package com.neoguara.rooms.auth;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class AuthUserDetails implements UserDetails {

    private final AuthUserData data;

    public AuthUserDetails(AuthUserData data) {
        this.data = data;
    }

    public AuthUserData getData() {
        return data;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + data.role()));
    }

    @Override
    public String getPassword() {
        return data.encodedPassword();
    }

    @Override
    public String getUsername() {
        return data.email();
    }

    @Override
    public boolean isEnabled() {
        return data.isActive() != null && data.isActive();
    }
}
