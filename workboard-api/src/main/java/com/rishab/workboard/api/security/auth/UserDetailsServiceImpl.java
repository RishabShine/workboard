package com.rishab.workboard.api.security.auth;

import com.rishab.workboard.api.domain.User;
import com.rishab.workboard.api.repository.UserRepository;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;

// Spring uses this when you call AuthenticationManager.authenticate(...) during login.
// It loads user + passwordHash so Spring can check the password.
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsernameOrEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // not using roles right now, so empty authorities
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(), // stored as hash
                List.of()
        );
    }
}
