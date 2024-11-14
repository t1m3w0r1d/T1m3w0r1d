package com.webapp.backend.controller;

import com.webapp.backend.model.User;
import com.webapp.backend.repository.UserRepository;
import com.webapp.backend.security.JwtTokenProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthController(UserRepository userRepository, JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    // Inscription de l'utilisateur
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        // Vérifier si le nom d'utilisateur existe déjà
        if (userRepository.existsByUsername(user.getUsername())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username is already taken");
        }

        // Sauvegarder l'utilisateur dans la base de données
        userRepository.save(user);

        // Retourner une réponse de succès
        return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
    }

    // Connexion de l'utilisateur
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        // Vous pouvez ajouter ici la logique pour vérifier les informations d'identification (login)
        // Par exemple, en vérifiant les mots de passe et en générant les tokens JWT
        // Exemple simple, à compléter selon votre logique d'authentification

        if (!userRepository.existsByUsername(user.getUsername())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username not found");
        }

        // À ce point, vous pouvez ajouter une logique pour vérifier le mot de passe
        // et générer un access token + refresh token
        String accessToken = jwtTokenProvider.generateAccessToken(user.getUsername());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getUsername());

        // Retourner les tokens dans la réponse
        return ResponseEntity.ok(new JwtResponse(accessToken, refreshToken));
    }

    // Classe pour encapsuler la réponse contenant les tokens
    public static class JwtResponse {
        private String accessToken;
        private String refreshToken;

        public JwtResponse(String accessToken, String refreshToken) {
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
        }

        public String getAccessToken() {
            return accessToken;
        }

        public void setAccessToken(String accessToken) {
            this.accessToken = accessToken;
        }

        public String getRefreshToken() {
            return refreshToken;
        }

        public void setRefreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
        }
    }
}
