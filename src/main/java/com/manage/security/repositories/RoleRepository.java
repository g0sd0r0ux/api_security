package com.manage.security.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.manage.security.models.RoleModel;

@Repository
public interface RoleRepository extends JpaRepository<RoleModel, Long> {

    // Se pueden crear clases personalizadas
    Optional<RoleModel> findByName(String name);

}
