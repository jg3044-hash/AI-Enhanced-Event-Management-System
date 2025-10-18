import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A simple, reliable AI engine to predict attendance based on historical data.
 * This is a foolproof replacement for the buggy AttendancePrediction.java.
 */
public class AnalyticsEngine {

    /**
     * AI Logic: Learns the historical attendance rate from all past events.
     * @return The average attendance rate as a percentage (e.g., 85.0 for 85%).
     */
    public static double getHistoricalAttendanceRate() {
        int totalPastRegistrations = 0;
        int totalPastAttendees = 0;

        // This query finds all registrations for events that are marked as 'COMPLETED'
        String query = "SELECT r.attendance_status FROM registrations r " +
                "JOIN events e ON r.event_id = e.event_id " +
                "WHERE e.status = 'COMPLETED'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                totalPastRegistrations++; // Every row is a past registration
                if ("ATTENDED".equalsIgnoreCase(rs.getString("attendance_status"))) {
                    totalPastAttendees++; // This person showed up
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return 75.0; // On error, return a safe default of 75%
        }

        // Avoid division by zero if there's no historical data
        if (totalPastRegistrations == 0) {
            return 75.0; // If no history, assume a default 75% attendance rate.
        }

        // Calculate the percentage: (Attended / Registered) * 100
        return ((double) totalPastAttendees / totalPastRegistrations) * 100.0;
    }
}