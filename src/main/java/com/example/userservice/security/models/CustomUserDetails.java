package com.example.userservice.security.models;

import com.example.userservice.models.Role;
import com.example.userservice.models.User;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@JsonDeserialize
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomUserDetails implements UserDetails {

    private String password;
    private String username;
    List<GrantedAuthority> authorities;

    public CustomUserDetails() {
    }

    public CustomUserDetails(User user) {
        this.password = user.getHashedPassword();
        this.username = user.getEmail();
        this.authorities = new ArrayList<>();
        for(Role role : user.getRoles()){
            authorities.add(new CustomGrantedAuthority(role));
        }
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }
}
