<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>FlyHigh - Event Listing</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/flatpickr/4.6.13/flatpickr.min.css">
    <script src="https://cdnjs.cloudflare.com/ajax/libs/flatpickr/4.6.13/flatpickr.min.js"></script>
</head>
<body class="bg-amber-100">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <!-- Header Section -->
        <header class="flex justify-between items-center mb-8">
            <div>
                <h1 class="text-3xl font-bold">FlyHigh</h1>
            </div>
            <div class="flex items-center space-x-4">
                <nav class="hidden md:flex items-center space-x-4">
                    <a href="#" class="text-gray-800">Destination</a>
                    <span class="text-gray-400">·</span>
                    <a href="#" class="text-gray-800">Bookings</a>
                    <span class="text-gray-400">·</span>
                    <a href="#" class="text-gray-800">Activities</a>
                </nav>
                <button class="p-2 rounded-full">
                    <i class="fas fa-search"></i>
                </button>
            </div>
            <div class="flex items-center space-x-4">
                <a href="#" class="text-gray-800">Log in</a>
                <a href="#" class="bg-black text-white px-4 py-2 rounded-full">Sign up</a>
            </div>
        </header>

        <!-- Hero Section -->
        <div class="relative rounded-xl overflow-hidden mb-12 bg-gray-200">
            <div class="absolute inset-0 bg-gradient-to-r from-gray-200 to-transparent"></div>
            <div class="relative p-8 md:p-12 h-80 flex flex-col justify-between">
                <div>
                    <p class="text-gray-600 mb-2">— Blue Mountain Country Club and Resort</p>
                    <h2 class="text-5xl md:text-6xl font-bold text-gray-900 mb-4">Treebo Tryst</h2>
                    <p class="text-gray-800 font-medium">~02°C Very Cold</p>
                </div>
                <div class="flex space-x-2">
                    <button class="w-8 h-8 rounded-full bg-white flex items-center justify-center shadow">
                        <i class="fas fa-chevron-left text-xs"></i>
                    </button>
                    <button class="w-8 h-8 rounded-full bg-white flex items-center justify-center shadow">
                        <i class="fas fa-chevron-right text-xs"></i>
                    </button>
                </div>
            </div>
        </div>

        <!-- Search Filter -->
        <div class="relative -mt-16 mb-12">
            <div class="bg-white rounded-xl shadow-lg p-6">
                <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
                    <div class="border-r border-gray-200">
                        <label class="block text-gray-800 font-medium mb-2">Location</label>
                        <div class="flex items-center relative">
                            <input type="text" id="locationInput" list="locationOptions" placeholder="Enter Your Destination..." class="w-full outline-none">
                            <datalist id="locationOptions">
                                <c:forEach items="${popularLocations}" var="location">
                                    <option value="${location}"></option>
                                </c:forEach>
                            </datalist>
                            <button class="ml-2 text-gray-500">
                                <i class="fas fa-map-marker-alt"></i>
                            </button>
                        </div>
                    </div>
                    <div class="border-r border-gray-200">
                        <label class="block text-gray-800 font-medium mb-2">Event Type</label>
                        <div class="flex items-center relative">
                            <select id="typeInput" class="w-full outline-none appearance-none">
                                <option value="">All Event Types</option>
                                <c:forEach items="${eventTypes}" var="type">
                                    <option value="${type}">${type}</option>
                                </c:forEach>
                            </select>
                            <div class="pointer-events-none absolute inset-y-0 right-0 flex items-center px-2 text-gray-500">
                                <i class="fas fa-chevron-down"></i>
                            </div>
                        </div>
                    </div>
                    <div>
                        <label class="block text-gray-800 font-medium mb-2">Date</label>
                        <div class="flex items-center">
                            <input type="text" id="dateInput" placeholder="Set date" class="w-full outline-none">
                            <button class="ml-2 text-gray-500">
                                <i class="far fa-calendar"></i>
                            </button>
                        </div>
                    </div>
                </div>
                <div class="flex justify-end mt-4">
                    <button id="searchBtn" class="bg-black text-white w-12 h-12 rounded-full flex items-center justify-center">
                        <i class="fas fa-search"></i>
                    </button>
                </div>
            </div>
        </div>

        <!-- Trending Section -->
        <div class="mb-12">
            <div class="flex justify-between items-center mb-6">
                <div>
                    <h3 class="text-3xl font-bold text-gray-900">Trending Events</h3>
                    <p class="text-gray-600">Discover exciting events and activities!</p>
                </div>
                <div class="flex space-x-2">
                    <button class="w-10 h-10 rounded-full border border-gray-300 flex items-center justify-center">
                        <i class="fas fa-chevron-left text-xs"></i>
                    </button>
                    <button class="w-10 h-10 rounded-full bg-black text-white flex items-center justify-center">
                        <i class="fas fa-chevron-right text-xs"></i>
                    </button>
                </div>
            </div>

            <!-- Events Grid -->
            <div id="eventsGrid" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
                <!-- Initial events from the server -->
                <c:forEach items="${upcomingEvents}" var="event">
                    <div class="bg-white rounded-xl overflow-hidden shadow-md">
                        <div class="relative h-48 w-full">
                            <img src="${not empty event.imgUrl ? event.imgUrl : '/api/placeholder/400/320'}" alt="${event.name}" class="w-full h-full object-cover">
                            <c:if test="${not empty event.rating}">
                                <div class="absolute top-2 right-2 bg-amber-400 text-white px-2 py-1 rounded">${event.rating}</div>
                            </c:if>
                        </div>
                        <div class="p-4">
                            <h4 class="text-lg font-semibold text-gray-900 mb-1">${event.name}</h4>
                            <p class="text-sm text-gray-500">
                                <fmt:parseDate value="${event.eventDate}" pattern="yyyy-MM-dd'T'HH:mm:ss" var="parsedDate" />
                                <fmt:formatDate value="${parsedDate}" pattern="MMM d, yyyy" /> • ${event.location}
                            </p>
                        </div>
                    </div>
                </c:forEach>
                
                <!-- If no events, show message -->
                <c:if test="${empty upcomingEvents}">
                    <div class="col-span-4 text-center py-8">
                        <p class="text-gray-500">No events found. Try different search criteria.</p>
                    </div>
                </c:if>
            </div>
        </div>
    </div>

    <!-- Event card template (hidden) -->
    <template id="eventCardTemplate">
        <div class="bg-white rounded-xl overflow-hidden shadow-md">
            <div class="relative h-48 w-full">
                <img src="" alt="" class="event-img w-full h-full object-cover">
                <div class="absolute top-2 right-2 bg-amber-400 text-white px-2 py-1 rounded event-rating">4.5</div>
            </div>
            <div class="p-4">
                <h4 class="text-lg font-semibold text-gray-900 mb-1 event-name"></h4>
                <p class="text-sm text-gray-500 event-details"></p>
            </div>
        </div>
    </template>

    <script>
        document.addEventListener('DOMContentLoaded', function() {
            flatpickr("#dateInput", {
                dateFormat: "Y-m-d",
                defaultDate: "${currentDate}",
                minDate: "today"
            });
            document.getElementById('searchBtn').addEventListener('click', function() {
                const location = document.getElementById('locationInput').value;
                const type = document.getElementById('typeInput').value;
                const date = document.getElementById('dateInput').value;
                
                fetchEvents(location, type, date);
            });
            
            document.getElementById('locationInput').addEventListener('keypress', function(e) {
                if (e.key === 'Enter') {
                    document.getElementById('searchBtn').click();
                }
            });
            
            document.getElementById('dateInput').addEventListener('change', function() {
                document.getElementById('searchBtn').click();
            });
        });

        function fetchEvents(location = '', type = '', date = '') {
            const eventsGrid = document.getElementById('eventsGrid');
            eventsGrid.innerHTML = `
                <div class="col-span-4 text-center py-8">
                    <div class="inline-block animate-spin rounded-full h-8 w-8 border-b-2 border-gray-900"></div>
                    <p class="mt-2 text-gray-600">Loading events...</p>
                </div>
            `;

            let queryParams = new URLSearchParams();
            if (location) queryParams.append('location', location);
            if (type) queryParams.append('type', type);
            if (date) queryParams.append('date', date);
            
            const queryString = queryParams.toString() ? `?${queryParams.toString()}` : '';
            fetch(`${pageContext.request.contextPath}/api/events${queryString}`)
                .then(response => {
                    if (!response.ok) {
                        throw new Error('Network response was not ok');
                    }
                    return response.json();
                })
                .then(data => {
                    displayEvents(data);
                })
                .catch(error => {
                    console.error('Error fetching events:', error);
                    eventsGrid.innerHTML = `
                        <div class="col-span-4 text-center py-8">
                            <p class="text-red-500">Failed to load events. Please try again.</p>
                        </div>
                    `;
                });
        }

        function displayEvents(events) {
            const eventsGrid = document.getElementById('eventsGrid');
            const template = document.getElementById('eventCardTemplate');
            
            eventsGrid.innerHTML = '';
            
            if (events.length === 0) {
                eventsGrid.innerHTML = `
                    <div class="col-span-4 text-center py-8">
                        <p class="text-gray-500">No events found. Try different search criteria.</p>
                    </div>
                `;
                return;
            }
            
            events.forEach(event => {
                const clone = template.content.cloneNode(true);
                
                const imgEl = clone.querySelector('.event-img');
                imgEl.src = event.imgUrl || `${pageContext.request.contextPath}/api/placeholder/400/320`;
                imgEl.alt = event.name;
                
                const ratingEl = clone.querySelector('.event-rating');
                if (event.rating) {
                    ratingEl.textContent = event.rating;
                } else {
                    ratingEl.style.display = 'none';
                }
            
                clone.querySelector('.event-name').textContent = event.name;
            
                let formattedDate = '';
                if (event.eventDate) {
                    const date = new Date(event.eventDate);
                    formattedDate = new Intl.DateTimeFormat('en-US', {
                        month: 'short', 
                        day: 'numeric', 
                        year: 'numeric'
                    }).format(date);
                }
                
                clone.querySelector('.event-details').textContent = 
                    `${formattedDate} • ${event.location}`;
                
                eventsGrid.appendChild(clone);
            });
        }
    </script>
</body>
</html>