package com.risosuit.DiegoGomezTagleGestionProductos.Services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    private final String SECRET_STRING
            = "una_clave_secreta_super_segura_y_larga_de_32_bytes!!";

    private SecretKey secretKey;

    @PostConstruct
    public void init() {

        secretKey = Keys.hmacShaKeyFor(
                SECRET_STRING.getBytes(StandardCharsets.UTF_8)
        );

    }

    /**
     * Genera el token JWT agregando: - username - idUsuario
     */
    public String generateToken(String username, Long idUsuario) {

        Date now = new Date();

        Date expiration = new Date(
                now.getTime() + 1000 * 60 * 120
        );

        return Jwts.builder()
                .subject(username)
                .claim("idUsuario", idUsuario)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(secretKey)
                .compact();

    }

   
    public String extractUsername(String token) {

        return getClaims(token)
                .getSubject();

    }

    /**
     * Obtiene el idUsuario del token
     */
    public Long extractIdUsuario(String token) {

        return getClaims(token)
                .get("idUsuario", Long.class);

    }

    /**
     * Valida que el token pertenezca al usuario y que no esté expirado
     */
    public boolean isTokenValid(String token, String username) {

        return extractUsername(token).equals(username)
                && !isTokenExpired(token);

    }

    /**
     * Valida si el token expiró
     */
    public boolean isTokenExpired(String token) {

        return getClaims(token)
                .getExpiration()
                .before(new Date());

    }

    private Claims getClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

        } catch (JwtException e) {

            throw new RuntimeException("Token inválido");

        }

    }

}
