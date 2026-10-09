package com.example.events.web;

import com.example.events.dao.EventDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.bson.Document;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Controller. Routes by the "action" parameter:
 *   GET  ?action=list|new|view|edit
 *   POST action=create|update|delete|register|unregister
 */
@WebServlet("/events")
public class EventServlet extends HttpServlet {
    private final EventDAO dao = new EventDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        if (action == null) action = "list";
        switch (action) {
            case "new" -> forward(req, resp, "form.jsp");
            case "view", "edit" -> {
                Document event = dao.findById(req.getParameter("id"));
                if (event == null) { resp.sendError(404, "Event not found"); return; }
                req.setAttribute("event", event);
                req.setAttribute("dateValue", new SimpleDateFormat("yyyy-MM-dd").format(event.getDate("date")));
                forward(req, resp, action.equals("view") ? "event.jsp" : "form.jsp");
            }
            default -> {
                req.setAttribute("events", dao.findAll(req.getParameter("q")));
                forward(req, resp, "list.jsp");
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        String action = req.getParameter("action");
        String id = req.getParameter("id");
        String base = req.getContextPath() + "/events";

        switch (action) {
            case "create" -> {
                dao.create(p(req, "title"), p(req, "venue"), date(req), cap(req), p(req, "description"));
                resp.sendRedirect(base);
            }
            case "update" -> {
                dao.update(id, p(req, "title"), p(req, "venue"), date(req), cap(req), p(req, "description"));
                resp.sendRedirect(base + "?action=view&id=" + id);
            }
            case "delete" -> {
                dao.delete(id);
                resp.sendRedirect(base);
            }
            case "register" -> {
                boolean ok = dao.register(id, p(req, "name"), p(req, "email"));
                String err = ok ? "" : "&error=" + URLEncoder.encode("Event full or email already registered", StandardCharsets.UTF_8);
                resp.sendRedirect(base + "?action=view&id=" + id + err);
            }
            case "unregister" -> {
                dao.unregister(id, p(req, "email"));
                resp.sendRedirect(base + "?action=view&id=" + id);
            }
            default -> resp.sendError(400, "Unknown action");
        }
    }

    private static String p(HttpServletRequest r, String n) { String v = r.getParameter(n); return v == null ? "" : v.trim(); }
    private static int cap(HttpServletRequest r) { return Integer.parseInt(p(r, "capacity")); }
    private static Date date(HttpServletRequest r) {
        try { return new SimpleDateFormat("yyyy-MM-dd").parse(p(r, "date")); }
        catch (ParseException e) { throw new IllegalArgumentException("Bad date"); }
    }
    private static void forward(HttpServletRequest req, HttpServletResponse resp, String jsp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/" + jsp).forward(req, resp);
    }
}
