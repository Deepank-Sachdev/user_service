package com.example.userservice.dtos;

import com.example.userservice.models.Token;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class LoginResponseDto {
    private boolean loggedIn;
    private Token token;
    private ResponseStatus responseStatus;
}