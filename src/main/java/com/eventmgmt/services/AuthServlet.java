package com.eventmgmt.services;

import java.io.IOException;
import java.util.Optional;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.eventmgmt.model.User;
import com.eventmgmt.repository.UserRepository;
import com.eventmgmt.util.EmailService;
import com.eventmgmt.util.PasswordResetTokenManager;

/**
 * Servlet to handle authentication-related pages: login, register, and forgot password.
 */
@WebServlet("/auth/*")
public class AuthServlet extends HttpServlet {

    private UserRepository userRepository;
    private EmailService emailService;
    private PasswordResetTokenManager tokenManager;

    @Override
    public void init() throws ServletException {
        super.init();
        userRepository = new UserRepository();
        emailService = new EmailService();
        tokenManager = new PasswordResetTokenManager();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pathInfo = request.getPathInfo();
        
        // Check if user is already logged in
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("userId") != null) {
            // If user is trying to access login/register pages while logged in, redirect to home
            if ("/login".equals(pathInfo) || "/register".equals(pathInfo)) {
                response.sendRedirect(request.getContextPath() + "/");
                return;
            }
        }

        if (pathInfo == null || pathInfo.equals("/")) {
            // Default to login page
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        } else if (pathInfo.equals("/login")) {
            // Handle any login-specific attributes
            String errorMessage = request.getParameter("error");
            if (errorMessage != null) {
                request.setAttribute("errorMessage", errorMessage);
            }
            
            String successMessage = request.getParameter("success");
            if (successMessage != null) {
                request.setAttribute("successMessage", successMessage);
            }
            
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        } else if (pathInfo.equals("/register")) {
            // Handle any register-specific attributes
            String errorMessage = request.getParameter("error");
            if (errorMessage != null) {
                request.setAttribute("errorMessage", errorMessage);
            }
            
            request.getRequestDispatcher("/register.jsp").forward(request, response);
        } else if (pathInfo.equals("/forgot")) {
            // Handle any forgot-password specific attributes
            String errorMessage = request.getParameter("error");
            if (errorMessage != null) {
                request.setAttribute("errorMessage", errorMessage);
            }
            
            String successMessage = request.getParameter("success");
            if (successMessage != null) {
                request.setAttribute("successMessage", successMessage);
            }
            
            request.getRequestDispatcher("/forgot.jsp").forward(request, response);
        } else if (pathInfo.startsWith("/reset")) {
            // Password reset page with token
            String token = request.getParameter("token");
            if (token == null || token.trim().isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/auth/forgot?error=Invalid or missing token");
                return;
            }
            
            if (!tokenManager.isValidToken(token)) {
                response.sendRedirect(request.getContextPath() + "/auth/forgot?error=Token expired or invalid");
                return;
            }
            
            request.setAttribute("token", token);
            request.getRequestDispatcher("/reset-password.jsp").forward(request, response);
        } else if (pathInfo.equals("/logout")) {
            // Handle logout
            HttpSession userSession = request.getSession(false);
            if (userSession != null) {
                userSession.invalidate();
            }
            
            response.sendRedirect(request.getContextPath() + "/auth/login?success=You have been successfully logged out");
        } else {
            // Handle unknown paths
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pathInfo = request.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/") || pathInfo.equals("/login")) {
            // Handle login form submission
            handleLogin(request, response);
        } else if (pathInfo.equals("/register")) {
            // Handle register form submission
            handleRegister(request, response);
        } else if (pathInfo.equals("/forgot")) {
            // Handle forgot password form submission
            handleForgotPassword(request, response);
        } else if (pathInfo.equals("/reset")) {
            // Handle password reset form submission
            handleResetPassword(request, response);
        } else {
            // Handle unknown paths
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    /**
     * Handles login form submission.
     */
    private void handleLogin(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String rememberMe = request.getParameter("rememberMe");
        
        // Simple validation
        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Email and password are required");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        }

        // Authenticate user
        Optional<User> userOpt = userRepository.authenticate(email, password);
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            
            // Create session
            HttpSession session = request.getSession(true);
            session.setAttribute("userId", user.getId());
            session.setAttribute("userEmail", user.getEmail());
            session.setAttribute("userRole", user.getRole().toString());
            
            // Handle remember me
            if (rememberMe != null && rememberMe.equals("on")) {
                session.setMaxInactiveInterval(7 * 24 * 60 * 60); // 7 days
            }
            
            // Redirect to home page or intended destination
            String redirect = request.getParameter("redirect");
            if (redirect != null && !redirect.trim().isEmpty()) {
                response.sendRedirect(redirect);
            } else {
                response.sendRedirect(request.getContextPath() + "/");
            }
        } else {
            // Authentication failed
            request.setAttribute("errorMessage", "Invalid email or password");
            request.setAttribute("email", email); // Return the email to prefill the form
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }

    /**
     * Handles register form submission.
     */
    private void handleRegister(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        
        // Simple validation
        if (email == null || email.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Email is required");
            forwardWithInputValues(request, response, "/register.jsp");
            return;
        }
        
        if (password == null || password.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Password is required");
            forwardWithInputValues(request, response, "/register.jsp");
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            request.setAttribute("errorMessage", "Passwords do not match");
            forwardWithInputValues(request, response, "/register.jsp");
            return;
        }
        
        // Password strength validation
        if (password.length() < 8) {
            request.setAttribute("errorMessage", "Password must be at least 8 characters long");
            forwardWithInputValues(request, response, "/register.jsp");
            return;
        }
        
        // Check if email is already registered
        if (userRepository.findByEmail(email).isPresent()) {
            request.setAttribute("errorMessage", "Email is already registered");
            forwardWithInputValues(request, response, "/register.jsp");
            return;
        }
        
        // Register user (default to ORGANIZER role)
        User user = userRepository.register(email, password, com.eventmgmt.model.UserRole.ORGANIZER);
        
        if (user != null) {
            // Automatically log in the user after registration
            HttpSession session = request.getSession(true);
            session.setAttribute("userId", user.getId());
            session.setAttribute("userEmail", user.getEmail());
            session.setAttribute("userRole", user.getRole().toString());
            
            // Redirect to home page
            response.sendRedirect(request.getContextPath() + "/");
        } else {
            // Registration failed
            request.setAttribute("errorMessage", "Registration failed. Please try again.");
            forwardWithInputValues(request, response, "/register.jsp");
        }
    }

    /**
     * Handles forgot password form submission.
     */
    private void handleForgotPassword(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String email = request.getParameter("email");
        
        // Simple validation
        if (email == null || email.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Email is required");
            request.getRequestDispatcher("/forgot.jsp").forward(request, response);
            return;
        }
        
        // Check if email exists
        Optional<User> userOpt = userRepository.findByEmail(email);
        
        if (userOpt.isPresent()) {
            // Generate and store reset token
            String token = tokenManager.generateToken(email);
            
            // Generate reset link
            String resetLink = request.getScheme() + "://" + request.getServerName();
            if (request.getServerPort() != 80 && request.getServerPort() != 443) {
                resetLink += ":" + request.getServerPort();
            }
            resetLink += request.getContextPath() + "/auth/reset?token=" + token;
            
            // Send email with reset link
            String emailBody = "Click the link below to reset your password:\n\n" + resetLink;
            emailService.sendEmail(email, "Password Reset Request", emailBody);
            
            // Show success message
            request.setAttribute("successMessage", "Password reset instructions have been sent to your email");
            request.getRequestDispatcher("/forgot.jsp").forward(request, response);
        } else {
            // Email not found, but don't reveal this for security reasons
            request.setAttribute("successMessage", "If the email is registered, password reset instructions will be sent");
            request.getRequestDispatcher("/forgot.jsp").forward(request, response);
        }
    }

    /**
     * Handles password reset form submission.
     */
    private void handleResetPassword(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String token = request.getParameter("token");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        
        // Validate token
        if (token == null || token.trim().isEmpty() || !tokenManager.isValidToken(token)) {
            response.sendRedirect(request.getContextPath() + "/auth/forgot?error=Invalid or expired token");
            return;
        }
        
        // Validate passwords
        if (password == null || password.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Password is required");
            request.setAttribute("token", token);
            request.getRequestDispatcher("/reset-password.jsp").forward(request, response);
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            request.setAttribute("errorMessage", "Passwords do not match");
            request.setAttribute("token", token);
            request.getRequestDispatcher("/reset-password.jsp").forward(request, response);
            return;
        }
        
        // Password strength validation
        if (password.length() < 8) {
            request.setAttribute("errorMessage", "Password must be at least 8 characters long");
            request.setAttribute("token", token);
            request.getRequestDispatcher("/reset-password.jsp").forward(request, response);
            return;
        }
        
        // Get email from token
        String email = tokenManager.getEmailFromToken(token);
        if (email == null) {
            response.sendRedirect(request.getContextPath() + "/auth/forgot?error=Invalid token");
            return;
        }
        
        // Update user's password
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            boolean updated = userRepository.resetPassword(user.getId(), password);
            
            if (updated) {
                // Invalidate token
                tokenManager.invalidateToken(token);
                
                // Redirect to login with success message
                response.sendRedirect(request.getContextPath() + "/auth/login?success=Your password has been reset successfully");
            } else {
                request.setAttribute("errorMessage", "Failed to reset password. Please try again.");
                request.setAttribute("token", token);
                request.getRequestDispatcher("/reset-password.jsp").forward(request, response);
            }
        } else {
            response.sendRedirect(request.getContextPath() + "/auth/forgot?error=User not found");
        }
    }

    /**
     * Helper method to forward request with input values preserved.
     */
    private void forwardWithInputValues(HttpServletRequest request, HttpServletResponse response, String path)
            throws ServletException, IOException {
        
        // Preserve input values for re-display
        String email = request.getParameter("email");
        if (email != null) {
            request.setAttribute("email", email);
        }
        
        // Forward to the target page
        RequestDispatcher dispatcher = request.getRequestDispatcher(path);
        dispatcher.forward(request, response);
    }
}