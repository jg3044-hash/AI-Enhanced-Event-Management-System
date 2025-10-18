import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * EventDAO handles all database operations related to events
 */
public class EventDAO {

    /**
     * Create a new event
     */
    public static boolean createEvent(Event1 event) {
        String query = "INSERT INTO events (event_name, description, category, organizer_id, venue, event_date, start_time, end_time, max_attendees, current_attendees, status, price) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, event.getEventName());
            pstmt.setString(2, event.getDescription());
            pstmt.setString(3, event.getCategory());
            pstmt.setInt(4, event.getOrganizerId());
            pstmt.setString(5, event.getVenue());
            pstmt.setDate(6, event.getEventDate());
            pstmt.setTime(7, event.getStartTime());
            pstmt.setTime(8, event.getEndTime());
            pstmt.setInt(9, event.getMaxAttendees());
            pstmt.setInt(10, 0);
            pstmt.setString(11, "UPCOMING");
            pstmt.setDouble(12, event.getPrice());  // ADD THIS LINE

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    event.setEventId(rs.getInt(1));
                }
                System.out.println("Event created successfully: " + event.getEventName());
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Error creating event: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }
    /**
     * Update an existing event
     */
    public static boolean updateEvent(Event1 event) {
        String query = "UPDATE events SET event_name = ?, description = ?, category = ?, venue = ?, event_date = ?, start_time = ?, end_time = ?, max_attendees = ?, status = ?, price = ? WHERE event_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, event.getEventName());
            pstmt.setString(2, event.getDescription());
            pstmt.setString(3, event.getCategory());
            pstmt.setString(4, event.getVenue());
            pstmt.setDate(5, event.getEventDate());
            pstmt.setTime(6, event.getStartTime());
            pstmt.setTime(7, event.getEndTime());
            pstmt.setInt(8, event.getMaxAttendees());
            pstmt.setString(9, event.getStatus());
            pstmt.setDouble(10, event.getPrice());  // ADD THIS LINE
            pstmt.setInt(11, event.getEventId());    // CHANGED FROM 10 TO 11

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Event updated successfully");
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Error updating event: " + e.getMessage());
        }

        return false;
    }

    /**
     * Delete an event
     */
    public static boolean deleteEvent(int eventId) {
        String query = "DELETE FROM events WHERE event_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, eventId);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Event deleted successfully");
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Error deleting event: " + e.getMessage());
        }

        return false;
    }

    /**
     * Get event by ID
     */
    public static Event1 getEventById(int eventId) {
        String query = "SELECT e.*, u.full_name as organizer_name FROM events e " +
                "LEFT JOIN users u ON e.organizer_id = u.user_id WHERE e.event_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, eventId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractEventFromResultSet(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error getting event: " + e.getMessage());
        }

        return null;
    }

    /**
     * Get all events
     */
    public static List<Event1> getAllEvents() {
        List<Event1> events = new ArrayList<>();
        String query = "SELECT e.*, u.full_name as organizer_name FROM events e " +
                "LEFT JOIN users u ON e.organizer_id = u.user_id ORDER BY e.event_date";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                events.add(extractEventFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error getting all events: " + e.getMessage());
        }

        return events;
    }

    /**
     * Get events by organizer
     */
    public static List<Event1> getEventsByOrganizer(int organizerId) {
        List<Event1> events = new ArrayList<>();
        String query = "SELECT e.*, u.full_name as organizer_name FROM events e " +
                "LEFT JOIN users u ON e.organizer_id = u.user_id WHERE e.organizer_id = ? ORDER BY e.event_date";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, organizerId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                events.add(extractEventFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error getting events by organizer: " + e.getMessage());
        }

        return events;
    }

    /**
     * Get events by category
     */
    public static List<Event1> getEventsByCategory(String category) {
        List<Event1> events = new ArrayList<>();
        String query = "SELECT e.*, u.full_name as organizer_name FROM events e " +
                "LEFT JOIN users u ON e.organizer_id = u.user_id WHERE e.category = ? ORDER BY e.event_date";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, category);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                events.add(extractEventFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error getting events by category: " + e.getMessage());
        }

        return events;
    }

    /**
     * Get upcoming events
     */
    public static List<Event1> getUpcomingEvents() {
        List<Event1> events = new ArrayList<>();
        String query = "SELECT e.*, u.full_name as organizer_name FROM events e " +
                "LEFT JOIN users u ON e.organizer_id = u.user_id " +
                "WHERE e.status = 'UPCOMING' AND e.event_date >= CURDATE() ORDER BY e.event_date";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                events.add(extractEventFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error getting upcoming events: " + e.getMessage());
        }

        return events;
    }

    /**
     * Search events by keyword
     */
    public static List<Event1> searchEvents(String keyword) {
        List<Event1> events = new ArrayList<>();
        String query = "SELECT e.*, u.full_name as organizer_name FROM events e " +
                "LEFT JOIN users u ON e.organizer_id = u.user_id " +
                "WHERE e.event_name LIKE ? OR e.description LIKE ? ORDER BY e.event_date";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            String searchPattern = "%" + keyword + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                events.add(extractEventFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error searching events: " + e.getMessage());
        }

        return events;
    }

    /**
     * Update current attendees count
     */
    public static boolean updateAttendeeCount(int eventId, int count) {
        String query = "UPDATE events SET current_attendees = ? WHERE event_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, count);
            pstmt.setInt(2, eventId);

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error updating attendee count: " + e.getMessage());
        }

        return false;
    }

    /**
     * Helper method to extract Event from ResultSet
     */
    private static Event1 extractEventFromResultSet(ResultSet rs) throws SQLException {
        Event1 event = new Event1();
        event.setEventId(rs.getInt("event_id"));
        event.setEventName(rs.getString("event_name"));
        event.setDescription(rs.getString("description"));
        event.setCategory(rs.getString("category"));
        event.setOrganizerId(rs.getInt("organizer_id"));
        event.setOrganizerName(rs.getString("organizer_name"));
        event.setVenue(rs.getString("venue"));
        event.setEventDate(rs.getDate("event_date"));
        event.setStartTime(rs.getTime("start_time"));
        event.setEndTime(rs.getTime("end_time"));
        event.setMaxAttendees(rs.getInt("max_attendees"));
        event.setCurrentAttendees(rs.getInt("current_attendees"));
        event.setStatus(rs.getString("status"));
        event.setPrice(rs.getDouble("price"));  // ADD THIS LINE
        return event;
    }
}