package com.eventmgmt.repository;

import com.eventmgmt.dao.EventDAO;
import com.eventmgmt.model.Event;
import com.eventmgmt.model.EventType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository class for Event entities.
 * Acts as a facade over the DAO layer, providing higher-level business
 * operations.
 */
public class EventRepository {

    private final EventDAO eventDAO;

    public EventRepository() {
        this.eventDAO = new EventDAO();
    }

    /**
     * Finds all events.
     * 
     * @return A list of all events
     */
    public List<Event> findAll() {
        return eventDAO.findAll();
    }

    /**
     * Finds an event by its ID.
     * 
     * @param id The ID of the event
     * @return An Optional containing the event if found
     */
    public Optional<Event> findById(String id) {
        return eventDAO.findById(id);
    }

    /**
     * Saves an event.
     * 
     * @param event The event to save
     * @return The saved event
     */
    public Event save(Event event) {
        return eventDAO.save(event);
    }

    /**
     * Deletes an event by its ID.
     * 
     * @param id The ID of the event to delete
     * @return true if the event was found and deleted, false otherwise
     */
    public boolean deleteById(String id) {
        return eventDAO.deleteById(id);
    }

    /**
     * Finds events by location (partial match).
     * 
     * @param location The location to search for
     * @return A list of events at the matching location
     */
    public List<Event> findByLocation(String location) {
        return eventDAO.findByLocation(location);
    }

    /**
     * Finds events by type.
     * 
     * @param type The event type
     * @return A list of events of the specified type
     */
    public List<Event> findByType(EventType type) {
        return eventDAO.findByType(type);
    }

    /**
     * Finds events on a specific date.
     * 
     * @param date The date to search for
     * @return A list of events on the specified date
     */
    public List<Event> findByDate(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
        return eventDAO.findByDateRange(startOfDay, endOfDay);
    }

    /**
     * Finds events in a date range.
     * 
     * @param startDate The start date
     * @param endDate   The end date
     * @return A list of events within the date range
     */
    public List<Event> findByDateRange(LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(LocalTime.MAX);
        return eventDAO.findByDateRange(start, end);
    }

    /**
     * Finds upcoming events.
     * 
     * @param limit The maximum number of events to return
     * @return A list of upcoming events
     */
    public List<Event> findUpcomingEvents(int limit) {
        return eventDAO.findUpcomingEvents(limit);
    }

    /**
     * Finds events created by a user.
     * 
     * @param userId The ID of the user
     * @return A list of events created by the user
     */
    public List<Event> findByCreator(String userId) {
        return eventDAO.findByCreator(userId);
    }

    /**
     * Searches for events based on multiple criteria.
     * 
     * @param name      Event name (partial match)
     * @param location  Event location (partial match)
     * @param type      Event type
     * @param startDate Minimum event date
     * @param endDate   Maximum event date
     * @return A list of events matching the criteria
     */
    public List<Event> searchEvents(String name, String location, EventType type,
            LocalDate startDate, LocalDate endDate) {

        LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : null;
        LocalDateTime endDateTime = endDate != null ? endDate.atTime(LocalTime.MAX) : null;

        return eventDAO.searchEvents(name, location, type, startDateTime, endDateTime);
    }

    /**
     * Finds events with filtered criteria, useful for search functionality.
     * 
     * @param location The location to filter by (optional)
     * @param activity The activity/type to filter by (optional)
     * @param dateStr  The date to filter by in ISO format (optional)
     * @return A list of events matching the filters
     */
    public List<Event> getFilteredEvents(String location, String activity, String dateStr) {
        EventType type = null;

        // Try to parse activity string to EventType if provided
        if (activity != null && !activity.trim().isEmpty()) {
            try {
                type = EventType.valueOf(activity.toUpperCase());
            } catch (IllegalArgumentException e) {
                // Not a valid enum value, will use as name search instead
            }
        }

        // Parse date if provided
        LocalDate date = null;
        if (dateStr != null && !dateStr.trim().isEmpty()) {
            try {
                date = LocalDate.parse(dateStr);
            } catch (Exception e) {
                // Invalid date format, ignore this filter
            }
        }

        // Use the search method with the parsed filters
        // Using activity as name filter if it's not a valid EventType
        return searchEvents(
                type == null ? activity : null,
                location,
                type,
                date,
                date);
    }
}