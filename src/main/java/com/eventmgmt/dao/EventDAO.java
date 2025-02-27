package com.eventmgmt.dao;

import com.eventmgmt.model.Event;
import com.eventmgmt.model.EventType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
    public List<Event> findUpcomingEvents(int limit) {
        return executeInTransaction(em -> {
            TypedQuery<Event> query = em.createQuery(
                    "SELECT e FROM Event e WHERE e.eventDate > CURRENT_TIMESTAMP ORDER BY e.eventDate ASC",
                    Event.class);
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
        // This is a simplified example. In a real application, you would need to check
        // against registrations or bookings to determine available capacity.
        return executeQuery(
                "SELECT e FROM Event e WHERE e.capacity > 0");
    }

    /**
     * Search for events based on multiple criteria.
     * Uses the JPA Criteria API for dynamic query building.
     * 
     * @param name      Event name (partial match)
     * @param location  Event location (partial match)
     * @param type      Event type
     * @param startDate Minimum event date
     * @param endDate   Maximum event date
     * @return A list of events matching the specified criteria
     */
    public List<Event> searchEvents(String name, String location, EventType type,
            LocalDateTime startDate, LocalDateTime endDate) {
        return executeInTransaction(em -> {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Event> cq = cb.createQuery(Event.class);
            Root<Event> event = cq.from(Event.class);

            List<Predicate> predicates = new ArrayList<>();

            if (name != null && !name.isEmpty()) {
                predicates.add(cb.like(cb.lower(event.get("name")), "%" + name.toLowerCase() + "%"));
            }

            if (location != null && !location.isEmpty()) {
                predicates.add(cb.like(cb.lower(event.get("location")), "%" + location.toLowerCase() + "%"));
            }

            if (type != null) {
                predicates.add(cb.equal(event.get("type"), type));
            }

            if (startDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(event.get("eventDate"), startDate));
            }

            if (endDate != null) {
                predicates.add(cb.lessThanOrEqualTo(event.get("eventDate"), endDate));
            }

            cq.where(predicates.toArray(new Predicate[0]));
            cq.orderBy(cb.asc(event.get("eventDate")));

            return em.createQuery(cq).getResultList();
        });
    }
}