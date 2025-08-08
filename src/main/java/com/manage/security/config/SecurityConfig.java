package com.manage.security.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    // Implementación de objeto para encriptación de datos sencibles que no necesiten invertir proceso
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Implementación de la cadena de filtros para mayor seguridad al procesar una la solicitud
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                    .cors(cors -> cors.configurationSource(this.corsConfiguration()))
                    .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                    .csrf(csrf -> csrf.disable())
                    .build();
    }

    // Implementamos el objeto para la configuración de las características de nuestro CORS
    @Bean
    public CorsConfigurationSource corsConfiguration() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(Arrays.asList("*"));
        config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "User-Agent"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource corsConfiguration = new UrlBasedCorsConfigurationSource();
        corsConfiguration.registerCorsConfiguration("/**", config);

        return corsConfiguration;
    }

    // .addFilterBefore(new JwtAuthFilter(), UsernamePasswordAuthenticationFilter.class) En caso de agregar filtro de autenticación de usuario predeterminado
    // UsernamePasswordAuthenticationFilter:
    // Solo actúa en /login (POST) si usas formLogin(). En tu API JWT, no lo necesitas.
    // http.formLogin(form -> form
    //     .loginProcessingUrl("/login") // Ruta POST para enviar usuario/contraseña
    //     .usernameParameter("email")   // Campo del request (default: "username")
    //     .passwordParameter("clave")   // Campo del request (default: "password")
    // );

    // Roles vs Authorities:
    // Roles: Prefijo ROLE_ (implícito en hasRole()).
    // Authorities: Nombres directos (ej.: READ).

    // Al parecer solo es necesario, para crear un objeto de autenticación y necesite ser gestionado por el Authenticator Manager y la implementación del UserDetails con UsernamePasswordAuthenticationFilter
    // // Objeto de spring que nos ayuda a gestionar la autenticación del sistema
    // @Autowired
    // private AuthenticationConfiguration authenticationConfiguration;
    // // Obtener la implementación del objeto que gestiona la autenticación
    // @Bean
    // public AuthenticationManager authenticationManager() throws Exception {
    //     return authenticationConfiguration.getAuthenticationManager();
    // }

    // // Implementamos el objeto relacionado con el filtro del CORS, es solamente necesario para cuando se necesita ajustar el orden de presedencia
    // @Bean
    // public FilterRegistrationBean<CorsFilter> corsFilter() {
    //     FilterRegistrationBean<CorsFilter> corsFilter = new FilterRegistrationBean<> (new CorsFilter(this.corsConfiguration()));
    //     corsFilter.setOrder(Ordered.HIGHEST_PRECEDENCE);
    //     return corsFilter;
    // }

    // // Es posible agregar un filtro a ciertas rutas, que simularía el comportamiendo de middlewares
    // @Bean  
    // public FilterRegistrationBean<MiFiltro> filtro() {  
    //     FilterRegistrationBean<MiFiltro> bean = new FilterRegistrationBean<>();  
    //     bean.setFilter(new MiFiltro());  
    //     bean.addUrlPatterns("/api/*"); // Solo aplica a rutas /api/...  
    //     return bean;  
    // }  

    // UN ORDEN QUE ES RECOMENDADO
    // @Bean
    // public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    //     return http
    //         .cors(withDefaults()) // 1. CORS (primero, para preflight requests)
    //         .csrf(csrf -> csrf.disable()) // 2. CSRF (deshabilitado para APIs)
    //         .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 3. Sin sesión
    //         .addFilterBefore(new JwtAuthFilter(), UsernamePasswordAuthenticationFilter.class) // 4. Filtro JWT
    //         .authorizeHttpRequests(auth -> auth // 5. Autorizaciones
    //             .requestMatchers("/auth/**").permitAll()
    //             .anyRequest().authenticated() // Hace referencia al autenticador del contexto de Spring, es independiente de los roles que tenga el usuario
    //         )
    //         .build();
    // }

    // IMPORTANTE
    //     ¿Por qué usar addFilterBefore() incluso si no usas UsernamePasswordAuthenticationFilter?
    // ✅ Razón clave:
    // Spring Security siempre incluye filtros internos (como AnonymousAuthenticationFilter o FilterSecurityInterceptor). Si añades tu JwtAuthFilter con addFilter(), se colocará al final de la cadena, lo que podría causar problemas si otros filtros esperan una autenticación previa.
    // Filtros: Usa addFilterBefore() y recuerda que el último registrado se ejecuta primero.
    // AOP:
    // Ventaja: Evita código repetitivo (ej.: logs/seguridad en cada método).
    // Spring lo usa internamente para @Transactional, @Cacheable, etc.
    
}
