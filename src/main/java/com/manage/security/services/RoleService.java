package com.manage.security.services;

import java.util.Map;

import org.springframework.http.ResponseEntity;

public interface RoleService {

    ResponseEntity<?> create(Map<String, Object> body);
    ResponseEntity<?> findAll();
    ResponseEntity<?> update(Map<String, Object> body);
    ResponseEntity<?> delete(Map<String, Object> body);

}
