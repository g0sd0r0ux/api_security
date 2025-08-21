package com.manage.security.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.manage.security.dtos.request.UserRequest;
import com.manage.security.helpers.GeneralHelper;
import com.manage.security.services.UserService;

@RestController
@RequestMapping(value = "/v1/users")
@PreAuthorize(value = "denyAll()")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping(value = "/register")
    @PreAuthorize(value = "permitAll()")
    public ResponseEntity<?> register(@RequestBody UserRequest userRequest, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            return GeneralHelper.badRequest("There are errors in the fields", GeneralHelper.buildErrors(bindingResult));
        }
        return userService.register(userRequest);
    }

    @PostMapping(value = "/login")
    @PreAuthorize(value = "permitAll()")
    public ResponseEntity<?> login(@RequestBody UserRequest userRequest) {
        return userService.login(userRequest);
    }

}
