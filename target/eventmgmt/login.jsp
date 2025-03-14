<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>VibeHalo - Login</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css" rel="stylesheet">
</head>
<body class="bg-orange-100">
    <div class="min-h-screen flex items-center justify-center p-4">
        <div class="w-full max-w-5xl bg-white rounded-xl shadow-lg overflow-hidden">
            <div class="flex flex-col md:flex-row">
                <!-- Left Side - Login Form -->
                <div class="w-full md:w-1/2 p-8 md:p-12">
                    <div class="mb-8">
                        <h1 class="text-3xl font-bold">VibeHalo</h1>
                    </div>
                    
                    <div class="mb-8">
                        <h2 class="text-3xl font-bold">Welcome back,</h2>
                        <h2 class="text-3xl font-bold mb-2">Event Planner!</h2>
                        <p class="text-gray-600">We are glad to see you again!</p>
                        <p class="text-gray-600">Please, enter your details</p>
                    </div>
                    
                    <div class="mb-6 flex space-x-4">
                        <button class="flex-1 flex items-center justify-center py-2 px-4 border border-gray-300 rounded-md hover:bg-gray-50">
                            <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-2" viewBox="0 0 24 24" fill="currentColor">
                                <path d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z" fill="#4285F4"/>
                                <path d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z" fill="#34A853"/>
                                <path d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z" fill="#FBBC05"/>
                                <path d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z" fill="#EA4335"/>
                            </svg>
                            Log in with Google
                        </button>
                        <button class="flex-1 flex items-center justify-center py-2 px-4 border border-gray-300 rounded-md hover:bg-gray-50">
                            <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-2" viewBox="0 0 24 24" fill="currentColor">
                                <path d="M17.05 20.28c-.98.93-2.04 1.66-3.25 2.16-1.21.5-2.48.75-3.8.75-1.32 0-2.59-.25-3.8-.75-1.21-.5-2.27-1.23-3.25-2.16-.98-.93-1.75-2.01-2.3-3.22-.55-1.21-.83-2.5-.83-3.87 0-1.37.28-2.66.83-3.87.55-1.21 1.32-2.29 2.3-3.22.98-.93 2.04-1.66 3.25-2.16 1.21-.5 2.48-.75 3.8-.75 1.32 0 2.59.25 3.8.75 1.21.5 2.27 1.23 3.25 2.16.98.93 1.75 2.01 2.3 3.22.55 1.21.83 2.5.83 3.87 0 1.37-.28 2.66-.83 3.87-.55 1.21-1.32 2.29-2.3 3.22z" fill="#333"/>
                                <path d="M12 6.38c-1.12 0-2.07.39-2.86 1.17-.79.78-1.18 1.73-1.18 2.85 0 1.12.39 2.07 1.18 2.85.79.78 1.74 1.17 2.86 1.17 1.12 0 2.07-.39 2.86-1.17.79-.78 1.18-1.73 1.18-2.85 0-1.12-.39-2.07-1.18-2.85-.79-.78-1.74-1.17-2.86-1.17z" fill="#fff"/>
                            </svg>
                            Log in with Apple
                        </button>
                    </div>
                    
                    <div class="mb-6 flex items-center">
                        <div class="flex-grow h-px bg-gray-300"></div>
                        <span class="px-4 text-gray-500">or</span>
                        <div class="flex-grow h-px bg-gray-300"></div>
                    </div>
                    
                    <form id="loginForm" class="mb-4" method="post" action="${pageContext.request.contextPath}/api/users/login">
                        <div class="mb-4">
                            <label for="email" class="block text-gray-700 mb-2">Email *</label>
                            <input type="email" id="email" name="email" required 
                                   class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-black" 
                                   placeholder="Enter your email">
                        </div>
                        
                        <div class="mb-6">
                            <label for="password" class="block text-gray-700 mb-2">Password *</label>
                            <input type="password" id="password" name="password" required 
                                   class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-black" 
                                   placeholder="Enter your password">
                        </div>
                        
                        <div class="flex justify-between items-center mb-6">
                            <div class="flex items-center">
                                <input type="checkbox" id="remember" name="remember" class="h-4 w-4 text-black focus:ring-black border-gray-300 rounded">
                                <label for="remember" class="ml-2 block text-gray-700">Remember me</label>
                            </div>
                            <a href="#" class="text-sm text-black hover:underline">Forgot Password?</a>
                        </div>
                        
                        <button type="submit" class="w-full bg-black text-white py-2 px-4 rounded-md hover:bg-gray-800 transition duration-300">
                            Login
                        </button>
                    </form>
                    
                    <p class="text-center text-gray-600">
                        Don't have an account? <a href="${pageContext.request.contextPath}/register" class="text-black hover:underline">Sign up</a>
                    </p>
                </div>
                
                <!-- Right Side - Stats Visualization -->
                <div class="hidden md:block md:w-1/2 bg-gradient-to-tr from-orange-300 to-orange-100 p-12">
                    <div class="h-full flex flex-col justify-between">
                        <!-- Top Card -->
                        <div class="bg-orange-200/50 backdrop-blur-sm rounded-xl p-4 mb-6">
                            <div class="flex items-start">
                                <div class="w-10 h-10 rounded-full bg-orange-300 flex items-center justify-center mr-3">
                                    <i class="fas fa-user text-orange-800"></i>
                                </div>
                                <div>
                                    <p class="text-orange-800/70 mb-1">missing key to our success...</p>
                                    <p class="font-semibold">Event Organizer</p>
                                    <p class="text-sm">CEO at VibeHalo Solutions</p>
                                </div>
                            </div>
                        </div>
                        
                        <!-- Middle Card - Stats -->
                        <div class="bg-white rounded-xl p-6 mb-6">
                            <div class="flex justify-between items-center mb-4">
                                <i class="fas fa-chart-bar text-gray-700"></i>
                                <span class="text-sm text-gray-600">Last month</span>
                            </div>
                            
                            <div class="mb-4">
                                <div class="flex items-center">
                                    <h3 class="text-2xl font-bold">+84.32%</h3>
                                    <span class="ml-2 text-green-500">↑</span>
                                </div>
                            </div>
                            
                            <div class="flex items-end h-32">
                                <div class="w-1/5 h-1/5 bg-orange-200 rounded-t-sm"></div>
                                <div class="w-1/5 h-2/5 bg-orange-300 rounded-t-sm mx-1"></div>
                                <div class="w-1/5 h-3/5 bg-orange-400 rounded-t-sm mx-1"></div>
                                <div class="w-1/5 h-4/5 bg-orange-500 rounded-t-sm mx-1"></div>
                                <div class="w-1/5 h-full bg-orange-500 rounded-t-sm"></div>
                            </div>
                        </div>
                        
                        <!-- Bottom Card -->
                        <div class="bg-orange-200/50 backdrop-blur-sm rounded-xl p-6">
                            <div class="flex justify-between items-center">
                                <i class="fas fa-clock text-orange-800"></i>
                                <span class="text-sm text-orange-800/70">Last month</span>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <script>
        document.addEventListener('DOMContentLoaded', function() {
            const loginForm = document.getElementById('loginForm');
            
            loginForm.addEventListener('submit', function(e) {
                e.preventDefault();
                
                const email = document.getElementById('email').value;
                const password = document.getElementById('password').value;
                
                // Create the request payload
                const data = {
                    email: email,
                    password: password
                };
                
                fetch('${pageContext.request.contextPath}/api/users/login', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify(data),
                    credentials: 'same-origin'
                })
                .then(response => {
                    if (!response.ok) {
                        return response.json().then(error => {
                            throw new Error(error.message || 'Login failed');
                        });
                    }
                    return response.json();
                })
                .then(user => {
                    console.log('Login successful', user);
                    window.location.href = '${pageContext.request.contextPath}/index.jsp';
                })
                .catch(error => {
                    console.error('Login error:', error);
                    // Display error message to the user
                    alert('Login failed: ' + error.message);
                });
            });
        });
    </script>
</body>
</html>