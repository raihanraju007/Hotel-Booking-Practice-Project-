package com.raju.hotelbooking.service;

import com.raju.hotelbooking.entity.AuthenticationResponse;
import com.raju.hotelbooking.entity.Token;
import com.raju.hotelbooking.entity.User;
import com.raju.hotelbooking.jwt.JwtService;
import com.raju.hotelbooking.repository.TokenRepository;
import com.raju.hotelbooking.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final TokenRepository tokenRepository;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService, TokenRepository tokenRepository, AuthenticationManager authenticationManager, EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.tokenRepository = tokenRepository;
        this.authenticationManager = authenticationManager;
        this.emailService = emailService;
    }

    private void saveUseToken(String jwt, User user){
        Token token = new Token();
        token.setToken(jwt);
        token.setLogout(false);
        token.setUser(user);


        tokenRepository.save(token);
    }

    private void removeAllTokenByUser(User user){
        List<Token> validTokens = tokenRepository.findAllTokenByUser(user.getId());
        if (validTokens.isEmpty()){
            return;
        }
        validTokens.forEach(t->{
            t.setLogout(true);
        });
        tokenRepository.saveAll(validTokens);
    }

    public AuthenticationResponse register(User user){
        return null;
    }
}
