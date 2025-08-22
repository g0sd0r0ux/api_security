package com.manage.security.config;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;
import java.util.logging.Logger;

import javax.crypto.SecretKey;

import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.manage.security.models.UserModel;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Configuration
public class JwtConfig {

    // No es necesario, por el contexto de la implementación, aquí solo manejamos la lógica para procesar el token
    // public class JwtConfig extends LocatorAdapter<Key> {
    // Para manejar llaves dinámicas, no es necesario, ya que la lógica de 
    // @Override
    // public Key locate(ProtectedHeader header)
    // {   
    //     // Obtenemos el id del kid 
    //     String kid = header.getKeyId();

    //     return getKeyFromKid();
    // }

    public static final Logger MESSAGE_LOGGER = Logger.getLogger(JwtConfig.class.getName());
    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String PREFIX_AUTH_TOKEN = "Bearer ";

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
        LocalDateTime userExp = userDB.getJwtExp();
        if(userJwtJti == null || userExp == null) {
            throw new Exception("It's neccesary the jwi and exp to create the user token");
        }
        Date dateExpiration = Date.from(userExp.atZone(ZoneId.systemDefault()).toInstant());
        return Jwts.builder()
            .header()
            .keyId(userDB.getId().toString())
            .and()
            .id(userJwtJti)
            .subject(userDB.getUsername())
            .claim("authorities", authoritiesStr)
            .issuedAt(new Date())
            .expiration(dateExpiration)
            .signWith(Keys.hmacShaKeyFor(userDB.getSecretKeyBytes()))
            .compact();
    }
    
    // Asignar jti y exp al usuario
    public static void updateJtiAndExp(UserModel userDB) {
        String userJwtJti = UUID.randomUUID().toString();
        LocalDateTime userJwtExp = LocalDateTime.now().plusHours(4); // Expira en 4 horas
        userDB.setJwtJti(userJwtJti);
        userDB.setJwtExp(userJwtExp);
    }

    // Obtener token sin la firma
    public static Claims getUnsignedToken(String jwtAuth) throws JwtException, IllegalArgumentException {
        return Jwts.parser().unsecured().build().parseUnsecuredClaims(jwtAuth).getPayload();
    }

    // // Si ya tienes un Date y necesitas convertirlo a LocalDateTime:
    // Date date = new Date();
    // LocalDateTime localDateTime = date.toInstant()
    // .atZone(ZoneId.systemDefault())
    // .toLocalDateTime();

}
