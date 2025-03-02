package com.eventmgmt.dao;

import com.eventmgmt.model.Event;
import com.eventmgmt.model.EventType;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Data Access Object (DAO) for managing Event entities.
 */
public class EventDAO extends BaseDAO<Event, String> {

    /**
     * Finds events by their location.
     * 
     * @param location The location to search for (partial match)
     * @return A list of events at the specified location
     */
    public List<Event> findByLocation(String location) {
        return executeQuery(
                "SELECT e FROM Event e WHERE LOWER(e.location) LIKE LOWER(:location)",
                "location", "%" + location + "%");
    }

    /**
     * Finds events by event type.
     * 
     * @param type The event type to search for
     * @return A list of events of the specified type
     */
    public List<Event> findByType(EventType type) {
        return executeQuery(
                "SELECT e FROM Event e WHERE e.type = :type",
                "type", type);
    }

    /**
     * Finds events by date range.
     * 
     * @param startDate The start date of the range
     * @param endDate   The end date of the range
     * @return A list of events within the specified date range
     */
    public List<Event> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return executeQuery(
                "SELECT e FROM Event e WHERE e.eventDate BETWEEN :startDate AND :endDate",
                "startDate", startDate,
                "endDate", endDate);
    }

    /**
     * Finds events by creator.
     * 
     * @param creatorId The ID of the creator user
     * @return A list of events created by the specified user
     */
    public List<Event> findByCreator(String creatorId) {
        return executeQuery(
                "SELECT e FROM Event e WHERE e.creator.id = :creatorId",
                "creatorId", creatorId);
    }

    /**
     * Finds upcoming events from the current time.
     * 
     * @param limit The maximum number of events to return
     * @return A list of upcoming events
     */
    @SuppressWarnings("unchecked")
    public List<Event> findUpcomingEvents(int limit) {
        return executeInTransaction(em -> {
            Query query = em.createQuery(
                    "SELECT e FROM Event e WHERE e.eventDate > CURRENT_TIMESTAMP ORDER BY e.eventDate ASC");
            query.setMaxResults(limit);
            return query.getResultList();
        });
    }

    /**
     * Finds events with available capacity.
     * 
     * @return A list of events with remaining capacity
     */
    public List<Event> findEventsWithAvailableCapacity() {
        return executeQuery(
                "SELECT e FROM Event e WHERE e.capacity > 0");
    }

    /**
     * Search for events based on multiple criteria.
     * Uses JPQL with dynamic conditions for flexibility.
     * 
     * @param name      Event name (partial match)
     * @param location  Event location (partial match)
     * @param type      Event type
     * @param startDate Minimum event date
     * @param endDate   Maximum event date
     * @return A list of events matching the specified criteria
     */
    @SuppressWarnings("unchecked")
    public List<Event> searchEvents(String name, String location, EventType type,
            LocalDateTime startDate, LocalDateTime endDate) {
        return executeInTransaction(em -> {
            StringBuilder jpql = new StringBuilder("SELECT e FROM Event e WHERE 1=1");

            if (name != null && !name.isEmpty()) {
                jpql.append(" AND LOWER(e.name) LIKE LOWER(:name)");
            }

            if (location != null && !location.isEmpty()) {
                jpql.append(" AND LOWER(e.location) LIKE LOWER(:location)");
            }

            if (type != null) {
                jpql.append(" AND e.type = :type");
            }

            if (startDate != null) {
                jpql.append(" AND e.eventDate >= :startDate");
            }

            if (endDate != null) {
                jpql.append(" AND e.eventDate <= :endDate");
            }

            jpql.append(" ORDER BY e.eventDate ASC");

            Query query = em.createQuery(jpql.toString());

            if (name != null && !name.isEmpty()) {
                query.setParameter("name", "%" + name.toLowerCase() + "%");
            }

            if (location != null && !location.isEmpty()) {
                query.setParameter("location", "%" + location.toLowerCase() + "%");
            }

            if (type != null) {
                query.setParameter("type", type);
            }

            if (startDate != null) {
                query.setParameter("startDate", startDate);
            }

            if (endDate != null) {
                query.setParameter("endDate", endDate);
            }

            return query.getResultList();
        });
    }
}