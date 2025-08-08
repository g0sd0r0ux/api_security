package com.manage.security.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.manage.security.models.UserModel;

public interface UserRepository extends JpaRepository<UserModel, Long> {

    Optional<UserModel> findByUsername(String username);
    boolean existsByUsername(String username);

}
