package com.manage.security.services;

import org.springframework.http.ResponseEntity;

import com.manage.security.dtos.request.UserRequest;

public interface UserService {

    ResponseEntity<?> register(UserRequest userRequest);
    ResponseEntity<?> login(UserRequest userRequest);

}
