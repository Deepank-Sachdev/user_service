package com.example.userservice.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Role extends BaseModel{
    @Column(columnDefinition = "varchar(255) default 'Customer'")
    private String name;
//    private String description;
}
