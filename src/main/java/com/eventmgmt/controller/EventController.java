package com.eventmgmt.controller;

import com.eventmgmt.model.Event;
import com.eventmgmt.model.EventType;
import com.eventmgmt.repository.EventRepository;
import com.eventmgmt.util.LocalDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.Collections;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

/**
 * Controller for handling event-related API requests.
 */
@WebServlet("/api/events/*")
public class EventController extends HttpServlet {

    private EventRepository eventRepository;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        super.init();
        eventRepository = new EventRepository();
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .setPrettyPrinting()
                .create();
    }

    /**
     * Handles GET requests for finding events.
     * Supports:
     * - /api/events - Get all events with optional filtering
     * - /api/events/{id} - Get a specific event by ID
     * - /api/events/upcoming?limit=X - Get X upcoming events
     * - /api/events/creator/{userId} - Get events created by a user
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try (PrintWriter out = response.getWriter()) {
            String pathInfo = request.getPathInfo();

            if (pathInfo == null || pathInfo.equals("/")) {
                // Handle main endpoint with filters
                handleFilteredEvents(request, response);
            } else if (pathInfo.equals("/upcoming")) {
                // Handle upcoming events
                int limit = parseIntParameter(request, "limit", 10);
                List<Event> events = eventRepository.findUpcomingEvents(limit);
                out.print(gson.toJson(events));
            } else if (pathInfo.startsWith("/creator/")) {
                // Handle events by creator
                try {
                    String userId = pathInfo.substring(9);
                    List<Event> events = eventRepository.findByCreator(userId);
                    out.print(gson.toJson(events));
                } catch (Exception e) {
                    sendError(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
                    return;
                }
            } else {
                try {
                    String eventId = pathInfo.substring(1);
                    Optional<Event> event = eventRepository.findById(eventId);

                    if (event.isPresent()) {
                        out.print(gson.toJson(event.get()));
                    } else {
                        sendError(response, HttpServletResponse.SC_NOT_FOUND, "Event not found");
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
     * Handles POST requests for creating new events.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            // Parse request body to Event
            Event event = parseEventFromRequest(request);

            // Save the event
            Event savedEvent = eventRepository.save(event);

            // Return the saved event
            response.setStatus(HttpServletResponse.SC_CREATED);
            PrintWriter out = response.getWriter();
            out.print(gson.toJson(savedEvent));
            out.flush();
        } catch (IllegalArgumentException e) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * Handles PUT requests for updating existing events.
     */
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Event ID required");
            return;
        }

        try {
            String eventId = pathInfo.substring(1);
            Optional<Event> existingEvent = eventRepository.findById(eventId);

            if (existingEvent.isPresent()) {
                // Parse the updated event data
                Event updatedEvent = parseEventFromRequest(request);
                updatedEvent.setId(eventId);

                Event savedEvent = eventRepository.save(updatedEvent);

                PrintWriter out = response.getWriter();
                out.print(gson.toJson(savedEvent));
                out.flush();
            } else {
                sendError(response, HttpServletResponse.SC_NOT_FOUND, "Event not found");
            }
        } catch (IllegalArgumentException e) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * Handles DELETE requests for removing events.
     */
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Event ID required");
            return;
        }

        try {
            String eventId = pathInfo.substring(1);
            boolean deleted = eventRepository.deleteById(eventId);

            if (deleted) {
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } else {
                sendError(response, HttpServletResponse.SC_NOT_FOUND, "Event not found");
            }
        } catch (Exception e) {
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * Handles the filtered events endpoint.
     */
    private void handleFilteredEvents(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String location = request.getParameter("location");
        String activity = request.getParameter("type"); // Activity is the event type
        String dateStr = request.getParameter("date");

        String startDateStr = request.getParameter("startDate");
        String endDateStr = request.getParameter("endDate");

        List<Event> events;

        if (startDateStr != null && endDateStr != null) {
            try {
                LocalDate startDate = LocalDate.parse(startDateStr);
                LocalDate endDate = LocalDate.parse(endDateStr);

                // If other filters exist, use search
                if (location != null || activity != null) {
                    EventType type = parseEventType(activity);
                    events = eventRepository.searchEvents(null, location, type, startDate, endDate);
                } else {
                    // Just date range
                    events = eventRepository.findByDateRange(startDate, endDate);
                }
            } catch (DateTimeParseException e) {
                sendError(response, HttpServletResponse.SC_BAD_REQUEST,
                        "Invalid date format. Use ISO format (yyyy-MM-dd)");
                return;
            }
        } else {
            events = eventRepository.getFilteredEvents(location, activity, dateStr);
        }

        String jsonResponse = gson.toJson(events != null ? events : Collections.emptyList());
        System.out.println("Sending JSON response: " + jsonResponse);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().print(jsonResponse);
        response.getWriter().flush();
    }

    /**
     * Parses an Event object from the request body.
     */
    private Event parseEventFromRequest(HttpServletRequest request) throws IOException {
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

        return gson.fromJson(data, Event.class);
    }

    /**
     * Parses an EventType from a string.
     */
    private EventType parseEventType(String typeStr) {
        if (typeStr == null || typeStr.trim().isEmpty()) {
            return null;
        }

        try {
            return EventType.valueOf(typeStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null; // Not a valid enum value
        }
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
}