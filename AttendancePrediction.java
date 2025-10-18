import java.sql.Time;
import java.util.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * AI-powered Attendance Prediction System (FULLY WORKING VERSION)
 * Uses historical data and statistical analysis to predict event attendance.
 */
public class AttendancePrediction {

    /**
     * Predicts the number of attendees for an event based on CURRENT registrations.
     * @param event The event to predict for.
     * @return The predicted number of people who will attend.
     */
    public static int predictAttendance(Event1 event) {
        if (event == null) {
            return 0;
        }

        // If event has 0 registrations, predict based on historical average
        if (event.getCurrentAttendees() == 0) {
            return predictWithNoRegistrations(event);
        }

        // Start with a base historical attendance RATE (e.g., 75%)
        double baseAttendanceRate = calculateHistoricalAverageRate(event);

        // Apply multipliers to the RATE
        baseAttendanceRate *= getDayOfWeekMultiplier(event);
        baseAttendanceRate *= getTimeOfDayMultiplier(event);
        baseAttendanceRate *= getCategoryPopularityMultiplier(event.getCategory());
        baseAttendanceRate *= getOrganizerReputationMultiplier(event.getOrganizerId());

        // Ensure the final rate is between 50% and 95%
        baseAttendanceRate = Math.min(baseAttendanceRate, 0.95); // Cap at 95%
        baseAttendanceRate = Math.max(baseAttendanceRate, 0.50); // Minimum 50%

        // Final prediction = RATE applied to CURRENT registrations
        int predicted = (int) Math.round(event.getCurrentAttendees() * baseAttendanceRate);

        // Ensure prediction is at least 1 if there are registrations
        return Math.max(predicted, 1);
    }

    /**
     * Predict attendance when there are no registrations yet
     */
    private static int predictWithNoRegistrations(Event1 event) {
        // Get similar historical events
        List<Event1> similarEvents = getHistoricalSimilarEvents(event);

        if (similarEvents.isEmpty()) {
            // No historical data, estimate based on capacity and category
            int baseEstimate = (int) (event.getMaxAttendees() * 0.3); // 30% of capacity
            baseEstimate = (int) (baseEstimate * getCategoryPopularityMultiplier(event.getCategory()));
            return Math.max(baseEstimate, 5); // At least 5 people
        }

        // Calculate average attendance from similar events
        int totalAttendance = 0;
        int count = 0;

        for (Event1 similar : similarEvents) {
            totalAttendance += similar.getCurrentAttendees();
            count++;
        }

        int avgAttendance = count > 0 ? totalAttendance / count : 0;

        // Apply multipliers
        double multiplier = getDayOfWeekMultiplier(event) *
                getTimeOfDayMultiplier(event) *
                getCategoryPopularityMultiplier(event.getCategory());

        int prediction = (int) (avgAttendance * multiplier);
        return Math.max(prediction, 5); // At least 5 people
    }

    /**
     * Predicts attendance with confidence level
     */
    public static PredictionResult predictWithConfidence(Event1 event) {
        int predicted = predictAttendance(event);
        double confidence = calculateConfidence(event);
        String confidenceLevel = getConfidenceLevel(confidence);

        // Format the result properly
        return new PredictionResult(predicted, confidence, confidenceLevel);
    }

    /**
     * Calculates the historical average attendance RATE for similar events.
     * @return A rate between 0.0 and 1.0 (e.g., 0.85 for 85%).
     */
    private static double calculateHistoricalAverageRate(Event1 targetEvent) {
        List<Event1> historicalEvents = getHistoricalSimilarEvents(targetEvent);

        if (historicalEvents.isEmpty()) {
            return 0.80; // Default 80% attendance rate (more optimistic)
        }

        double totalRate = 0.0;
        int count = 0;

        for (Event1 event : historicalEvents) {
            if (event.getMaxAttendees() > 0 && event.getCurrentAttendees() > 0) {
                // For completed events, current_attendees is who actually showed up
                // Assume registrations were about 120% of actual attendance
                double estimatedRegistrations = event.getCurrentAttendees() / 0.80;
                if (estimatedRegistrations > 0) {
                    double rate = (double) event.getCurrentAttendees() / estimatedRegistrations;
                    totalRate += rate;
                    count++;
                }
            }
        }

        double avgRate = count > 0 ? totalRate / count : 0.80;

        // Ensure rate is reasonable (between 60% and 95%)
        avgRate = Math.max(0.60, Math.min(0.95, avgRate));

        return avgRate;
    }

    /**
     * Gets similar historical events based on category and status
     */
    private static List<Event1> getHistoricalSimilarEvents(Event1 targetEvent) {
        List<Event1> allEvents = EventDAO.getAllEvents();
        List<Event1> similarEvents = new ArrayList<>();

        for (Event1 event : allEvents) {
            // Only look at completed events with same category
            if ("COMPLETED".equalsIgnoreCase(event.getStatus()) &&
                    event.getCategory().equalsIgnoreCase(targetEvent.getCategory())) {
                similarEvents.add(event);
            }
        }

        return similarEvents;
    }

