package com.manage.security.models;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name="users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = {"id", "username"})
@JsonPropertyOrder(value = {"id", "username", "username"})
public class UserModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    @JsonProperty(access = Access.WRITE_ONLY)
    private String password;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "users_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id"),
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "role_id"})
    )
    @JsonIgnoreProperties(value = {"users"})
    private Set<RoleModel> roles = new HashSet<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = {"user"})
    private Set<ActionModel> actions = new HashSet<>();

}
