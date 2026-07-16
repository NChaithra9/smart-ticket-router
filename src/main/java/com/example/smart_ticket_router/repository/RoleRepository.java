package com.example.smart_ticket_router.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.smart_ticket_router.entity.Role;

/**
 * Repository for Role entity.
 */
public interface RoleRepository
        extends JpaRepository<Role, Long> {

    /**
     * Finds a role by its name.
     *
     * @param roleName role name
     * @return matching role
     */
    Optional<Role> findByRoleName(String roleName);

}