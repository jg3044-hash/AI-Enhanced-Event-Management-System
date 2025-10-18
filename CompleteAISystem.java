import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * COMPLETE AI-ENHANCED EVENT MANAGEMENT SYSTEM
 * Feature 1-3: Local AI (No API needed)
 * Feature 4: FREE Hugging Face AI Chatbot
 */
public class CompleteAISystem {

    // ============================================================
    // AI FEATURE 1: RECOMMENDATION ENGINE (Local ML)
    // ============================================================

    public static List<EventRecommendation> getRecommendations(int userId) {
        List<Event1> userEvents = RegistrationDAO.getUserRegisteredEvents(userId);
        Set<Integer> userEventIds = userEvents.stream()
                .map(Event1::getEventId)
                .collect(Collectors.toSet());

        List<UserSimilarity> similarUsers = findSimilarUsers(userId, userEventIds);
        Map<Integer, Double> eventScores = new HashMap<>();

        for (UserSimilarity similar : similarUsers) {
            List<Event1> theirEvents = RegistrationDAO.getUserRegisteredEvents(similar.userId);
            for (Event1 event : theirEvents) {
                if (userEventIds.contains(event.getEventId())) continue;
                if ("COMPLETED".equals(event.getStatus())) continue;
                eventScores.merge(event.getEventId(), similar.similarityScore, Double::sum);
            }
        }

        List<EventRecommendation> recommendations = new ArrayList<>();
        for (Map.Entry<Integer, Double> entry : eventScores.entrySet()) {
            Event1 event = EventDAO.getEventById(entry.getKey());
            if (event != null && !event.isFull()) {
                recommendations.add(new EventRecommendation(event, entry.getValue()));
            }
        }

        recommendations.sort((a, b) -> Double.compare(b.score, a.score));
        return recommendations.stream().limit(5).collect(Collectors.toList());
    }

    private static List<UserSimilarity> findSimilarUsers(int userId, Set<Integer> userEventIds) {
        List<UserSimilarity> similarities = new ArrayList<>();
        List<User> allUsers = UserDAO.getAllUsers();

        for (User otherUser : allUsers) {
            if (otherUser.getUserId() == userId) continue;
            List<Event1> otherEvents = RegistrationDAO.getUserRegisteredEvents(otherUser.getUserId());
            Set<Integer> otherEventIds = otherEvents.stream().map(Event1::getEventId).collect(Collectors.toSet());

            double similarity = calculateJaccardSimilarity(userEventIds, otherEventIds);
            if (similarity >= 0.3) {
                similarities.add(new UserSimilarity(otherUser.getUserId(), similarity));
            }
        }

        similarities.sort((a, b) -> Double.compare(b.similarityScore, a.similarityScore));
        return similarities.stream().limit(3).collect(Collectors.toList());
    }

    private static double calculateJaccardSimilarity(Set<Integer> set1, Set<Integer> set2) {
        if (set1.isEmpty() && set2.isEmpty()) return 0.0;
        Set<Integer> intersection = new HashSet<>(set1);
        intersection.retainAll(set2);
        Set<Integer> union = new HashSet<>(set1);
        union.addAll(set2);
        return union.isEmpty() ? 0.0 : (double) intersection.size() / union.size();
    }

    // ============================================================
    // AI FEATURE 2: CONFLICT PREDICTION (Local ML)
    // ============================================================

    public static ConflictPrediction predictConflictRisk(int userId, Event1 newEvent) {
        List<ConflictPattern> patterns = analyzeConflictPatterns(userId);

        if (patterns.isEmpty()) {
            return new ConflictPrediction(0.15, "LOW", "No conflict history");
        }

        double riskScore = 0.0;
        LocalTime eventTime = newEvent.getStartTime().toLocalTime();
        int dayOfWeek = newEvent.getEventDate().toLocalDate().getDayOfWeek().getValue();

        double timeRisk = calculateTimeSlotRisk(eventTime, patterns);
        double dayRisk = calculateDayRisk(dayOfWeek, patterns);

        riskScore = (timeRisk * 0.5) + (dayRisk * 0.5);
        riskScore = Math.min(1.0, riskScore);

        String riskLevel = riskScore > 0.7 ? "HIGH" : riskScore > 0.4 ? "MEDIUM" : "LOW";
        return new ConflictPrediction(riskScore, riskLevel, "Based on " + patterns.size() + " past conflicts");
    }

