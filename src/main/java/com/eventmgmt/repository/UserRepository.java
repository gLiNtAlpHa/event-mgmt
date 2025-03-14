
package com.eventmgmt.repository;

import java.util.List;
import java.util.Optional;

import com.eventmgmt.dao.UserDAO;
import com.eventmgmt.model.Event;
import com.eventmgmt.model.User;
import com.eventmgmt.model.UserRole;
import com.eventmgmt.util.PasswordUtil;

/**
 * Repository class for User entities.
 * Acts as a facade over the DAO layer, providing higher-level business
 * operations with security features like password encryption.
 */
public class UserRepository {

    private final UserDAO userDAO;

    public UserRepository() {
        this.userDAO = new UserDAO();
    }

    /**
     * Finds all users.
     * 
     * @return A list of all users
     */
    public List<User> findAll() {
        return userDAO.findAll();
    }

    /**
     * Finds a user by their ID.
     * 
     * @param id The ID of the user
     * @return An Optional containing the user if found
     */
    public Optional<User> findById(String id) {
        return userDAO.findById(id);
    }

    /**
     * Finds a user by their email.
     * 
     * @param email The email of the user
     * @return An Optional containing the user if found
     */
    public Optional<User> findByEmail(String email) {
        return userDAO.findByEmail(email);
    }

    /**
     * Saves a user.
     * 
     * @param user The user to save
     * @return The saved user
     */
    public User save(User user) {
        return userDAO.save(user);
    }

    /**
     * Deletes a user by their ID.
     * 
     * @param id The ID of the user to delete
     * @return true if the user was found and deleted, false otherwise
     */
    public boolean deleteById(String id) {
        return userDAO.deleteById(id);
    }

    /**
     * Finds users by role.
     * 
     * @param role The user role
     * @return A list of users with the specified role
     */
    public List<User> findByRole(UserRole role) {
        return userDAO.findByRole(role);
    }

    /**
     * Searches for users by partial email match.
     * 
     * @param emailPattern The email pattern to search for
     * @return A list of users with matching email patterns
     */
    public List<User> searchByEmail(String emailPattern) {
        return userDAO.searchByEmail(emailPattern);
    }

    /**
     * Authenticates a user with secure password verification.
     * 
     * @param email The user's email
     * @param plainPassword The user's plain text password
     * @return An Optional containing the user if authentication succeeds
     */
    public Optional<User> authenticate(String email, String plainPassword) {
        return userDAO.verifyCredentials(email, plainPassword);
    }

    /**
     * Registers a new user with encrypted password.
     * 
     * @param email The email for the new user
     * @param plainPassword The plain text password for the new user
     * @param role The role for the new user
     * @return The newly created user, or null if the email is already in use
     */
    public User register(String email, String plainPassword, UserRole role) {
        if (userDAO.isEmailInUse(email)) {
            return null;
        }
        
        User user = new User();
        user.setEmail(email);
        // Encrypt the password before saving
        user.setPassword(PasswordUtil.encryptPassword(plainPassword));
        user.setRole(role);
        
        return userDAO.save(user);
    }

    /**
     * Updates a user's details with secure password handling.
     * 
     * @param userId The ID of the user to update
     * @param email The new email (or null to keep current)
     * @param plainPassword The new plain text password (or null to keep current)
     * @param role The new role (or null to keep current)
     * @return An Optional containing the updated user if found
     */
    public Optional<User> updateUser(String userId, String email, String plainPassword, UserRole role) {
        return userDAO.findById(userId).map(user -> {
            if (email != null && !email.equals(user.getEmail())) {
                if (userDAO.isEmailInUse(email)) {
                    return null; // Email already in use
                }
                user.setEmail(email);
            }
            
            if (plainPassword != null) {
                // Encrypt the new password
                user.setPassword(PasswordUtil.encryptPassword(plainPassword));
            }
            
            if (role != null) {
                user.setRole(role);
            }
            
            return userDAO.save(user);
        });
    }

    /**
     * Change a user's password with secure verification and encryption.
     * 
     * @param userId The ID of the user
     * @param currentPassword The current password for verification
     * @param newPassword The new password to set
     * @return true if the password was changed successfully, false otherwise
     */
    public boolean changePassword(String userId, String currentPassword, String newPassword) {
        return userDAO.findById(userId).map(user -> {
            // Verify the current password
            if (!PasswordUtil.verifyPassword(currentPassword, user.getPassword())) {
                return false;
            }
            
            // Encrypt and set the new password
            user.setPassword(PasswordUtil.encryptPassword(newPassword));
            userDAO.save(user);
            return true;
        }).orElse(false);
    }

    /**
     * Reset a user's password with secure verification and encryption.
     * 
     * @param userId The ID of the user
     * @param newPassword The new password to set
     * @return true if the password was changed successfully, false otherwise
     */
    public boolean resetPassword(String userId, String newPassword) {
        return userDAO.findById(userId).map(user -> {
            // Encrypt and set the new password
            user.setPassword(PasswordUtil.encryptPassword(newPassword));
            userDAO.save(user);
            return true;
        }).orElse(false);
    }

    /**
     * Adds an event to a user's created events.
     * 
     * @param userId The ID of the user
     * @param event The event to add
     * @return The updated user if found, or empty if not found
     */
    public Optional<User> addCreatedEvent(String userId, Event event) {
        return userDAO.findById(userId).map(user -> {
            user.addCreatedEvent(event);
            return userDAO.save(user);
        });
    }

    /**
     * Gets the total number of users.
     * 
     * @return The total number of users
     */
    public Long countUsers() {
        return userDAO.count();
    }

    /**
     * Gets paginated users.
     * 
     * @param offset The offset for pagination
     * @param limit The maximum number of results to return
     * @return A list of users for the requested page
     */
    public List<User> getPaginatedUsers(int offset, int limit) {
        return userDAO.findAll(offset, limit);
    }
}
