import java.sql.Date;
import java.sql.Time;

/**
 * Event class represents an event in the system
 * Stores all event-related information
 */
public class Event1 {

    private int eventId;
    private String eventName;
    private String description;
    private String category;
    private int organizerId;
    private String organizerName;
    private String venue;
    private Date eventDate;
    private Time startTime;
    private Time endTime;
    private int maxAttendees;
    private int currentAttendees;
    private String status;
    private double price;

    // Default constructor
    public Event1() {
    }

    // Constructor with all parameters
    public Event1(int eventId, String eventName, String description, String category,
                  int organizerId, String venue, Date eventDate, Time startTime,
                  Time endTime, int maxAttendees, int currentAttendees, String status, double price) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.description = description;
        this.category = category;
        this.organizerId = organizerId;
        this.venue = venue;
        this.eventDate = eventDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.maxAttendees = maxAttendees;
        this.currentAttendees = currentAttendees;
        this.status = status;
        this.price=price;
    }

    // Constructor without eventId (for creating new events)
    public Event1(String eventName, String description, String category,
                  int organizerId, String venue, Date eventDate, Time startTime,
                  Time endTime, int maxAttendees) {
        this.eventName = eventName;
        this.description = description;
        this.category = category;
        this.organizerId = organizerId;
        this.venue = venue;
        this.eventDate = eventDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.maxAttendees = maxAttendees;
        this.currentAttendees = 0;
        this.status = "UPCOMING";
    }

    // Getters and Setters
    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getOrganizerId() {
        return organizerId;
    }

    public void setOrganizerId(int organizerId) {
        this.organizerId = organizerId;
    }

    public String getOrganizerName() {
        return organizerName;
    }

    public void setOrganizerName(String organizerName) {
        this.organizerName = organizerName;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    public Time getStartTime() {
        return startTime;
    }

    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }

    public Time getEndTime() {
        return endTime;
    }

    public void setEndTime(Time endTime) {
        this.endTime = endTime;
    }

    public int getMaxAttendees() {
        return maxAttendees;
    }

    public void setMaxAttendees(int maxAttendees) {
        this.maxAttendees = maxAttendees;
    }

    public int getCurrentAttendees() {
        return currentAttendees;
    }

    public void setCurrentAttendees(int currentAttendees) {
        this.currentAttendees = currentAttendees;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    // Utility methods
    public int getAvailableSeats() {
        return maxAttendees - currentAttendees;
    }

    public boolean isFull() {
        return currentAttendees >= maxAttendees;
    }

    public double getOccupancyPercentage() {
        if (maxAttendees == 0) return 0;
        return (currentAttendees * 100.0) / maxAttendees;
    }

    // AI System compatibility methods
    public int getCapacity() {
        return maxAttendees;
    }

    public int getCurrentParticipants() {
        return currentAttendees;
    }


    @Override
    public String toString() {
        return "Event{" +
                "eventId=" + eventId +
                ", eventName='" + eventName + '\'' +
                ", category='" + category + '\'' +
                ", venue='" + venue + '\'' +
                ", eventDate=" + eventDate +
                ", availableSeats=" + getAvailableSeats() +
                '}';
    }
}