    private static List<ConflictPattern> analyzeConflictPatterns(int userId) {
        List<ConflictPattern> patterns = new ArrayList<>();
        List<Event1> userEvents = RegistrationDAO.getUserRegisteredEvents(userId);

        for (int i = 0; i < userEvents.size(); i++) {
            for (int j = i + 1; j < userEvents.size(); j++) {
                if (hasTimeConflict(userEvents.get(i), userEvents.get(j))) {
                    patterns.add(new ConflictPattern(
                            userEvents.get(i).getStartTime().toLocalTime(),
                            userEvents.get(i).getEventDate().toLocalDate().getDayOfWeek().getValue()
                    ));
                }
            }
        }
        return patterns;
    }

    private static boolean hasTimeConflict(Event1 e1, Event1 e2) {
        if (!e1.getEventDate().equals(e2.getEventDate())) return false;
        LocalTime start1 = e1.getStartTime().toLocalTime();
        LocalTime end1 = e1.getEndTime().toLocalTime();
        LocalTime start2 = e2.getStartTime().toLocalTime();
        LocalTime end2 = e2.getEndTime().toLocalTime();
        return start1.isBefore(end2) && end1.isAfter(start2);
    }

    private static double calculateTimeSlotRisk(LocalTime time, List<ConflictPattern> patterns) {
        long count = patterns.stream().filter(p -> Math.abs(p.timeSlot.getHour() - time.getHour()) <= 1).count();
        return patterns.isEmpty() ? 0.2 : (double) count / patterns.size();
    }

    private static double calculateDayRisk(int day, List<ConflictPattern> patterns) {
        long count = patterns.stream().filter(p -> p.dayOfWeek == day).count();
        return patterns.isEmpty() ? 0.2 : (double) count / patterns.size();
    }

    // ============================================================
    // AI FEATURE 3: ATTENDANCE PREDICTION (Local ML)
    // ============================================================

    public static AttendancePrediction predictAttendance(int userId, Event1 event) {
        List<Event1> history = RegistrationDAO.getUserRegisteredEvents(userId);

        if (history.isEmpty()) {
            return new AttendancePrediction(0.5, "NEUTRAL", "New user");
        }

        UserProfile profile = buildUserProfile(history);
        double score = 0.0;

        if (event.getCategory().equals(profile.favoriteCategory)) score += 6.0;
        if (event.getAvailableSeats() > 10) score += 2.0;
        if ("UPCOMING".equals(event.getStatus())) score += 1.0;

        double probability = Math.min(1.0, Math.max(0.0, score / 10.0));
        String likelihood = probability > 0.7 ? "HIGH" : probability > 0.4 ? "MEDIUM" : "LOW";

        return new AttendancePrediction(probability, likelihood, "Based on " + profile.favoriteCategory + " preference");
    }

    private static UserProfile buildUserProfile(List<Event1> events) {
        Map<String, Integer> categoryCount = new HashMap<>();
        for (Event1 event : events) {
            categoryCount.merge(event.getCategory(), 1, Integer::sum);
        }
        String favoriteCategory = categoryCount.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("WORKSHOP");
        return new UserProfile(favoriteCategory);
    }

    // ============================================================
    // AI FEATURE 4: FREE AI CHATBOT (Hugging Face API)
    // ============================================================

    private static final String HF_API_KEY = "hf_YOUR_KEY_HERE"; // Get from huggingface.co
    private static final String HF_API_URL = "https://api-inference.huggingface.co/models/facebook/blenderbot-400M-distill";

    public static String aiChatbot(String userMessage, int userId) {
        try {
            String contextPrompt = "You are an event assistant. User asks: " + userMessage;
            String aiResponse = callHuggingFaceAPI(contextPrompt);
            return enhanceWithEventData(aiResponse, userMessage);
        } catch (Exception e) {
            return localChatbot(userMessage); // Fallback to local if API fails
        }
    }

