package com.manage.security.config;

import java.security.Key;
import java.security.SecureRandom;
import java.util.Date;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.springframework.context.annotation.Configuration;

// import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.manage.security.models.UserModel;

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
    public static SecretKey createSecretKey()
    {
        byte[] keyBytes = new byte[64]; // 64 bytes para HS512
        new SecureRandom().nextBytes(keyBytes); // Llena el array con bytes aleatorios seguros
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public static String createJwt(UserModel userDB) throws Exception
    {
        String authoritiesStr = new ObjectMapper().writeValueAsString(userDB.getRoles());
        String userJwtJti = userDB.getJwtJti();
        Date userExp = userDB.getJwtExp();
        if(userJwtJti == null || userExp == null) {
            throw new Exception("It's neccesary the jwi and exp to create the user token");
        }
        return Jwts.builder()
            .header()
            .keyId(userDB.getId().toString())
            .and()
            .id(userJwtJti)
            .subject(userDB.getUsername())
            .claim("authorities", authoritiesStr)
            .issuedAt(new Date())
            .expiration(userExp)
            .signWith(Keys.hmacShaKeyFor(userDB.getSecretKeyBytes()))
            .compact();
    }
    
    // Asignar jti y exp al usuario
    public static void updateJtiAndExp(UserModel userDB) {
        String userJwtJti = UUID.randomUUID().toString();
        Date userJwtExp = new Date(System.currentTimeMillis() + (1000*60*60*4)); // Expira en 4 horas
        userDB.setJwtJti(userJwtJti);
        userDB.setJwtExp(userJwtExp);
    }

}
