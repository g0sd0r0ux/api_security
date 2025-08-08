package com.manage.security.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.manage.security.dtos.request.RoleRequest;
import com.manage.security.dtos.responses.RoleResponse;
import com.manage.security.helpers.GeneralHelper;
import com.manage.security.models.RoleModel;
import com.manage.security.repositories.RoleRepository;

@Service
@Transactional
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public ResponseEntity<?> create(RoleRequest roleRequest) {
        String name = roleRequest.name();

        // Luego optimizamos validación
        if(GeneralHelper.isNullOrBlank(name)) {
            return GeneralHelper.badRequest("The data can not be used", null);
        }

        Optional<RoleModel> roleOptional = roleRepository.findByName(name);
        if(roleOptional.isPresent()) {
            return GeneralHelper.badRequest("The role is already created", null);
        }

        // Registramos el rol en la bd, porque no existe
        RoleModel roleDB = new RoleModel();
        roleDB.setName(name);
        roleDB = roleRepository.save(roleDB);

        // Construímos el objeto dto para entregar los datos
        RoleResponse roleResponse = new RoleResponse(roleDB.getId(), roleDB.getName());
        return GeneralHelper.okRequest("The role has been created", roleResponse);
    }

    @Override
    public ResponseEntity<?> findAll() {
        List<RoleModel> rolesDB = roleRepository.findAll();
        List<RoleResponse> rolesDto = new ArrayList<>();

        rolesDB.forEach(roleDB -> {
            rolesDto.add(new RoleResponse(roleDB.getId(), roleDB.getName()));
        });

        return GeneralHelper.okRequest("The roles could be found", rolesDto);
    }

    @Override
    public ResponseEntity<?> update(RoleRequest roleRequest) {
        Long id = roleRequest.id();
        String newName = roleRequest.new_name();
        
        // Luego optimizamos validación
        if(id == null || GeneralHelper.isNullOrBlank(newName)) {
            return GeneralHelper.badRequest("The data can not be used", null);
        }
        
        // Verificar que exista el role
        Optional<RoleModel> roleOptional1 = roleRepository.findById(id);
        if(roleOptional1.isEmpty()) {
            return GeneralHelper.badRequest("The role could not be found", null); 
        }

        // Se encontró el role, pero se verífica que el nuevo nombre no exista actualmente
        Optional<RoleModel> roleOptional2 = roleRepository.findByName(newName);
        if(roleOptional2.isPresent()) {
            return GeneralHelper.badRequest("The role name is already stored", null); 
        }

        // Se puede actualizar el role
        RoleModel roleDB = roleOptional1.get();
        roleDB.setName(newName);
        roleDB = roleRepository.save(roleDB);

        RoleResponse roleResponse = new RoleResponse(roleDB.getId(), roleDB.getName());
        return GeneralHelper.okRequest("The role has been updated", roleResponse);
    }

    @Override
    public ResponseEntity<?> delete(Map<String, Object> body) {
        Object idObj = body.get("id");

        // Verificar campo, paso que debería hacerse antes
        if(idObj == null || !(idObj instanceof Integer)) {
            return GeneralHelper.badRequest("The data can not be used", null);
        }

        // Se elimina el role
        roleRepository.deleteById( ((Integer) idObj).longValue() );
        return GeneralHelper.okRequest("The role has been eliminated", null);
    }

}
