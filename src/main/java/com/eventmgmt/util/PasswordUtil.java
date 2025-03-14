package com.eventmgmt.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Utility class for password encryption and verification.
 * Uses SHA-256 hashing with a random salt for secure password storage.
 */
public class PasswordUtil {
    
    private static final String ALGORITHM = "SHA-256";
    private static final int SALT_LENGTH = 16; // 128 bits
    
    /**
     * Generates a random salt.
     * 
     * @return A random salt as a byte array
     */
    public static byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return salt;
    }
    
    /**
     * Hashes a password with a given salt using SHA-256.
     * 
     * @param password The plain text password
     * @param salt The salt for additional security
     * @return The hashed password
     */
    public static byte[] hashPassword(String password, byte[] salt) {
        try {
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            md.update(salt);
            return md.digest(password.getBytes());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }
    
    /**
     * Encrypts a password and returns the salt and hash as a combined string.
     * Format: Base64(salt):Base64(hash)
     * 
     * @param password The plain text password
     * @return A string containing the encoded salt and hash
     */
    public static String encryptPassword(String password) {
        byte[] salt = generateSalt();
        byte[] hash = hashPassword(password, salt);
        
        String saltStr = Base64.getEncoder().encodeToString(salt);
        String hashStr = Base64.getEncoder().encodeToString(hash);
        
        return saltStr + ":" + hashStr;
    }
    
    /**
     * Verifies a password against a stored hash.
     * 
     * @param password The plain text password to verify
     * @param storedValue The stored salt:hash string
     * @return true if the password matches, false otherwise
     */
    public static boolean verifyPassword(String password, String storedValue) {
        try {
            String[] parts = storedValue.split(":");
            if (parts.length != 2) {
                return false;
            }
            
            byte[] salt = Base64.getDecoder().decode(parts[0]);
            byte[] storedHash = Base64.getDecoder().decode(parts[1]);
            
            byte[] computedHash = hashPassword(password, salt);
            
            // Time-constant comparison to prevent timing attacks
            int diff = storedHash.length ^ computedHash.length;
            for (int i = 0; i < storedHash.length && i < computedHash.length; i++) {
                diff |= storedHash[i] ^ computedHash[i];
            }
            
            return diff == 0;
        } catch (Exception e) {
            return false;
        }
    }
}
