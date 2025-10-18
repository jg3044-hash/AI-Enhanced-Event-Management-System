import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * RegistrationDAO handles all database operations related to event registrations
 */
public class RegistrationDAO {

    /**
     * Register user for an event
     */
    public static boolean registerForEvent(int userId, int eventId) {
        // Check if already registered
        if (isAlreadyRegistered(userId, eventId)) {
            System.out.println("User already registered for this event");
            return false;
        }

        // Check if event is full
        Event1 event = EventDAO.getEventById(eventId);
        if (event != null && event.isFull()) {
            System.out.println("Event is full");
            return false;
        }

        String query = "INSERT INTO registrations (event_id, user_id, attendance_status) VALUES (?, ?, 'REGISTERED')";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, eventId);
            pstmt.setInt(2, userId);

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                // Update event attendee count
                if (event != null) {
                    int newCount = event.getCurrentAttendees() + 1;
                    EventDAO.updateAttendeeCount(eventId, newCount);
                }

                System.out.println("Registration successful");
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Error registering for event: " + e.getMessage());
        }

        return false;
    }

    /**
     * Cancel registration
     */
    public static boolean cancelRegistration(int userId, int eventId) {
        String query = "DELETE FROM registrations WHERE user_id = ? AND event_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, userId);
            pstmt.setInt(2, eventId);

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                // Update event attendee count
                Event1 event = EventDAO.getEventById(eventId);
                if (event != null) {
                    int newCount = Math.max(0, event.getCurrentAttendees() - 1);
                    EventDAO.updateAttendeeCount(eventId, newCount);
                }

                System.out.println("Registration cancelled");
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Error cancelling registration: " + e.getMessage());
        }

        return false;
    }

    /**
     * Check if user is already registered for event
     */
    public static boolean isAlreadyRegistered(int userId, int eventId) {
        String query = "SELECT COUNT(*) FROM registrations WHERE user_id = ? AND event_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, userId);
            pstmt.setInt(2, eventId);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            System.err.println("Error checking registration: " + e.getMessage());
        }

        return false;
    }

    /**
     * Get all registrations for a user
     */
    public static List<Registration> getUserRegistrations(int userId) {
        List<Registration> registrations = new ArrayList<>();
        String query = "SELECT r.*, e.event_name, u.full_name as user_name " +
                "FROM registrations r " +
                "JOIN events e ON r.event_id = e.event_id " +
                "JOIN users u ON r.user_id = u.user_id " +
                "WHERE r.user_id = ? ORDER BY r.registration_date DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Registration reg = new Registration();
                reg.setRegistrationId(rs.getInt("registration_id"));
                reg.setEventId(rs.getInt("event_id"));
                reg.setUserId(rs.getInt("user_id"));
                reg.setEventName(rs.getString("event_name"));
                reg.setUserName(rs.getString("user_name"));
                reg.setRegistrationDate(rs.getTimestamp("registration_date"));
                reg.setAttendanceStatus(rs.getString("attendance_status"));
                registrations.add(reg);
            }

        } catch (SQLException e) {
            System.err.println("Error getting user registrations: " + e.getMessage());
        }

        return registrations;
    }

    /**
     * Get all registrations for an event
     */
    public static List<Registration> getEventRegistrations(int eventId) {
        List<Registration> registrations = new ArrayList<>();
        String query = "SELECT r.*, e.event_name, u.full_name as user_name " +
                "FROM registrations r " +
                "JOIN events e ON r.event_id = e.event_id " +
                "JOIN users u ON r.user_id = u.user_id " +
                "WHERE r.event_id = ? ORDER BY r.registration_date";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, eventId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Registration reg = new Registration();
                reg.setRegistrationId(rs.getInt("registration_id"));
                reg.setEventId(rs.getInt("event_id"));
                reg.setUserId(rs.getInt("user_id"));
                reg.setEventName(rs.getString("event_name"));
                reg.setUserName(rs.getString("user_name"));
                reg.setRegistrationDate(rs.getTimestamp("registration_date"));
                reg.setAttendanceStatus(rs.getString("attendance_status"));
                registrations.add(reg);
            }

        } catch (SQLException e) {
            System.err.println("Error getting event registrations: " + e.getMessage());
        }

        return registrations;
    }

    /**
     * Gets the total count of all registrations in the system.
     * This is the CORRECT way to get the number for the Admin Dashboard.
     */
    public static int getTotalRegistrationCount() {
        String query = "SELECT COUNT(*) FROM registrations";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error getting total registration count: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Gets the REAL registration count for a single event.
     * This ignores the potentially incorrect 'current_attendees' column.
     */
    public static int getRegistrationCountForEvent(int eventId) {
        String query = "SELECT COUNT(*) FROM registrations WHERE event_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, eventId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Gets the total registration count for all events created by a specific organizer.
     * This is the correct method for the Organizer Dashboard.
     */
    public static int getRegistrationCountForOrganizer(int organizerId) {
        String query = "SELECT COUNT(*) FROM registrations r " +
                "JOIN events e ON r.event_id = e.event_id " +
                "WHERE e.organizer_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, organizerId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error getting registration count for organizer: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Mark attendance for a user
     */
    public static boolean markAttendance(int userId, int eventId) {
        String query = "UPDATE registrations SET attendance_status = 'ATTENDED' WHERE user_id = ? AND event_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, userId);
            pstmt.setInt(2, eventId);

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Attendance marked");
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Error marking attendance: " + e.getMessage());
        }

        return false;
    }

    /**
     * Get registered events for a user (returns Event objects)
     */
    public static List<Event1> getUserRegisteredEvents(int userId) {
        List<Event1> events = new ArrayList<>();
        String query = "SELECT e.*, u.full_name as organizer_name " +
                "FROM events e " +
                "JOIN registrations r ON e.event_id = r.event_id " +
                "LEFT JOIN users u ON e.organizer_id = u.user_id " +
                "WHERE r.user_id = ? ORDER BY e.event_date";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
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
                events.add(event);
            }

        } catch (SQLException e) {
            System.err.println("Error getting registered events: " + e.getMessage());
        }

        return events;
    }

    /**
     * Get total registration count for an event
     */
    public static int getRegistrationCount(int eventId) {
        String query = "SELECT COUNT(*) FROM registrations WHERE event_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, eventId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("Error getting registration count: " + e.getMessage());
        }

        return 0;
    }
}
