package com.eventmgmt.util;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages password reset tokens.
 * In a production environment, these should be stored in a database with proper encryption.
 */
public class PasswordResetTokenManager {

    private final Map<String, TokenInfo> tokens = new ConcurrentHashMap<>();
    
    // Token expiration time in minutes
    private static final int TOKEN_EXPIRATION_MINUTES = 60;
    
    /**
     * Generates a new password reset token for the given email.
     * 
     * @param email User email address
     * @return Generated token string
     */
    public String generateToken(String email) {
        // Generate a random token using UUID and additional randomness
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[20];
        random.nextBytes(bytes);
        String token = UUID.randomUUID().toString() + 
                       Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        token = token.replaceAll("[^a-zA-Z0-9]", "").substring(0, 40);
        
        // Store token with expiration
        TokenInfo tokenInfo = new TokenInfo(email, LocalDateTime.now().plusMinutes(TOKEN_EXPIRATION_MINUTES));
        tokens.put(token, tokenInfo);
        
        return token;
    }
    
    /**
     * Checks if a token is valid and not expired.
     * 
     * @param token Token to validate
     * @return true if token is valid, false otherwise
     */
    public boolean isValidToken(String token) {
        TokenInfo tokenInfo = tokens.get(token);
        
        if (tokenInfo == null) {
            return false;
        }
        
        // Check if token has expired
        if (LocalDateTime.now().isAfter(tokenInfo.expirationTime)) {
            tokens.remove(token);
            return false;
        }
        
        return true;
    }
    
    /**
     * Gets the email associated with a token.
     * 
     * @param token Token to lookup
     * @return Email address or null if token is invalid
     */
    public String getEmailFromToken(String token) {
        TokenInfo tokenInfo = tokens.get(token);
        return tokenInfo != null ? tokenInfo.email : null;
    }
    
    /**
     * Invalidates a token.
     * 
     * @param token Token to invalidate
     */
    public void invalidateToken(String token) {
        tokens.remove(token);
    }
    
    /**
     * Cleans up expired tokens.
     * my intention with this is to call it periodically by a scheduled task. 
     */
    public void cleanupExpiredTokens() {
        LocalDateTime now = LocalDateTime.now();
        tokens.entrySet().removeIf(entry -> now.isAfter(entry.getValue().expirationTime));
    }
    
    /**
     * Inner class to hold token information.
     */
    private static class TokenInfo {
        private final String email;
        private final LocalDateTime expirationTime;
        
        public TokenInfo(String email, LocalDateTime expirationTime) {
            this.email = email;
            this.expirationTime = expirationTime;
        }
    }
}