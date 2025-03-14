package com.eventmgmt.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Optional;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.eventmgmt.model.User;
import com.eventmgmt.model.UserRole;
import com.eventmgmt.repository.UserRepository;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

/**
 * Controller for handling user-related API requests.
 */
@WebServlet("/api/users/*")
public class UserController extends HttpServlet {

    private UserRepository userRepository;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        super.init();
        userRepository = new UserRepository();
        gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
    }

    /**
     * Handles GET requests for user operations.
     * Supports:
     * - /api/users - Get all users (with optional pagination)
     * - /api/users/{id} - Get a specific user by ID
     * - /api/users/email/{email} - Get a user by email
     * - /api/users/role/{role} - Get users by role
     * - /api/users/search?email={pattern} - Search users by email pattern
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try (PrintWriter out = response.getWriter()) {
            String pathInfo = request.getPathInfo();

            if (pathInfo == null || pathInfo.equals("/")) {
                // Handle pagination for listing all users
                int offset = parseIntParameter(request, "offset", 0);
                int limit = parseIntParameter(request, "limit", 10);
                List<User> users = userRepository.getPaginatedUsers(offset, limit);
                
                // Add headers for pagination
                if (request.getParameter("count") != null) {
                    response.setHeader("X-Total-Count", userRepository.countUsers().toString());
                }
                
                out.print(gson.toJson(users));
            } else if (pathInfo.startsWith("/email/")) {
                // Handle get user by email
                String email = pathInfo.substring(7);
                Optional<User> user = userRepository.findByEmail(email);
                
                if (user.isPresent()) {
                    out.print(gson.toJson(user.get()));
                } else {
                    sendError(response, HttpServletResponse.SC_NOT_FOUND, "User not found");
                    return;
                }
            } else if (pathInfo.startsWith("/role/")) {
                // Handle get users by role
                try {
                    String roleStr = pathInfo.substring(6);
                    UserRole role = UserRole.valueOf(roleStr.toUpperCase());
                    List<User> users = userRepository.findByRole(role);
                    out.print(gson.toJson(users));
                } catch (IllegalArgumentException e) {
                    sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid user role");
                    return;
                }
            } else if (pathInfo.equals("/search")) {
                // Handle search by email pattern
                String emailPattern = request.getParameter("email");
                if (emailPattern == null || emailPattern.trim().isEmpty()) {
                    sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Email pattern is required");
                    return;
                }
                
                List<User> users = userRepository.searchByEmail(emailPattern);
                out.print(gson.toJson(users));
            } else {
                // Handle get user by ID
                try {
                    String userId = pathInfo.substring(1);
                    Optional<User> user = userRepository.findById(userId);
                    
                    if (user.isPresent()) {
                        out.print(gson.toJson(user.get()));
                    } else {
                        sendError(response, HttpServletResponse.SC_NOT_FOUND, "User not found");
                        return;
                    }
                } catch (Exception e) {
                    sendError(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
                    return;
                }
            }
            
            out.flush();
        } catch (Exception e) {
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * Handles POST requests for user registration and login.
     * Supports:
     * - /api/users/register - Register a new user
     * - /api/users/login - Authenticate a user
     * - /api/users - Create a user (admin operation)
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String pathInfo = request.getPathInfo();
        
        try {
            if (pathInfo != null && pathInfo.equals("/register")) {
                // Handle user registration
                JsonObject requestJson = parseJsonRequest(request);
                
                String email = requestJson.get("email").getAsString();
                String password = requestJson.get("password").getAsString();
                UserRole role = UserRole.valueOf(requestJson.get("role").getAsString().toUpperCase());
                
                User user = userRepository.register(email, password, role);
                
                if (user == null) {
                    sendError(response, HttpServletResponse.SC_CONFLICT, "Email already in use");
                    return;
                }
                
                response.setStatus(HttpServletResponse.SC_CREATED);
                PrintWriter out = response.getWriter();
                out.print(gson.toJson(user));
                out.flush();
            } else if (pathInfo != null && pathInfo.equals("/login")) {
                // Handle user login
                JsonObject requestJson = parseJsonRequest(request);
                
                String email = requestJson.get("email").getAsString();
                String password = requestJson.get("password").getAsString();
                
                Optional<User> userOpt = userRepository.authenticate(email, password);
                
                if (userOpt.isPresent()) {
                    User user = userOpt.get();
                    
                    // Create a session for the authenticated user
                    HttpSession session = request.getSession(true);
                    session.setAttribute("userId", user.getId());
                    session.setAttribute("userRole", user.getRole().toString());
                    
                    PrintWriter out = response.getWriter();
                    out.print(gson.toJson(user));
                    out.flush();
                } else {
                    sendError(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid credentials");
                }
            } else {
                // Handle regular user creation (admin operation)
                if (!isAdmin(request)) {
                    sendError(response, HttpServletResponse.SC_FORBIDDEN, "Admin access required");
                    return;
                }
                
                User user = parseUserFromRequest(request);
                User savedUser = userRepository.save(user);
                
                response.setStatus(HttpServletResponse.SC_CREATED);
                PrintWriter out = response.getWriter();
                out.print(gson.toJson(savedUser));
                out.flush();
            }
        } catch (IllegalArgumentException e) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * Handles PUT requests for updating user information.
     * Supports:
     * - /api/users/{id} - Update a user's details
     * - /api/users/{id}/password - Change a user's password
     */
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "User ID required");
            return;
        }

        try {
            if (pathInfo.endsWith("/password")) {
                // Handle password change
                String userId = pathInfo.substring(1, pathInfo.length() - 9);
                
                if (!isUserAuthorized(request, userId)) {
                    sendError(response, HttpServletResponse.SC_FORBIDDEN, "Not authorized");
                    return;
                }
                
                JsonObject requestJson = parseJsonRequest(request);
                String currentPassword = requestJson.get("currentPassword").getAsString();
                String newPassword = requestJson.get("newPassword").getAsString();
                
                boolean success = userRepository.changePassword(userId, currentPassword, newPassword);
                
                if (success) {
                    response.setStatus(HttpServletResponse.SC_OK);
                    JsonObject result = new JsonObject();
                    result.addProperty("success", true);
                    PrintWriter out = response.getWriter();
                    out.print(gson.toJson(result));
                    out.flush();
                } else {
                    sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid current password");
                }
            } else {
                // Handle user update
                String userId = pathInfo.substring(1);
                
                if (!isUserAuthorized(request, userId) && !isAdmin(request)) {
                    sendError(response, HttpServletResponse.SC_FORBIDDEN, "Not authorized");
                    return;
                }
                
                JsonObject requestJson = parseJsonRequest(request);
                
                String email = requestJson.has("email") ? requestJson.get("email").getAsString() : null;
                String password = requestJson.has("password") ? requestJson.get("password").getAsString() : null;
                UserRole role = requestJson.has("role") ? 
                    UserRole.valueOf(requestJson.get("role").getAsString().toUpperCase()) : null;
                
                Optional<User> updatedUser = userRepository.updateUser(userId, email, password, role);
                
                if (updatedUser.isPresent()) {
                    PrintWriter out = response.getWriter();
                    out.print(gson.toJson(updatedUser.get()));
                    out.flush();
                } else {
                    sendError(response, HttpServletResponse.SC_NOT_FOUND, "User not found or email already in use");
                }
            }
        } catch (IllegalArgumentException e) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * Handles DELETE requests for removing users.
     * Requires admin privileges.
     */
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Check for admin privileges
        if (!isAdmin(request)) {
            sendError(response, HttpServletResponse.SC_FORBIDDEN, "Admin access required");
            return;
        }

        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "User ID required");
            return;
        }

        try {
            String userId = pathInfo.substring(1);
            boolean deleted = userRepository.deleteById(userId);

            if (deleted) {
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } else {
                sendError(response, HttpServletResponse.SC_NOT_FOUND, "User not found");
            }
        } catch (Exception e) {
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * Parses a User object from the request body.
     */
    private User parseUserFromRequest(HttpServletRequest request) throws IOException {
        StringBuilder buffer = new StringBuilder();
        String line;

        try (BufferedReader reader = request.getReader()) {
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
        }

        String data = buffer.toString();
        if (data == null || data.trim().isEmpty()) {
            throw new IllegalArgumentException("Request body is empty");
        }

        return gson.fromJson(data, User.class);
    }

    /**
     * Parses a JsonObject from the request body.
     */
    private JsonObject parseJsonRequest(HttpServletRequest request) throws IOException {
        StringBuilder buffer = new StringBuilder();
        String line;

        try (BufferedReader reader = request.getReader()) {
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
        }

        String data = buffer.toString();
        if (data == null || data.trim().isEmpty()) {
            throw new IllegalArgumentException("Request body is empty");
        }

        return gson.fromJson(data, JsonObject.class);
    }

    /**
     * Parses an integer parameter with default value.
     */
    private int parseIntParameter(HttpServletRequest request, String paramName, int defaultValue) {
        String paramValue = request.getParameter(paramName);
        if (paramValue == null || paramValue.trim().isEmpty()) {
            return defaultValue;
        }

        try {
            return Integer.parseInt(paramValue);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * Sends an error response.
     */
    private void sendError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);

        JsonObject errorJson = new JsonObject();
        errorJson.addProperty("error", message);

        PrintWriter out = response.getWriter();
        out.print(gson.toJson(errorJson));
        out.flush();
    }

    /**
     * Checks if the current request is from an admin user.
     */
    private boolean isAdmin(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }
        
        String userRole = (String) session.getAttribute("userRole");
        return userRole != null && userRole.equals(UserRole.ADMIN.toString());
    }

    /**
     * Checks if the current request is authorized to access the specified user.
     * A user can access their own details, or an admin can access any user.
     */
    private boolean isUserAuthorized(HttpServletRequest request, String userId) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }
        
        String sessionUserId = (String) session.getAttribute("userId");
        String userRole = (String) session.getAttribute("userRole");
        
        return (sessionUserId != null && sessionUserId.equals(userId)) || 
               (userRole != null && userRole.equals(UserRole.ADMIN.toString()));
    }
}
