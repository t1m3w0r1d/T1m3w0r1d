package com.webapp.backend.security;

import io.jsonwebtoken.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final PrivateKey privateKey;
    private final PublicKey publicKey;
    private final long ACCESS_TOKEN_EXPIRATION = 3600000L; // 1 heure
    private final long REFRESH_TOKEN_EXPIRATION = 2592000000L; // 30 jours

    public JwtTokenProvider(PrivateKey privateKey, PublicKey publicKey) {
        this.privateKey = privateKey;
        this.publicKey = publicKey;
    }

    // Générer un JWT (access_token)
    public String generateAccessToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION))
                .signWith(privateKey, SignatureAlgorithm.RS256)  // Utilisation de RS256 pour RSA
                .compact();
    }

    // Générer un refresh_token
    public String generateRefreshToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION))
                .signWith(privateKey, SignatureAlgorithm.RS256)  // Utilisation de RS256 pour RSA
                .compact();
    }

    // Extraire le nom d'utilisateur du JWT
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(publicKey)  // Vérification avec la clé publique
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    // Vérifier si le token est expiré
    public boolean isTokenExpired(String token) {
        Date expiration = Jwts.parserBuilder()
                .setSigningKey(publicKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
        return expiration.before(new Date());
    }

    // Valider le token
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(publicKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String getJwtFromRequest(HttpServletRequest request) {
        // Vérifier si le token JWT est dans l'en-tête Authorization
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // Retirer le préfixe "Bearer " et retourner le token
        }
        // Vérifier si le token JWT est dans les cookies
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("JWT".equals(cookie.getName())) {
                    return cookie.getValue(); // Retourner la valeur du cookie JWT
                }
            }
        }
        return null; // Retourner null si aucun token n'est trouvé
    }
}
