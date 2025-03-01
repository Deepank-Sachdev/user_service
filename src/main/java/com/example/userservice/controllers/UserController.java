package com.example.userservice.controllers;

import com.example.userservice.dtos.*;
import com.example.userservice.dtos.ResponseStatus;
import com.example.userservice.exceptions.TokenNotFoundException;
import com.example.userservice.exceptions.UserNotFoundException;
import com.example.userservice.models.Token;
import com.example.userservice.models.User;
import com.example.userservice.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")

public class UserController {

    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    public SignupUserResponseDTO signUp(@RequestBody SignUpUserRequestDto requestDto) {
        SignupUserResponseDTO responseDTO = new SignupUserResponseDTO();
        try {
            User user = userService.signUp(requestDto.getName(), requestDto.getEmail(), requestDto.getPassword());
            responseDTO.setName(user.getName());
            responseDTO.setEmail(user.getEmail());
            responseDTO.setRoles(user.getRoles());
            responseDTO.setResponseStatus(ResponseStatus.SUCCESS);
        } catch (UserNotFoundException e) {
            responseDTO.setResponseStatus(ResponseStatus.FAILURE);
        }
        return responseDTO;
    }

    @PostMapping("/login")
    public LoginResponseDto Login(@RequestBody LoginRequestDto requestDto) {
        LoginResponseDto responseDto = new LoginResponseDto();
        try {
            Token token = userService.login(requestDto.getEmail(), requestDto.getPassword());
                responseDto.setToken(token);
                responseDto.setLoggedIn(true);
                responseDto.setResponseStatus(ResponseStatus.SUCCESS);

        } catch (UserNotFoundException e) {
            responseDto.setResponseStatus(ResponseStatus.FAILURE);
        }
        return responseDto;
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody LogoutRequestDto requestDto) throws TokenNotFoundException {
         userService.logout(requestDto.getToken());
         return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/validate")
    public ResponseEntity<UserDto> validateToken(@RequestHeader ("Authorization") String token) throws TokenNotFoundException {
        try {
            User user = userService.validateToken(token);
            if (user == null) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>(UserDto.fromUser(user), HttpStatus.OK);
        }
        catch (TokenNotFoundException ex) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
