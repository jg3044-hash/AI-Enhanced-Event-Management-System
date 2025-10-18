import java.sql.Timestamp;

/**
 * Registration class represents an event registration
 */
public class Registration {

    private int registrationId;
    private int eventId;
    private int userId;
    private String eventName;
    private String userName;
    private Timestamp registrationDate;
    private String attendanceStatus;

    public Registration() {
    }

    public Registration(int eventId, int userId) {
        this.eventId = eventId;
        this.userId = userId;
        this.attendanceStatus = "REGISTERED";
    }

    public int getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(int registrationId) {
        this.registrationId = registrationId;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Timestamp getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Timestamp registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getAttendanceStatus() {
        return attendanceStatus;
    }

    public void setAttendanceStatus(String attendanceStatus) {
        this.attendanceStatus = attendanceStatus;
    }

    @Override
    public String toString() {
        return "Registration{" +
                "registrationId=" + registrationId +
                ", eventName='" + eventName + '\'' +
                ", userName='" + userName + '\'' +
                ", status='" + attendanceStatus + '\'' +
                '}';
    }
}