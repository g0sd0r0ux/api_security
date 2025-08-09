package com.manage.security.dtos.responses;

import java.util.HashSet;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"id", "username"})
public class UserResponse {

    private Long id;
    private String username;
    private String jwtAuh;
    private Set<RoleResponse> roles = new HashSet<>();
    // private Set<ActionModel> actions; // Debería crear un action response

}
