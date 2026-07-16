package com.example.smart_ticket_router.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.smart_ticket_router.entity.Permission;

/**
 * Repository for Permission entity.
 */
public interface PermissionRepository
        extends JpaRepository<Permission, Long> {

    /**
     * Finds a permission by name.
     *
     * @param permissionName permission name
     * @return matching permission
     */
    Optional<Permission> findByPermissionName(String permissionName);

}