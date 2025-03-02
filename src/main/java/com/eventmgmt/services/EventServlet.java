package com.eventmgmt.services;

import com.eventmgmt.model.Event;
import com.eventmgmt.model.EventType;
import com.eventmgmt.repository.EventRepository;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@WebServlet("/")
public class EventServlet extends HttpServlet {

    private EventRepository eventRepository;

    @Override
    public void init() throws ServletException {
        super.init();
        eventRepository = new EventRepository();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<Event> upcomingEvents = eventRepository.findUpcomingEvents(4);

        List<String> eventTypes = Arrays.stream(EventType.values())
                .map(Enum::name)
                .toList();

        List<String> popularLocations = getPopularLocations();

        LocalDate currentDate = LocalDate.now();
        LocalDate nextMonthDate = currentDate.plusMonths(1);

        request.setAttribute("upcomingEvents", upcomingEvents);
        request.setAttribute("eventTypes", eventTypes);
        request.setAttribute("popularLocations", popularLocations);
        request.setAttribute("currentDate", currentDate);
        request.setAttribute("nextMonthDate", nextMonthDate);

        RequestDispatcher dispatcher = request.getRequestDispatcher("/index.jsp");
        dispatcher.forward(request, response);
    }

    private List<String> getPopularLocations() {
        return List.of(
                "Blue Mountain",
                "City Central",
                "Green Forest",
                "Historic District",
                "Lakeside Park",
                "Ocean View");
    }
}