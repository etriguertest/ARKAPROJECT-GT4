package com.grupo4.ms_authentication.service;

import com.grupo4.ms_authentication.entity.User;
import com.grupo4.ms_authentication.repository.UserRepository;
import com.grupo4.ms_authentication.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public String register(String username, String password) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("Usuario ya existe");
        }
        User user = User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .role("USER")
                .build();
        userRepository.save(user);
        return jwtService.generateToken(username);
    }

    public String login(String username, String password) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty() || !passwordEncoder.matches(password, user.get().getPassword())) {
            throw new RuntimeException("Credenciales inválidas");
        }
        return jwtService.generateToken(username);
    }
}
