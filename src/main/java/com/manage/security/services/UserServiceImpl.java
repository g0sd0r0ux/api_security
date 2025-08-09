package com.manage.security.services;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.manage.security.config.JwtConfig;
import com.manage.security.dtos.request.UserRequest;
import com.manage.security.dtos.responses.RoleResponse;
import com.manage.security.dtos.responses.UserResponse;
import com.manage.security.helpers.GeneralHelper;
import com.manage.security.models.RoleModel;
import com.manage.security.models.UserModel;
import com.manage.security.repositories.RoleRepository;
import com.manage.security.repositories.UserRepository;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private JwtConfig jwtConfig;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public ResponseEntity<?> register(UserRequest userRequest)
    {
        String username = userRequest.username();
        String password = userRequest.password();
        String repeatPassword = userRequest.repeatPassword();
        Set<String> roles = userRequest.roles();

        if(GeneralHelper.isNullOrBlank(username) || GeneralHelper.isNullOrBlank(password) ||
            GeneralHelper.isNullOrBlank(repeatPassword) || !password.equals(repeatPassword) ||
            !this.verifyRoles(roles))
        {
            return GeneralHelper.badRequest("The data can not be used", null);
        }

        // Los campos están correctos, pero revisamos si el usuario no existe por el username, debido a que
        // es un campo único, además de traer los roles existentes y buscar que coincidan con alguno, para
        // asignarlo al usuario
        if(userRepository.existsByUsername(username)) {
            return GeneralHelper.badRequest("The username already exists", null);
        }

        // OPTIMIZADO - buscar roles
        List<RoleModel> rolesDB = roleRepository.findAll();
        Set<RoleModel> rolesForUser = roles.stream()
            .flatMap(roleName -> rolesDB.stream()
                .filter(roleDB -> roleName.equals(roleDB.getName()))
                .findFirst()
                .stream()
            ).collect(Collectors.toSet());
        if(rolesForUser.isEmpty()) {
            return GeneralHelper.badRequest("There weren't roles found to put it into the user", null);
        }

        // El username no existe y se encontraron roles para asignarle al usuario, por lo tanto, se crea
        UserModel userDB = new UserModel();
        userDB.setUsername(username);
        userDB.setPassword(passwordEncoder.encode(password));
        userDB.setRoles(rolesForUser);
        userDB = userRepository.save(userDB);

        try {
            // Como ya se creó el usuario, ahora posee id, y se le puede crear un token de seguridad
            Collection<GrantedAuthority> authorities = rolesForUser.stream()
                .map(roleForUser -> new SimpleGrantedAuthority(roleForUser.getName()))
                .collect(Collectors.toSet());
            SecretKey userSecretKey = jwtConfig.createSecretKey();
            String userJwtAuth = jwtConfig.createJwt(userDB.getId(), username, authorities, userSecretKey);
            // En caso de obtener el token sin mayor problema, se actualiza el usuario guardando su token y su key
            userDB.setJwtAuth(userJwtAuth);
            userDB.setSecretKeyBytes(userSecretKey.getEncoded());
            userDB = userRepository.save(userDB);
            Set<RoleResponse> rolesResponse = rolesForUser.stream()
                .map(roleForUser -> new RoleResponse(roleForUser.getId(), roleForUser.getName()))
                .collect(Collectors.toSet());
            UserResponse userResponse = new UserResponse(userDB.getId(), userDB.getUsername(), userDB.getJwtAuth(), rolesResponse);
            return GeneralHelper.okRequest("The uset has been created successfully", userResponse);
        } catch(JsonProcessingException e) {
            // No se pudo crear el token por la excepción de ingresar el claim de authorities, por lo tanto,
            // se elimina el usuario y se rechaza solicitud
            userRepository.delete(userDB);
            return GeneralHelper.badRequest("It's not possible register the user", null);
        }

    }

    private boolean verifyRoles(Set<String> roles) {
        if(roles.isEmpty()) {
            return false;
        }
        boolean adminRole=false, maintainerRole=false, customerRole=false;
        for(String role : roles) {
            if(role.equals("ROLE_ADMIN")) {
                adminRole=true;
            } else if(role.equals("ROLE_MAINTAINER")) {
                maintainerRole=true;
            } else if(role.equals("ROLE_CUSTOMER")) {
                customerRole=true;
            }
        }
        if((adminRole || maintainerRole) && !customerRole) {
            return true;
        } else if(customerRole && !adminRole && !maintainerRole) {
            return true;
        }
        return false;
    }

    // CONCLUSIONES
        // Consumer<T>: Función que procesa elementos sin retornar nada (ej.: forEach, ifPresent).
        // Stream: Transforma datos y puede retornar valores (ej.: filter, findFirst).
        // flatMap: Útil para "aplanar" múltiples streams en uno.
        // Manejo de vacíos/errores: No hay excepciones; los resultados son vacíos si no hay coincidencias. 

        // // Stream = flujo de datos
        // Set<RoleModel> rolesForUser = new HashSet<>();
        // List<RoleModel> rolesDB = roleRepository.findAll();
        // roles.forEach(role -> {
        //     rolesDB.stream()
        //         .filter(roleDB -> role.equals(roleDB.getName())) // Filtra por nombre
        //         .findFirst() // Encuentra al primero que coincida (similar a break)
        //         .ifPresent(rolesForUser::add);
        // });

        // OPTIMIZADO
        // Set<RoleModel> rolesForUser = userRequest.roles().stream()  // Convierte el Set<String> a Stream
        //     .flatMap(roleName -> roleRepository.findAll().stream()  // "Aplana" List<RoleModel> a Stream<RoleModel>
        //         .filter(roleDB -> roleName.equals(roleDB.getName()))    // Filtra por nombre
        //         .findFirst()                                           // Equivalente a break
        //         .stream())                                             // Convierte Optional<RoleModel> a Stream<RoleModel>
        //     .collect(Collectors.toSet());                          // Terminal: recolecta a Set<RoleModel>

        // Conversión Segura de List a Set
        // ✅ Métodos para convertir:
        // java
        // List<RoleModel> rolesDB = roleRepository.findAll();
        // // Opción 1: Constructor de HashSet
        // Set<RoleModel> rolesSet = new HashSet<>(rolesDB);

        // // Opción 2: Stream + collect
        // Set<RoleModel> rolesSet = rolesDB.stream().collect(Collectors.toSet());
        // ⚠️ ¿Es necesaria la conversión en tu caso?
        // No, porque solo estás iterando sobre la List (rolesDB). La conversión sería útil si necesitaras eliminar duplicados o realizar operaciones de conjunto (uniones, intersecciones).

        // // Consumer<T>:
        // // Es una interfaz funcional que acepta un argumento (T) y no devuelve nada (void). Se usa en operaciones que procesan elementos sin retornar resultados.
        // Consumer<String> imprimir = (texto) -> {
        //     System.out.println(texto);
        // };
        // imprimir.accept("Que tal");
}
