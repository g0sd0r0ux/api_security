package com.manage.security.services;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private PasswordEncoder passwordEncoder;

    @Override
    public ResponseEntity<?> register(UserRequest userRequest)
    {
        String username = userRequest.username();
        String password = userRequest.password();
        String repeatPassword = userRequest.repeatPassword();
        Set<String> roles = userRequest.roles();

        if(GeneralHelper.isNullOrBlank(username) || GeneralHelper.isNullOrBlank(password) ||
            GeneralHelper.isNullOrBlank(repeatPassword) || !password.equals(repeatPassword) || roles.isEmpty())
        {
            return GeneralHelper.badRequest("The data can not be used", null);
        }

        // Los campos están correctos, pero revisamos si el usuario no existe por el username, debido a que
        // es un campo único, además de traer los roles existentes y buscar que coincidan con alguno, para
        // asignarlo al usuario
        if(userRepository.existsByUsername(username)) {
            return GeneralHelper.badRequest("The username already exists", null);
        }

        // Consumer<T>:
        // Es una interfaz funcional que acepta un argumento (T) y no devuelve nada (void). Se usa en operaciones que procesan elementos sin retornar resultados.
        Consumer<String> imprimir = (texto) -> {
            System.out.println(texto);
        };
        imprimir.accept("Que tal");

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
        Set<RoleResponse> rolesResponse = rolesForUser.stream()
            .map(roleForUser -> new RoleResponse(roleForUser.getId(), roleForUser.getName()))
            .collect(Collectors.toSet());
        UserResponse userResponse = new UserResponse(userDB.getId(), userDB.getUsername(), rolesResponse);

        return GeneralHelper.okRequest("The uset has been created successfully", userResponse);
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

}
