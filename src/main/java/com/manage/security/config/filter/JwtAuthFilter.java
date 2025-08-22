package com.manage.security.config.filter;

import java.io.IOException;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.filter.OncePerRequestFilter;

import com.manage.security.config.JwtConfig;
import com.manage.security.helpers.GeneralHelper;
import com.manage.security.models.UserModel;
import com.manage.security.repositories.UserRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
@Transactional
public class JwtAuthFilter extends OncePerRequestFilter {

    // No es necesario, porque aquí manejaremos la instancia del repositorio del usuario y la relación del kid 
    // (parámetro del header), para relacionarlo a un usuario (en este caso el id), y obtener su secret key
    // private Locator<Key> keyLocator;

    private UserRepository userRepository;

    public JwtAuthFilter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
        @NonNull FilterChain filterChain) throws ServletException, IOException
    {
        String jwtAuth = request.getHeader(JwtConfig.HEADER_AUTHORIZATION);
        
        try {
            // Si es nulo o vacío hacemos el filtro, ya que, puede que la solicitud sea hacía un endpoint público
            if(GeneralHelper.isNullOrBlank(jwtAuth)) {
                filterChain.doFilter(request, response);
                return;
            }

            // Hay valor en la cabecera, comprobamos que este el prefijo
            if(!jwtAuth.contains(JwtConfig.PREFIX_AUTH_TOKEN)) {
                throw new Exception("Unauthorized prefix request");
            }

            // Esta el prefijo, lo sacamos y procemos el token
            jwtAuth = jwtAuth.replace(JwtConfig.PREFIX_AUTH_TOKEN, "");
            Claims claims = JwtConfig.getUnsignedToken(jwtAuth);
            String kid = claims.get("kid", String.class);
            String jti = claims.getId();

            // Verificamos primero que exista el usuario
            Optional<UserModel> userOptional = userRepository.findById(Long.parseLong(kid));
            if(userOptional.isEmpty()) {
                throw new Exception("Unauthorized user request");
            }

            // Verificamos que el jti sea correcto
            UserModel userDB = userOptional.get();
            if(!userDB.getJwtJti().equals(jti)) {
                throw new Exception("Unauthorized jti request");
            }

            // Verificamos la firma del token
            Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(userDB.getSecretKeyBytes()))
                .build()
                .parseSignedClaims(jwtAuth);
            
            // Si no hubo excepción esta todo correcto por lo que generamos el objeto de autenticación, asignandole
            // las autoridades al usuario, mediante sus roles
            Collection<? extends GrantedAuthority> userAuthorities = userDB.getRoles().stream()
                .map(userRole -> new SimpleGrantedAuthority(userRole.getName()))
                .collect(Collectors.toSet());
            Authentication auth = new UsernamePasswordAuthenticationToken(userDB.getUsername(), null, userAuthorities);
            SecurityContextHolder.getContext().setAuthentication(auth);

        } catch(ExpiredJwtException e) {
            // ExpiredJwtException solo se lanza cuando: El token tiene una firma válida (se verificó con la
            // SecretKey correcta). Pero su fecha de expiración (exp) ya pasó.
            JwtConfig.MESSAGE_LOGGER.info("Updating jwt expired");
            if(!updateExpiredJwt(response, jwtAuth)) {
                GeneralHelper.expectationFailed(response, "Expectation failed process occurs while updating jwt: " + e.getMessage());
                return;
            }
        } catch(JwtException | IllegalArgumentException e) {
            // Excepción IllegalArgumentException: Ocurre cuando el token no tiene el formato correcto
            // (ej.: no tiene 3 partes separadas por puntos).
            JwtConfig.MESSAGE_LOGGER.info("Expectation failed process occurs");
            GeneralHelper.expectationFailed(response, e.getMessage());
            return;
        } catch(Exception e) {
            JwtConfig.MESSAGE_LOGGER.info("Unauthorized process occurs");
            GeneralHelper.unauthorized(response, e.getMessage());
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean updateExpiredJwt(HttpServletResponse response, String jwtAuthHeader) {
        try {
            // Buscamos el kid para obtener el usuario y crear un nuevo token, en base a su secret key
            String jwtAuth = jwtAuthHeader.replace(JwtConfig.PREFIX_AUTH_TOKEN, "");
            String kid = JwtConfig.getUnsignedToken(jwtAuth).get("kid", String.class);
            UserModel userDB = userRepository.findById(Long.parseLong(kid)).orElseThrow();
            JwtConfig.updateJtiAndExp(userDB);
            String newJwtAuth = JwtConfig.createJwt(userDB);
            // Incluímos el nuevo token en la cabecera '', y actualizamos el jti y exp del usuario en la bd
            userDB = userRepository.save(userDB);
            response.addHeader("New-Access-Token", newJwtAuth);
            // Por último creamos el objeto de autenticación, ya que, si se le pudo actualizar el token del usuario
            Collection<? extends GrantedAuthority> userAuthorities = userDB.getRoles().stream()
                .map(userRoleDB -> new SimpleGrantedAuthority(userRoleDB.getName()))
                .collect(Collectors.toSet());
            Authentication auth = new UsernamePasswordAuthenticationToken(userDB.getUsername(), null, userAuthorities);
            SecurityContextHolder.getContext().setAuthentication(auth);
        } catch(Exception e) {
            return false;
        }
        return true;
    }

}
