package com.teamzeu.velo.services.service_impl;

import com.teamzeu.velo.entities.User;
import com.teamzeu.velo.repositories.UserRepository;
import com.teamzeu.velo.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username)
                .orElseThrow(()->new UsernameNotFoundException("user with the email: " + username + "not found"));
        return new CustomUserDetails(user);
    }
}
