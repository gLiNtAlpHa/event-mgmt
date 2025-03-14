<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>VibeHalo - Event Listing</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/flatpickr/4.6.13/flatpickr.min.css">
    <script src="https://cdnjs.cloudflare.com/ajax/libs/flatpickr/4.6.13/flatpickr.min.js"></script>
</head>
<body class="bg-gray-100">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <!-- Header Section -->
        <header class="flex justify-between items-center mb-8">
            <div>
                <h1 class="text-3xl font-bold">VibeHalo</h1>
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
                <a href="#" class="bg-black text-white px-4 py-2 rounded-md">Host an Event</a>
            </div>
        </header>

        <!-- Hero Section -->
        <div class="relative rounded-xl overflow-hidden mb-12 h-80">
            <!-- Carousel container -->
            <div id="heroCarousel" class="h-full w-full">
                <!-- Slide 1 -->
                <div class="carousel-item absolute inset-0 opacity-100 transition-opacity duration-500" 
                     style="background-image: url('https://img.freepik.com/free-vector/scheduling-planning-setting-goals-schedule-timing-workflow-optimization-taking-note-assignment-businesswoman-with-timetable-cartoon-character-vector-isolated-concept-metaphor-illustration_335657-2851.jpg?uid=R71886669&ga=GA1.1.658547807.1740966066&semt=ais_hybrid'); background-size: cover; background-position: center;">
                    <div class="absolute inset-0 bg-gradient-to-r from-gray-900/70 to-transparent"></div>
                    <div class="relative p-8 md:p-12 h-full flex flex-col justify-between">
                        <div>
                            <p class="text-white/80 mb-2">Effortless Planning, Seamless Execution</p>
                            <h2 class="text-5xl md:text-6xl font-bold text-white mb-4">Host Unforgettable Events</h2>
                            <p class="text-white font-medium">From luxurious dinners to corporate galas, VibeHalo ensures your event is a masterpiece.  
                                Plan, manage, and execute like a pro with our all-in-one event management platform.</p>
                        </div>
                    </div>
                </div>
                
                <!-- Slide 2 -->
                <div class="carousel-item absolute inset-0 opacity-0 transition-opacity duration-500" 
                     style="background-image: url('https://img.freepik.com/free-vector/water-contamination-detection-system-abstract-concept-illustration_335657-3772.jpg?uid=R71886669&ga=GA1.1.658547807.1740966066&semt=ais_hybrid'); background-size: cover; background-position: center;">
                    <div class="absolute inset-0 bg-gradient-to-r from-gray-900/70 to-transparent"></div>
                    <div class="relative p-8 md:p-12 h-full flex flex-col justify-between">
                        <div>
                            <p class="text-white/80 mb-2">Smart Automation, Smooth Coordination</p>
                            <h2 class="text-5xl md:text-6xl font-bold text-white mb-4">Next-Gen Event Management</h2>
                            <p class="text-white font-medium">AI-driven tools help you track registrations, send notifications, 
                                so you can focus on creating epic experiences.</p>
                        </div>
                    </div>
                </div>
                
                <!-- Slide 3 -->
                <div class="carousel-item absolute inset-0 opacity-0 transition-opacity duration-500" 
                     style="background-image: url('https://img.freepik.com/free-vector/hand-drawn-garden-party-illustration_52683-89332.jpg?uid=R71886669&ga=GA1.1.658547807.1740966066&semt=ais_hybrid'); background-size: cover; background-position: center;">
                    <div class="absolute inset-0 bg-gradient-to-r from-gray-900/70 to-transparent"></div>
                    <div class="relative p-8 md:p-12 h-full flex flex-col justify-between">
                        <div>
                            <p class="text-white/80 mb-2">Memories That Last a Lifetime</p>
                            <h2 class="text-5xl md:text-6xl font-bold text-white mb-4">Turn Moments Into Magic</h2>
                            <p class="text-white font-medium"> Whether it's a high-energy concert or an intimate wedding, VibeHalo takes care of the details.  
                                You bring the vision, we bring the vibe. Let’s make it legendary. 🎉</p>
                        </div>
                    </div>
                </div>
            </div>
            
            <!-- Carousel Navigation -->
            <div class="absolute bottom-8 left-8 md:left-12 flex space-x-2 z-10">
                <button id="prevBtn" class="w-8 h-8 rounded-full bg-white flex items-center justify-center shadow hover:bg-gray-100 transition-colors">
                    <i class="fas fa-chevron-left text-xs"></i>
                </button>
                <button id="nextBtn" class="w-8 h-8 rounded-full bg-white flex items-center justify-center shadow hover:bg-gray-100 transition-colors">
                    <i class="fas fa-chevron-right text-xs"></i>
                </button>
            </div>
            
            <!-- Carousel Indicators -->
            <div class="absolute bottom-8 right-8 md:right-12 flex space-x-2 z-10">
                <button data-slide="0" class="carousel-indicator w-2 h-2 rounded-full bg-white opacity-100"></button>
                <button data-slide="1" class="carousel-indicator w-2 h-2 rounded-full bg-white opacity-50"></button>
                <button data-slide="2" class="carousel-indicator w-2 h-2 rounded-full bg-white opacity-50"></button>
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
            const carousel = document.getElementById('heroCarousel');
            const slides = carousel.querySelectorAll('.carousel-item');
            const indicators = document.querySelectorAll('.carousel-indicator');
            const prevBtn = document.getElementById('prevBtn');
            const nextBtn = document.getElementById('nextBtn');
            let currentSlide = 0;
            const totalSlides = slides.length;
            
            function showSlide(index) {
                slides.forEach(slide => {
                    slide.classList.remove('opacity-100');
                    slide.classList.add('opacity-0');
                });
                
                // Update indicators
                indicators.forEach((indicator, i) => {
                    if (i === index) {
                        indicator.classList.remove('opacity-50');
                        indicator.classList.add('opacity-100');
                    } else {
                        indicator.classList.remove('opacity-100');
                        indicator.classList.add('opacity-50');
                    }
                });
                
                slides[index].classList.remove('opacity-0');
                slides[index].classList.add('opacity-100');
                
                currentSlide = index;
            }
            
            prevBtn.addEventListener('click', () => {
                let newIndex = currentSlide - 1;
                if (newIndex < 0) newIndex = totalSlides - 1;
                showSlide(newIndex);
            });
            
            nextBtn.addEventListener('click', () => {
                let newIndex = currentSlide + 1;
                if (newIndex >= totalSlides) newIndex = 0;
                showSlide(newIndex);
            });
            
            // indicators
            indicators.forEach((indicator, index) => {
                indicator.addEventListener('click', () => {
                    showSlide(index);
                });
            });
            
            setInterval(() => {
                let newIndex = currentSlide + 1;
                if (newIndex >= totalSlides) newIndex = 0;
                showSlide(newIndex);
            }, 5000);
            
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
            fetchEvents();
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
                    return response.text().then(text => {
                        console.log('Raw response:', text);
                        // If text is empty, throw a specific error
                        if (!text.trim()) {
                            throw new Error('Empty response from server');
                        }
                        // Try to parse the text as JSON
                        try {
                            return JSON.parse(text);
                        } catch (e) {
                            console.error('JSON parse error:', e);
                            console.error('Response that failed to parse:', text);
                            throw new Error('Invalid JSON response');
                        }
                    });
                   
                })
                .then(data => {
                    console.log('Fetched events:', data);
                    displayEvents(data);
                })
                .catch(error => {
                    console.log('Error fetching events:', error);
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