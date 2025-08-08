package com.manage.security.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.manage.security.dtos.request.RoleRequest;
import com.manage.security.helpers.GeneralHelper;
import com.manage.security.services.RoleService;

@RestController
@RequestMapping(value = "/v1/roles")
@PreAuthorize(value = "denyAll()")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @PostMapping(value = "/create")
    @PreAuthorize(value = "permitAll()")
    public ResponseEntity<?> create(@RequestBody RoleRequest roleRequest, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            return GeneralHelper.badRequest("There are errors in the fields", null);
        }
        return roleService.create(roleRequest);
    }

    @GetMapping(value = "/get/all")
    @PreAuthorize("permitAll()")
    public ResponseEntity<?> findAll() {
        return roleService.findAll();
    }

    @PutMapping(value = "/update")
    @PreAuthorize("permitAll()")
    public ResponseEntity<?> update(@RequestBody RoleRequest roleRequest, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            return GeneralHelper.badRequest("There are errors in the fields", null);
        }
        return roleService.update(roleRequest);
    }

    // Recibir los datos con Map<>, puede servir para realizar flujos, donde se deban consumir servicios que son
    // dinámicos y cambie la estructura de los datos
    @DeleteMapping(value = "/delete")
    @PreAuthorize("permitAll()")
    public ResponseEntity<?> delete(@RequestBody Map<String, Object> body, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            return GeneralHelper.badRequest("There are errors in the fields", null);
        }
        return roleService.delete(body);
    }
}
