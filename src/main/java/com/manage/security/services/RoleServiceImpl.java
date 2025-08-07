package com.manage.security.services;

import java.util.Map;

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
        
        // Registramos el rol en la bd
        String roleName = (String) nameObj;
        RoleModel roleDB = new RoleModel();
        roleDB.setName(roleName);
        roleDB = roleRepository.save(roleDB);

        // Construímos el objeto dto para entregar los datos
        RoleDto roleDto = new RoleDto(roleDB.getId(), roleDB.getName());
        return GeneralHelper.okRequest("The role has been created", roleDto);
    }

    

}
