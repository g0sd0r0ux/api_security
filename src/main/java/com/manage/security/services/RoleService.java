package com.manage.security.services;

import java.util.Map;

import org.springframework.http.ResponseEntity;

import com.manage.security.dtos.request.RoleRequest;

public interface RoleService {

    ResponseEntity<?> create(RoleRequest roleRequest);
    ResponseEntity<?> findAll();
    ResponseEntity<?> update(RoleRequest roleRequest);
    ResponseEntity<?> delete(Map<String, Object> body);

}
