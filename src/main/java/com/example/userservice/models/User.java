package com.example.userservice.models;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToMany;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.springframework.data.repository.cdi.Eager;

import java.util.List;

@Getter
@Setter
@Entity(name = "users")
public class User extends BaseModel {
    private String name;
    private String email;
    private String hashedPassword;
    private String mobile;
    @ManyToMany(fetch = FetchType.EAGER)
    private List<Role> roles;
}
