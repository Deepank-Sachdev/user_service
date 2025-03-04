package com.example.userservice.security.services;

import com.example.userservice.models.User;
import com.example.userservice.repositories.UserRepoistory;
import com.example.userservice.security.models.CustomUserDetails;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private UserRepoistory userRepoistory;

    public CustomUserDetailsService(UserRepoistory userRepoistory) {
        this.userRepoistory = userRepoistory;
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOptional = userRepoistory.findByEmail(username);
        if (userOptional.isEmpty()){
            throw new UsernameNotFoundException("User doesn't exist");
        }
        return new CustomUserDetails(userOptional.get());
    }
}
