package com.neoguara.rooms.auth;

import com.neoguara.rooms.auth.application.ports.UserAuthPort;
import com.neoguara.rooms.auth.infrastructure.security.AuthUserDetails;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthUserDetailsService implements UserDetailsService {

    private final UserAuthPort userAuthPort;

    public AuthUserDetailsService(UserAuthPort userAuthPort) {
        this.userAuthPort = userAuthPort;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userAuthPort.findByEmail(email)
                .map(AuthUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
    }
}
