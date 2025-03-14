package com.eventmgmt.dao;

import java.util.List;
import java.util.Optional;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import com.eventmgmt.model.User;
import com.eventmgmt.model.UserRole;
import com.eventmgmt.util.PasswordUtil;

/**
 * Data Access Object for User entities.
 * Extends the BaseDAO to handle persistence operations for User entities.
 */
public class UserDAO extends BaseDAO<User, String> {
    
    /**
     * Finds a user by email.
     * 
     * @param email The email to search for
     * @return An Optional containing the user if found
     */
    public Optional<User> findByEmail(String email) {
        return executeInTransaction(em -> {
            try {
                String jpql = "SELECT u FROM User u WHERE u.email = :email";
                Query query = em.createQuery(jpql);
                query.setParameter("email", email);
                return Optional.ofNullable((User) query.getSingleResult());
            } catch (NoResultException e) {
                return Optional.empty();
            }
        });
    }
    
    /**
     * Finds users by role.
     * 
     * @param role The role to search for
     * @return A list of users with the specified role
     */
    public List<User> findByRole(UserRole role) {
        String jpql = "SELECT u FROM User u WHERE u.role = :role";
        return executeQuery(jpql, "role", role);
    }
    
    /**
     * Searches for users by partial email match.
     * 
     * @param emailPattern The email pattern to search for
     * @return A list of users with matching email patterns
     */
    public List<User> searchByEmail(String emailPattern) {
        String jpql = "SELECT u FROM User u WHERE LOWER(u.email) LIKE LOWER(:pattern)";
        return executeQuery(jpql, "pattern", "%" + emailPattern + "%");
    }
    
    /**
     * Verifies user credentials for login.
     * 
     * @param email The user's email
     * @param plainPassword The plain text password to verify
     * @return An Optional containing the user if credentials are valid
     */
    public Optional<User> verifyCredentials(String email, String plainPassword) {
        return findByEmail(email).filter(user -> 
            PasswordUtil.verifyPassword(plainPassword, user.getPassword())
        );
    }
    
    /**
     * Checks if an email is already in use.
     * 
     * @param email The email to check
     * @return true if the email is already in use, false otherwise
     */
    public boolean isEmailInUse(String email) {
        return findByEmail(email).isPresent();
    }
}
