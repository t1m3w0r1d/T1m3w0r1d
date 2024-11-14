package com.webapp.backend.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import jakarta.servlet.http.HttpServletResponse;

@Configuration
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;  // Injection de JwtTokenProvider

    public SecurityConfig(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    // Liste des chemins d'URL qui sont autorisés sans authentification
    private static final String[] AUTH_WHITELIST = {
        "/api/auth/register",
        "/api/auth/login"
    };

    // Définition du SecurityFilterChain avec une configuration moderne
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // Instancier le filtre avec jwtTokenProvider
        JwtAuthenticationFilter authenticationFilter = new JwtAuthenticationFilter(jwtTokenProvider);

        http
                // Désactivation de CSRF pour les applications sans état (stateless)
                .csrf(csrf -> csrf.disable())

                // Configuration des règles d'autorisation des requêtes
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers(AUTH_WHITELIST).permitAll()  // Permet l'accès aux URL listées
                                .anyRequest().authenticated()  // Toutes les autres demandes doivent être authentifiées
                )

                // Gestion des exceptions pour les requêtes non autorisées
                .exceptionHandling(exceptionHandling ->
                        exceptionHandling.authenticationEntryPoint(unauthorizedEntryPoint())  // Point d'entrée pour les erreurs d'authentification
                )

                // Ajout du filtre JWT avant le filtre d'authentification de base
                .addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // Méthode pour définir le point d'entrée en cas de refus d'accès (ex. retour HTTP 401)
    private AuthenticationEntryPoint unauthorizedEntryPoint() {
        return (request, response, authException) -> {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
        };
    }
}
