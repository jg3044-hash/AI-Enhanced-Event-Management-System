import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

/**
 * Intelligent Conflict Detection System
 * Detects scheduling conflicts and provides smart suggestions
 */
public class ConflictDetector {

    /**
     * Check if registering for an event would create conflicts
     */
    public static ConflictResult checkConflicts(int userId, Event1 newEvent) {
        List<Event1> userEvents = RegistrationDAO.getUserRegisteredEvents(userId);
        List<Event1> conflicts = new ArrayList<>();

        for (Event1 existingEvent : userEvents) {
            if (hasTimeConflict(newEvent, existingEvent)) {
                conflicts.add(existingEvent);
            }
        }

        return new ConflictResult(conflicts.isEmpty(), conflicts,
                generateSuggestions(newEvent, conflicts));
    }

    /**
     * Check if two events have time conflict
     */
    private static boolean hasTimeConflict(Event1 event1, Event1 event2) {
        // Different dates = no conflict
        if (!event1.getEventDate().equals(event2.getEventDate())) {
            return false;
        }

        // Same date - check time overlap
        LocalTime start1 = event1.getStartTime().toLocalTime();
        LocalTime end1 = event1.getEndTime().toLocalTime();
        LocalTime start2 = event2.getStartTime().toLocalTime();
        LocalTime end2 = event2.getEndTime().toLocalTime();

        // Check if times overlap
        return start1.isBefore(end2) && end1.isAfter(start2);
    }

    /**
     * Generate smart suggestions to resolve conflicts
     */
    private static List<String> generateSuggestions(Event1 newEvent, List<Event1> conflicts) {
        List<String> suggestions = new ArrayList<>();

        if (conflicts.isEmpty()) {
            return suggestions;
        }

        // Suggestion 1: Cancel conflicting events
        suggestions.add("Consider canceling registration for: " +
                conflicts.get(0).getEventName());

        // Suggestion 2: Look for similar events at different times
        List<Event1> alternatives = findAlternativeEvents(newEvent);
        if (!alternatives.isEmpty()) {
            suggestions.add("Alternative: '" + alternatives.get(0).getEventName() +
                    "' at " + alternatives.get(0).getEventDate() + " " +
                    alternatives.get(0).getStartTime());
        }

        // Suggestion 3: Prioritize by category
        if (conflicts.size() > 1) {
            suggestions.add("Priority tip: Consider which event category is more important to you");
        }

        return suggestions;
    }

    /**
     * Find alternative events (same category, different time)
     */
    private static List<Event1> findAlternativeEvents(Event1 targetEvent) {
        List<Event1> allEvents = EventDAO.getEventsByCategory(targetEvent.getCategory());
        List<Event1> alternatives = new ArrayList<>();

        for (Event1 event : allEvents) {
            // Skip the target event itself
            if (event.getEventId() == targetEvent.getEventId()) {
                continue;
            }

            // Skip full events
            if (event.isFull()) {
                continue;
            }

            // Skip past events
            if ("COMPLETED".equals(event.getStatus()) || "CANCELLED".equals(event.getStatus())) {
                continue;
            }

            // Different date/time
            if (!event.getEventDate().equals(targetEvent.getEventDate()) ||
                    !event.getStartTime().equals(targetEvent.getStartTime())) {
                alternatives.add(event);
            }
        }

        return alternatives;
    }

    /**
     * Get all conflicts for a user's schedule
     */
    public static List<ConflictPair> getAllUserConflicts(int userId) {
        List<Event1> userEvents = RegistrationDAO.getUserRegisteredEvents(userId);
        List<ConflictPair> conflictPairs = new ArrayList<>();

        for (int i = 0; i < userEvents.size(); i++) {
            for (int j = i + 1; j < userEvents.size(); j++) {
                Event1 event1 = userEvents.get(i);
                Event1 event2 = userEvents.get(j);

                if (hasTimeConflict(event1, event2)) {
                    conflictPairs.add(new ConflictPair(event1, event2));
                }
            }
        }

        return conflictPairs;
    }

