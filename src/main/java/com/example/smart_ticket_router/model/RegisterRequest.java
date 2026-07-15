package com.example.smart_ticket_router.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents the request payload used for registering a new user.
 * <p>
 * This class contains the user's name, email, and password
 * required during the registration process.
 * </p>
 */
public class RegisterRequest {

    /**
     * Logger for RegisterRequest.
     */
    private static final Logger logger = LoggerFactory.getLogger(RegisterRequest.class);

    /**
     * Name of the user.
     */
    private String name;

    /**
     * Email address of the user.
     */
    private String email;

    /**
     * Password chosen by the user.
     */
    private String password;

    /**
     * Default constructor.
     */
    public RegisterRequest() {
        logger.debug("RegisterRequest object created.");
    }

    /**
     * Returns the user's name.
     *
     * @return user's name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the user's name.
     *
     * @param name user's name
     */
    public void setName(String name) {
        logger.debug("Setting user name.");
        this.name = name;
    }

    /**
     * Returns the user's email.
     *
     * @return user's email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the user's email.
     *
     * @param email user's email
     */
    public void setEmail(String email) {
        logger.debug("Setting user email.");
        this.email = email;
    }

    /**
     * Returns the user's password.
     *
     * @return user's password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the user's password.
     *
     * <b>Note:</b> The actual password value is never logged for security reasons.
     *
     * @param password user's password
     */
    public void setPassword(String password) {
        logger.debug("Setting user password.");
        this.password = password;
    }
}