package com.example.userservice.services;

import com.example.userservice.exceptions.InvalidCredentialsException;
import com.example.userservice.exceptions.TokenNotFoundException;
import com.example.userservice.exceptions.UserNotFoundException;
import com.example.userservice.models.Token;
import com.example.userservice.models.User;
import com.example.userservice.repositories.TokenRepository;
import com.example.userservice.repositories.UserRepoistory;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

@Service
public class UserService {

    private UserRepoistory userRepository;
    private TokenRepository tokenRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Value("${jwt.secret}")
    private String jwtSecret;
    @Value("${jwt.expiration}")
    private long jwtExpiration;

    public UserService(UserRepoistory userRepository,
                          TokenRepository tokenRepository,
                        BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.tokenRepository = tokenRepository;
    }

    public Token login(String email, String password) throws InvalidCredentialsException{
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()){
            throw new InvalidCredentialsException("Invalid Credentials, Please try again!");
        }
        if (bCryptPasswordEncoder.matches(password, userOptional.get().getHashedPassword())) {
            Token jwtToken = generateToken(userOptional.get());
            return tokenRepository.save(jwtToken);
        }
        else {
            throw new InvalidCredentialsException("Invalid Credentials, Please try again!");
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
        try {
            String email = Jwts.parser()
                        .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
                        .build()
                        .parseClaimsJws(token)
                        .getBody()
                        .getSubject();
//            String email = Jwts.parser().verifyWith(Keys.hmacShaKeyFor(jwtSecret.getBytes())).build().parseClaimsJws(token).getBody().getSubject();
            Optional<Token> tokenOptional = tokenRepository.findByTokenAndDeletedAndExpiryGreaterThan(token,
                    false, new Date());

            if (tokenOptional.isEmpty()) {
                throw new TokenNotFoundException("Unable to login. Please try again");
            }
            return tokenOptional.get().getUser();

        } catch (ExpiredJwtException e) {
            // Handle expired JWT
            throw new TokenNotFoundException("JWT token has expired.");
        } catch (SignatureException e) {
            // Handle invalid JWT signature
            throw new TokenNotFoundException("Invalid JWT signature.");
        } catch (MalformedJwtException e) {
            // Handle malformed JWT
            throw new TokenNotFoundException("Malformed JWT.");
        } catch (UnsupportedJwtException e) {
            // Handle unsupported JWT
            throw new TokenNotFoundException("Unsupported JWT.");
        } catch (IllegalArgumentException e) {
            // Handle empty or null token
            throw new TokenNotFoundException("Invalid JWT token.");
        } catch (Exception e) {
            // Catch any other unexpected exceptions
            throw new TokenNotFoundException("Invalid token.");
        }
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
        token.setToken(generateJwtToken(user));
        token.setExpiry(new Date(System.currentTimeMillis() + jwtExpiration));

        return token;
    }
    private String generateJwtToken(User user) {
        Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        return Jwts.builder()
                .subject(user.getEmail())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(key)
                .compact();
    }
}
