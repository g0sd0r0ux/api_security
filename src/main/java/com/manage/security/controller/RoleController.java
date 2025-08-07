package com.manage.security.controller;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.manage.security.helpers.GeneralHelper;
import com.manage.security.services.RoleService;

@RestController
@RequestMapping(value = "/v1/roles/")
@PreAuthorize(value = "denyAll()")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @PostMapping(value = "/create")
    @PreAuthorize("permitAll()")
    public ResponseEntity<?> create(@RequestBody HashMap<String, Object> json, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            return GeneralHelper.badRequest("There are errors in the fields", null);
        }
        return roleService.create(json);
    }

}
