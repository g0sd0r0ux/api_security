package com.manage.security.config;

import java.security.Key;
import java.security.SecureRandom;
import java.util.Collection;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.GrantedAuthority;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.LocatorAdapter;
import io.jsonwebtoken.ProtectedHeader;
import io.jsonwebtoken.security.Keys;

@Configuration
public class JwtConfig extends LocatorAdapter<Key> {

    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String PREFIX_AUTH_TOKEN = "Bearer ";
    
    // Para manejar llaves dinámicas
    @Override
    public Key locate(ProtectedHeader header)
    {    
        return null;
    }

    // Genera una clave HMAC-SHA-512 segura (64 bytes)
    public SecretKey createSecretKey()
    {
        byte[] keyBytes = new byte[64]; // 64 bytes para HS512
        new SecureRandom().nextBytes(keyBytes); // Llena el array con bytes aleatorios seguros
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String createJwt(Long id, String username, Collection<? extends GrantedAuthority> authorities,
        SecretKey secretKey) throws JsonProcessingException
    {
        String authoritiesStr = new ObjectMapper().writeValueAsString(authorities);
        return Jwts.builder()
            .header()
            .keyId(id.toString())
            .and()
            .subject(username)
            .claim("authorities", authoritiesStr)
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + (1000 * 60 * 5))) // Expira en 5 minutos
            .signWith(secretKey)
            .compact();
    }
    
    

}
