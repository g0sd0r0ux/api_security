package com.manage.security.dtos.request;

import java.util.Set;

public record UserRequest(
    Long id,
    String username,
    String password,
    String repeatPassword,
    Set<String> roles
) {

}
