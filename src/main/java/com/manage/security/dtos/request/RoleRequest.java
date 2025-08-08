package com.manage.security.dtos.request;

public record RoleRequest(
    Long id,
    String name,
    String new_name
) {

}

// Un DTO es cualquier objeto que transfiere datos entre capas:
// DTO de entrada: Datos del cliente → API.
// DTO de salida: API → Cliente.