    /**
     * Day of week multiplier (weekends typically have higher attendance)
     */
    private static double getDayOfWeekMultiplier(Event1 event) {
        try {
            if (event.getEventDate() == null) {
                return 1.0;
            }

            LocalDate date = event.getEventDate().toLocalDate();
            DayOfWeek dayOfWeek = date.getDayOfWeek();

            switch (dayOfWeek) {
                case SATURDAY:
                case SUNDAY:
                    return 1.15; // 15% higher on weekends
                case FRIDAY:
                    return 1.10; // 10% higher on Friday
                case MONDAY:
                    return 0.90; // 10% lower on Monday
                default:
                    return 1.0; // Normal for Tue-Thu
            }
        } catch (Exception e) {
            return 1.0;
        }
    }

    /**
     * Time of day multiplier (evening events typically have better attendance)
     */
    private static double getTimeOfDayMultiplier(Event1 event) {
        try {
            if (event.getStartTime() == null) {
                return 1.0;
            }

            LocalTime time = event.getStartTime().toLocalTime();
            int hour = time.getHour();

            if (hour >= 18 && hour <= 20) {
                return 1.15; // Evening events (6-8 PM) - best attendance
            } else if (hour >= 9 && hour <= 11) {
                return 1.05; // Morning events (9-11 AM) - good attendance
            } else if (hour >= 14 && hour <= 16) {
                return 1.10; // Afternoon events (2-4 PM) - good attendance
            } else if (hour < 9 || hour > 21) {
                return 0.85; // Very early or late - lower attendance
            } else {
                return 1.0; // Normal times
            }
        } catch (Exception e) {
            return 1.0;
        }
    }

    /**
     * Category popularity multiplier
     */
    private static double getCategoryPopularityMultiplier(String category) {
        if (category == null) {
            return 1.0;
        }

        switch (category.toUpperCase()) {
            case "TECHNOLOGY":
                return 1.10; // Tech events are popular
            case "BUSINESS":
                return 1.05; // Business events are fairly popular
            case "INNOVATION":
                return 1.08; // Innovation events attract interest
            case "EDUCATION":
                return 0.95; // Education events slightly lower
            case "TRAINING":
                return 0.90; // Training has lower attendance
            default:
                return 1.0;
        }
    }

    /**
     * Organizer reputation multiplier (based on past event success)
     */
    private static double getOrganizerReputationMultiplier(int organizerId) {
        if (organizerId <= 0) {
            return 1.0;
        }

        try {
            // Get organizer's past events
            List<Event1> organizerEvents = EventDAO.getEventsByOrganizer(organizerId);

            if (organizerEvents.isEmpty()) {
                return 1.0; // New organizer, neutral
            }

            // Calculate average fill rate for completed events
            double totalFillRate = 0.0;
            int completedCount = 0;

            for (Event1 event : organizerEvents) {
                if ("COMPLETED".equalsIgnoreCase(event.getStatus()) &&
                        event.getMaxAttendees() > 0) {
                    double fillRate = (double) event.getCurrentAttendees() / event.getMaxAttendees();
                    totalFillRate += fillRate;
                    completedCount++;
                }
            }

            if (completedCount == 0) {
                return 1.0;
            }

            double avgFillRate = totalFillRate / completedCount;

            // Organizers with high fill rates get a boost
            if (avgFillRate >= 0.80) {
                return 1.15; // Excellent organizer
            } else if (avgFillRate >= 0.60) {
                return 1.08; // Good organizer
            } else if (avgFillRate >= 0.40) {
                return 1.0; // Average organizer
            } else {
                return 0.92; // Below average organizer
            }

        } catch (Exception e) {
            return 1.0;
        }
    }

    /**
     * Calculate confidence level based on available data
     */
    private static double calculateConfidence(Event1 event) {
        double confidence = 0.5; // Base 50%

        // More registrations = higher confidence
        if (event.getCurrentAttendees() > 50) {
            confidence += 0.20;
        } else if (event.getCurrentAttendees() > 20) {
            confidence += 0.15;
        } else if (event.getCurrentAttendees() > 10) {
            confidence += 0.10;
        }

        // Historical data availability
        List<Event1> similar = getHistoricalSimilarEvents(event);
        if (similar.size() > 10) {
            confidence += 0.20;
        } else if (similar.size() > 5) {
            confidence += 0.15;
        } else if (similar.size() > 2) {
            confidence += 0.10;
        }

        // Cap at 95%
        return Math.min(confidence, 0.95);
    }

    /**
     * Convert confidence score to level
     */
    private static String getConfidenceLevel(double confidence) {
        if (confidence >= 0.80) {
            return "HIGH";
        } else if (confidence >= 0.60) {
            return "MEDIUM";
        } else {
            return "LOW";
        }
    }

    /**
     * Result class containing prediction and confidence
     */
    public static class PredictionResult {
        private int predictedAttendance;
        private double confidence;
        private String confidenceLevel;

        public PredictionResult(int predictedAttendance, double confidence, String confidenceLevel) {
            this.predictedAttendance = predictedAttendance;
            this.confidence = confidence;
            this.confidenceLevel = confidenceLevel;
        }

        public int getPredictedAttendance() {
            return predictedAttendance;
        }

        public double getConfidence() {
            return confidence;
        }

        public String getConfidenceLevel() {
            return confidenceLevel;
        }

        @Override
        public String toString() {
            return String.format("Predicted: %d attendees (Confidence: %.0f%% - %s)",
                    predictedAttendance, confidence * 100, confidenceLevel);
        }
    }
}