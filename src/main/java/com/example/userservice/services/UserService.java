package com.example.userservice.services;

import com.example.userservice.exceptions.TokenNotFoundException;
import com.example.userservice.exceptions.UserNotFoundException;
import com.example.userservice.models.Token;
import com.example.userservice.models.User;
import com.example.userservice.repositories.TokenRepository;
import com.example.userservice.repositories.UserRepoistory;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

@Service
public class UserService {

    private UserRepoistory userRepository;
    private TokenRepository tokenRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserService(UserRepoistory userRepository,
                          TokenRepository tokenRepository,
                        BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.tokenRepository = tokenRepository;
    }

    public Token login(String email, String password) throws UserNotFoundException{
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()){
            throw new UserNotFoundException("User doesn't exists. Please Signup");
        }
        if (bCryptPasswordEncoder.matches(password, userOptional.get().getHashedPassword())) {
            Token token = generateToken(userOptional.get());
            return tokenRepository.save(token);
        }
        else {
            throw new UserNotFoundException("Invalid password");
        }
    }

    public User signUp(String name, String email, String password) throws UserNotFoundException {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            throw new UserNotFoundException("User already exists");
        }

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setHashedPassword(bCryptPasswordEncoder.encode(password));

        return userRepository.save(user);
    }

    public User validateToken(String token) throws TokenNotFoundException {
        Optional<Token> tokenOptional = tokenRepository.findByTokenAndDeletedAndExpiryGreaterThan(token,
                                                false, new Date());

        if (tokenOptional.isEmpty()){
            throw  new TokenNotFoundException("Unable to login. Please try again");
        }

        return tokenOptional.get().getUser();
    }

    public void logout(String tokenValue) throws TokenNotFoundException {
        Optional<Token> tokenOptional = tokenRepository.findByTokenAndDeleted(tokenValue, false);
        if(tokenOptional.isPresent()){
            Token token = tokenOptional.get();
            token.softDelete();
            tokenRepository.save(token);
        }
        else {
            throw new TokenNotFoundException("Error logging out");
        }
    }

    private Token generateToken(User user) {
        Token token = new Token();
        token.setUser(user);
        token.setToken(RandomStringUtils.randomAlphanumeric(128));

            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, 30);
            Date date = calendar.getTime();

        token.setExpiry(date);

        return token;
    }
}
