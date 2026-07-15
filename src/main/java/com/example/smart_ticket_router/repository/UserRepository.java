package com.example.smart_ticket_router.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.smart_ticket_router.entity.User;

/**
 * Repository interface for performing CRUD operations
 * and custom queries on {@link User} entities.
 * <p>
 * Extends {@link JpaRepository} to provide standard
 * database operations and defines additional query
 * methods for retrieving users.
 * </p>
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Retrieves a user by their email address.
     *
     * @param email the email address of the user
     * @return an {@link Optional} containing the user if found,
     *         otherwise an empty {@link Optional}
     */
    Optional<User> findByEmail(String email);

}