    /**
     * Check if user's schedule is optimal (no conflicts, good distribution)
     */
    public static ScheduleQuality analyzeScheduleQuality(int userId) {
        List<Event1> userEvents = RegistrationDAO.getUserRegisteredEvents(userId);

        if (userEvents.isEmpty()) {
            return new ScheduleQuality(100, "No events registered", new ArrayList<>());
        }

        List<String> issues = new ArrayList<>();
        int qualityScore = 100;

        // Check for conflicts
        List<ConflictPair> conflicts = getAllUserConflicts(userId);
        if (!conflicts.isEmpty()) {
            qualityScore -= 30 * conflicts.size();
            issues.add("Found " + conflicts.size() + " scheduling conflict(s)");
        }

        // Check for overloaded days
        Map<Date, Integer> eventsPerDay = new HashMap<>();
        for (Event1 event : userEvents) {
            eventsPerDay.put(event.getEventDate(),
                    eventsPerDay.getOrDefault(event.getEventDate(), 0) + 1);
        }

        for (Map.Entry<Date, Integer> entry : eventsPerDay.entrySet()) {
            if (entry.getValue() > 3) {
                qualityScore -= 10;
                issues.add("Overloaded day: " + entry.getKey() + " (" + entry.getValue() + " events)");
            }
        }

        // Check for category diversity
        Set<String> categories = new HashSet<>();
        for (Event1 event : userEvents) {
            categories.add(event.getCategory());
        }

        if (categories.size() == 1 && userEvents.size() > 3) {
            qualityScore -= 5;
            issues.add("Low diversity: All events from same category");
        }

        qualityScore = Math.max(0, qualityScore);

        String rating;
        if (qualityScore >= 90) {
            rating = "Excellent";
        } else if (qualityScore >= 70) {
            rating = "Good";
        } else if (qualityScore >= 50) {
            rating = "Fair";
        } else {
            rating = "Needs Improvement";
        }

        return new ScheduleQuality(qualityScore, rating, issues);
    }

    /**
     * Conflict detection result
     */
    public static class ConflictResult {
        private boolean canRegister;
        private List<Event1> conflictingEvents;
        private List<String> suggestions;

        public ConflictResult(boolean canRegister, List<Event1> conflictingEvents, List<String> suggestions) {
            this.canRegister = canRegister;
            this.conflictingEvents = conflictingEvents;
            this.suggestions = suggestions;
        }

        public boolean canRegister() {
            return canRegister;
        }

        public List<Event1> getConflictingEvents() {
            return conflictingEvents;
        }

        public List<String> getSuggestions() {
            return suggestions;
        }

        public String getMessage() {
            if (canRegister) {
                return "No conflicts detected. Safe to register!";
            } else {
                return "Warning: This event conflicts with " + conflictingEvents.size() +
                        " event(s) you're already registered for.";
            }
        }
    }

    /**
     * Conflict pair (two conflicting events)
     */
    public static class ConflictPair {
        private Event1 event1;
        private Event1 event2;

        public ConflictPair(Event1 event1, Event1 event2) {
            this.event1 = event1;
            this.event2 = event2;
        }

        public Event1 getEvent1() {
            return event1;
        }

        public Event1 getEvent2() {
            return event2;
        }

        @Override
        public String toString() {
            return event1.getEventName() + " conflicts with " + event2.getEventName();
        }
    }

    /**
     * Schedule quality assessment
     */
    public static class ScheduleQuality {
        private int score;
        private String rating;
        private List<String> issues;

        public ScheduleQuality(int score, String rating, List<String> issues) {
            this.score = score;
            this.rating = rating;
            this.issues = issues;
        }

        public int getScore() {
            return score;
        }

        public String getRating() {
            return rating;
        }

        public List<String> getIssues() {
            return issues;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Schedule Quality: ").append(score).append("/100 (").append(rating).append(")\n");
            if (!issues.isEmpty()) {
                sb.append("Issues:\n");
                for (String issue : issues) {
                    sb.append("- ").append(issue).append("\n");
                }
            }
            return sb.toString();
        }
    }

    /**
     * Test conflict detection
     */
    public static void main(String[] args) {
        System.out.println("=== Conflict Detection Test ===\n");

        // Test with user ID 3
        User testUser = UserDAO.getUserById(3);
        if (testUser != null) {
            System.out.println("Analyzing schedule for: " + testUser.getFullName());

            ScheduleQuality quality = analyzeScheduleQuality(testUser.getUserId());
            System.out.println(quality);

            List<ConflictPair> conflicts = getAllUserConflicts(testUser.getUserId());
            if (!conflicts.isEmpty()) {
                System.out.println("\nDetected Conflicts:");
                for (ConflictPair pair : conflicts) {
                    System.out.println("- " + pair);
                }
            }
        }
    }
}