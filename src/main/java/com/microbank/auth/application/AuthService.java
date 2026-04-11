package com.microbank.auth.application;

import com.microbank.auth.domain.Role;
import com.microbank.auth.domain.User;
import com.microbank.auth.domain.UserRepository;
import com.microbank.auth.presentation.AuthResponse;
import com.microbank.auth.presentation.LoginRequest;
import com.microbank.auth.presentation.RegisterRequest;
import com.microbank.shared.exceptions.InvalidAccountException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new InvalidAccountException("El usuario ya existe: " + request.getUsername());
        }

        User user = new User(
                request.getUsername(),
                passwordEncoder.encode(request.getPassword()),
                Set.of(Role.ROLE_USER)
        );

        User saved = userRepository.save(user);
        String token = jwtUtil.generateToken(saved.getUsername(), saved.getId());

        return new AuthResponse(token, saved.getUsername(), saved.getId());
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new InvalidAccountException("Usuario no encontrado"));

        String token = jwtUtil.generateToken(user.getUsername(), user.getId());
        return new AuthResponse(token, user.getUsername(), user.getId());
    }
}
