package com.manage.security.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.manage.security.dtos.RoleDto;
import com.manage.security.helpers.GeneralHelper;
import com.manage.security.models.RoleModel;
import com.manage.security.repositories.RoleRepository;

@Service
@Transactional
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public ResponseEntity<?> create(Map<String, Object> body) {
        Object nameObj = body.get("name");
        
        // Verificar campo, paso que debería hacerse antes
        if(nameObj == null || !(nameObj instanceof String) ) {
            return GeneralHelper.badRequest("The data can not be used", null);
        }

        // Verificar si existe el rol en la base de datos
        String roleName = (String) nameObj;
        Optional<RoleModel> roleOptional = roleRepository.findByName(roleName);
        if(roleOptional.isPresent()) {
            return GeneralHelper.badRequest("The role is already created", null);
        }

        // Registramos el rol en la bd, porque no existe
        RoleModel roleDB = new RoleModel();
        roleDB.setName(roleName);
        roleDB = roleRepository.save(roleDB);

        // Construímos el objeto dto para entregar los datos
        RoleDto roleDto = new RoleDto(roleDB.getId(), roleDB.getName());
        return GeneralHelper.okRequest("The role has been created", roleDto);
    }

    @Override
    public ResponseEntity<?> findAll() {
        List<RoleModel> rolesDB = roleRepository.findAll();
        List<RoleDto> rolesDto = new ArrayList<>();

        rolesDB.forEach(roleDB -> {
            rolesDto.add(new RoleDto(roleDB.getId(), roleDB.getName()));
        });

        return GeneralHelper.okRequest("The roles could be found", rolesDto);
    }

    @Override
    public ResponseEntity<?> update(Map<String, Object> body) {
        Object idObj = body.get("id");
        Object newNameObj = body.get("new_name");
        
        // Verificar campo, paso que debería hacerse antes
        if(idObj == null || newNameObj == null || !(idObj instanceof Integer) || !(newNameObj instanceof String) ) {
            return GeneralHelper.badRequest("The data can not be used", null);
        }
        
        // Verificar que exista el role
        Long id = ((Integer) idObj).longValue();
        Optional<RoleModel> roleOptional1 = roleRepository.findById(id);
        if(roleOptional1.isEmpty()) {
            return GeneralHelper.badRequest("The role could not be found", null); 
        }

        // Se encontró el role, pero se verífica que el nuevo nombre no exista actualmente
        String newName = (String) newNameObj;
        Optional<RoleModel> roleOptional2 = roleRepository.findByName(newName);
        if(roleOptional2.isPresent()) {
            return GeneralHelper.badRequest("The role name is already stored", null); 
        }

        // Se puede actualizar el role
        RoleModel roleDB = roleOptional1.get();
        roleDB.setName(newName);
        roleDB = roleRepository.save(roleDB);

        RoleDto roleDto = new RoleDto(roleDB.getId(), roleDB.getName());
        return GeneralHelper.okRequest("The role has been updated", roleDto);
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
