package com.manage.security.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"id"}) // Espeficicamos con que campos se crean el equals y hashCode
// @RequiredArgsConstructor // Constructor, que incluye los campos final y @NonNull de lombok solamente
public class RoleResponse {

    private Long id;
    private String name;
    
}
