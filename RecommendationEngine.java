import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A simple, rule-based AI for event recommendations (Content-Based Filtering).
 * This version is perfect for the project report.
 */
public class RecommendationEngine {

    /**
     * Recommends events by matching the user's profile interests with the event's category.
     *
     * @param user The user for whom to generate recommendations.
     * @param allUpcomingEvents A list of all available events.
     * @param limit The maximum number of recommendations to return.
     * @return A list of recommended Event1 objects.
     */
    public static List<Event1> getSimpleRecommendations(User user, List<Event1> allUpcomingEvents, int limit) {
        if (user == null || user.getInterests() == null || allUpcomingEvents == null) {
            return new ArrayList<>(); // Return an empty list if there's no data
        }

        // 1. Get the user's interests from their profile (e.g., "Technology,Business")
        String[] userInterests = user.getInterests().toLowerCase().split(",");

        // 2. Get IDs of events the user is already registered for (so we don't recommend them again)
        List<Event1> registeredEvents = RegistrationDAO.getUserRegisteredEvents(user.getUserId());
        List<Integer> registeredEventIds = registeredEvents.stream()
                .map(Event1::getEventId)
                .collect(Collectors.toList());

        // 3. Find events that match the user's interests
        List<Event1> recommendations = new ArrayList<>();
        for (Event1 event : allUpcomingEvents) {
            boolean matchesInterest = false;
            for (String interest : userInterests) {
                if (event.getCategory().equalsIgnoreCase(interest.trim())) {
                    matchesInterest = true;
                    break;
                }
            }

            // Add to the list if it matches an interest AND the user isn't already registered
            if (matchesInterest && !registeredEventIds.contains(event.getEventId())) {
                recommendations.add(event);
            }
        }

        // 4. Return the final list, limited to the desired number.
        return recommendations.stream().limit(limit).collect(Collectors.toList());
    }

    // Your other methods can remain for other parts of the app
    public static List<Event1> getTrendingEvents(int limit) { /* ... */ return new ArrayList<>(); }
    public static List<Event1> getSimilarEvents(Event1 target, List<Event1> all, int limit) { /* ... */ return new ArrayList<>(); }
}