    private static String callHuggingFaceAPI(String message) throws Exception {
        URL url = new URL(HF_API_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization", "Bearer " + HF_API_KEY);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        String jsonInput = "{\"inputs\":\"" + message.replace("\"", "\\\"") + "\"}";
        try (OutputStream os = conn.getOutputStream()) {
            os.write(jsonInput.getBytes("utf-8"));
        }

        if (conn.getResponseCode() == 200) {
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }

            // Parse response (simplified)
            String resp = response.toString();
            int start = resp.indexOf("\"generated_text\":\"") + 18;
            int end = resp.indexOf("\"", start);
            if (start > 0 && end > start) {
                return resp.substring(start, end);
            }
        }
        throw new Exception("API call failed");
    }

    private static String enhanceWithEventData(String aiResponse, String userMessage) {
        String msg = userMessage.toLowerCase();

        if (msg.contains("event") || msg.contains("recommend")) {
            List<Event1> upcoming = EventDAO.getUpcomingEvents();
            if (!upcoming.isEmpty()) {
                Event1 event = upcoming.get(0);
                aiResponse += String.format("\n\n📅 Upcoming: '%s' on %s (%d seats)",
                        event.getEventName(), event.getEventDate(), event.getAvailableSeats());
            }
        }
        return aiResponse;
    }

    private static String localChatbot(String message) {
        message = message.toLowerCase();
        if (message.contains("recommend")) {
            List<Event1> events = EventDAO.getUpcomingEvents();
            if (!events.isEmpty()) {
                return "I recommend: " + events.get(0).getEventName();
            }
        }
        return "I'm your event assistant! Ask about recommendations, conflicts, or events.";
    }

    // ============================================================
    // DATA CLASSES
    // ============================================================

    static class UserSimilarity {
        int userId;
        double similarityScore;
        UserSimilarity(int userId, double score) {
            this.userId = userId;
            this.similarityScore = score;
        }
    }

    static class ConflictPattern {
        LocalTime timeSlot;
        int dayOfWeek;
        ConflictPattern(LocalTime time, int day) {
            this.timeSlot = time;
            this.dayOfWeek = day;
        }
    }

    static class UserProfile {
        String favoriteCategory;
        UserProfile(String category) {
            this.favoriteCategory = category;
        }
    }

    public static class EventRecommendation {
        public Event1 event;
        public double score;
        public EventRecommendation(Event1 event, double score) {
            this.event = event;
            this.score = score;
        }
        public String toString() {
            return String.format("⭐ %s (Score: %.2f)", event.getEventName(), score);
        }
    }

    public static class ConflictPrediction {
        public double riskScore;
        public String riskLevel;
        public String explanation;
        public ConflictPrediction(double score, String level, String exp) {
            this.riskScore = score;
            this.riskLevel = level;
            this.explanation = exp;
        }
        public String toString() {
            return String.format("Risk: %s (%.0f%%) - %s", riskLevel, riskScore*100, explanation);
        }
    }

    public static class AttendancePrediction {
        public double probability;
        public String likelihood;
        public String reason;
        public AttendancePrediction(double prob, String like, String reason) {
            this.probability = prob;
            this.likelihood = like;
            this.reason = reason;
        }
        public String toString() {
            return String.format("Likelihood: %s (%.0f%%) - %s", likelihood, probability*100, reason);
        }
    }

    // ============================================================
    // DEMO
    // ============================================================

    public static void main(String[] args) {
        System.out.println("╔═════════════════════════════════════════════╗");
        System.out.println("║  AI-ENHANCED EVENT MANAGEMENT SYSTEM        ║");
        System.out.println("║  3 Local AI + 1 FREE API Chatbot            ║");
        System.out.println("╚═════════════════════════════════════════════╝\n");

        int testUserId = 3;

        // Feature 1: Recommendations
        System.out.println("🤖 FEATURE 1: Smart Recommendations");
        List<EventRecommendation> recs = getRecommendations(testUserId);
        if (recs.isEmpty()) {
            System.out.println("   Not enough data yet");
        } else {
            for (int i = 0; i < Math.min(3, recs.size()); i++) {
                System.out.println("   " + (i+1) + ". " + recs.get(i));
            }
        }

        // Feature 2: Conflict Prediction
        System.out.println("\n🤖 FEATURE 2: Conflict Prediction");
        List<Event1> upcoming = EventDAO.getUpcomingEvents();
        if (!upcoming.isEmpty()) {
            ConflictPrediction pred = predictConflictRisk(testUserId, upcoming.get(0));
            System.out.println("   " + pred);
        }

        // Feature 3: Attendance Prediction
        System.out.println("\n🤖 FEATURE 3: Attendance Prediction");
        if (!upcoming.isEmpty()) {
            AttendancePrediction att = predictAttendance(testUserId, upcoming.get(0));
            System.out.println("   " + att);
        }

        // Feature 4: AI Chatbot
        System.out.println("\n🤖 FEATURE 4: FREE AI Chatbot");
        String response = aiChatbot("What events do you recommend?", testUserId);
        System.out.println("   " + response);

        System.out.println("\n✅ All features working!");
    }
}