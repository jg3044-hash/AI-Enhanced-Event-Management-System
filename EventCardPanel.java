import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

public class EventCardPanel extends JPanel {
    private Event event;

    public EventCardPanel(Event event) {
        this.event = event;
        setOpaque(false); // Important for the custom shadow painting
        setLayout(new BorderLayout());
        // Set a maximum height to prevent cards from stretching weirdly
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 255));
        setBorder(new EmptyBorder(12, 12, 12, 12)); // This margin provides space FOR the shadow

        // The main container with rounded corners and a white background
        JPanel cardContent = new JPanel(new BorderLayout(0, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                // This custom painting creates the rounded corners
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 16, 16));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        cardContent.setBackground(Color.WHITE);
        cardContent.setOpaque(false); // Let the custom paint handle the background

        // Top colored strip with gradient effect
        JPanel topStrip = createTopStrip(event.getCategory());
        cardContent.add(topStrip, BorderLayout.NORTH);

        // All other content goes in here
        JPanel contentArea = new JPanel();
        contentArea.setLayout(new BoxLayout(contentArea, BoxLayout.Y_AXIS));
        contentArea.setOpaque(false);
        contentArea.setBorder(new EmptyBorder(15, 20, 15, 20));

        // Header, Info, and Bottom Bar
        contentArea.add(createHeaderPanel());
        contentArea.add(Box.createVerticalStrut(15));
        contentArea.add(createInfoPanel());
        contentArea.add(Box.createVerticalGlue()); // Pushes the bottom bar down
        contentArea.add(createBottomBar());

        cardContent.add(contentArea, BorderLayout.CENTER);
        add(cardContent, BorderLayout.CENTER);
    }

    // Custom painting for the soft drop shadow
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int shadowSize = 10;
        int cornerRadius = 16;
        int x = shadowSize;
        int y = shadowSize;
        int w = getWidth() - (shadowSize * 2);
        int h = getHeight() - (shadowSize * 2);

        // Draw the shadow
        for (int i = 0; i < shadowSize; i++) {
            g2.setColor(new Color(0, 0, 0, 15 - i)); // Fading shadow
            g2.fillRoundRect(x, y + i, w, h, cornerRadius, cornerRadius);
        }
        g2.dispose();
        super.paintComponent(g);
    }

    // --- Helper methods to build the card sections ---

    private JPanel createTopStrip(String category) {
        JPanel strip = new JPanel();
        strip.setBackground(getCategoryColor(category));
        strip.setPreferredSize(new Dimension(0, 6));
        return strip;
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout(0, 8));
        headerPanel.setOpaque(false);

        JLabel categoryBadge = new JLabel(event.getCategory().toUpperCase());
        categoryBadge.setFont(new Font("Segoe UI", Font.BOLD, 10));
        categoryBadge.setForeground(getCategoryColor(event.getCategory()));
        categoryBadge.setBackground(getCategoryColor(event.getCategory()).brighter().brighter());
        categoryBadge.setOpaque(true);
        categoryBadge.setBorder(new EmptyBorder(4, 8, 4, 8));

        JTextArea titleArea = new JTextArea(event.getName());
        titleArea.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleArea.setForeground(new Color(30, 30, 30));
        titleArea.setLineWrap(true);
        titleArea.setWrapStyleWord(true);
        titleArea.setEditable(false);
        titleArea.setOpaque(false);

        JPanel badgeWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        badgeWrapper.setOpaque(false);
        badgeWrapper.add(categoryBadge);

        headerPanel.add(badgeWrapper, BorderLayout.NORTH);
        headerPanel.add(titleArea, BorderLayout.CENTER);
        return headerPanel;
    }

    private JPanel createInfoPanel() {
        JPanel infoPanel = new JPanel();
        infoPanel.setOpaque(false);
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.add(createInfoRow("📅", event.getDate() + "  •  " + event.getTime()));
        infoPanel.add(Box.createVerticalStrut(8));
        infoPanel.add(createInfoRow("📍", event.getVenue()));
        return infoPanel;
    }

    private JPanel createInfoRow(String icon, String text) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        row.setOpaque(false);
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        iconLabel.setBorder(new EmptyBorder(0, 0, 0, 10));
        JLabel textLabel = new JLabel(text);
        textLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textLabel.setForeground(new Color(80, 80, 80));
        row.add(iconLabel);
        row.add(textLabel);
        return row;
    }

    private JPanel createBottomBar() {
        JPanel bottomBar = new JPanel(new BorderLayout(10, 0));
        bottomBar.setOpaque(false);
        bottomBar.setBorder(new CompoundBorder(
                new MatteBorder(1, 0, 0, 0, new Color(230, 230, 230)),
                new EmptyBorder(15, 0, 0, 0))
        );

        JPanel leftInfo = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        leftInfo.setOpaque(false);
        JLabel priceLabel = new JLabel("₹" + String.format("%,.0f", event.getPrice()));
        priceLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        priceLabel.setForeground(new Color(59, 130, 246));
        JLabel seatsLabel = new JLabel(event.getAvailableSeats() + " seats available");
        seatsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        seatsLabel.setForeground(event.getAvailableSeats() > 20 ? new Color(16, 185, 129) : new Color(239, 68, 68));
        leftInfo.add(priceLabel);
        leftInfo.add(seatsLabel);

        JPanel rightButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightButtons.setOpaque(false);
        JButton detailsButton = new JButton("Details");
        styleSecondaryButton(detailsButton);
        JButton registerButton = new JButton("Register");
        stylePrimaryButton(registerButton);
        rightButtons.add(detailsButton);
        rightButtons.add(registerButton);

        bottomBar.add(leftInfo, BorderLayout.CENTER);
        bottomBar.add(rightButtons, BorderLayout.EAST);
        return bottomBar;
    }

    // --- Button Styling ---

    private void stylePrimaryButton(JButton btn) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(59, 130, 246));
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(8, 18, 8, 18));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void styleSecondaryButton(JButton btn) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(new Color(50, 50, 50));
        btn.setBackground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new CompoundBorder(
                new MatteBorder(1, 1, 1, 1, new Color(200, 200, 200)),
                new EmptyBorder(7, 17, 7, 17))
        );
    }

    private Color getCategoryColor(String category) {
        switch (category.toLowerCase()) {
            case "conference": return new Color(59, 130, 246);
            case "workshop": return new Color(168, 85, 247);
            case "seminar": return new Color(16, 185, 129);
            case "training": return new Color(251, 146, 60);
            case "networking": return new Color(236, 72, 153);
            case "webinar": return new Color(6, 182, 212);
            case "technology": return new Color(139, 92, 246);    // ADD THIS LINE
            default: return new Color(107, 114, 128);
        }
    }

    // --- Main Method to run the application ---
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Event Management System");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(900, 800); // Adjusted size for a single column
            frame.setLocationRelativeTo(null);

            // This is the main container for all cards, with a vertical layout
            JPanel cardContainer = new JPanel();
            cardContainer.setLayout(new BoxLayout(cardContainer, BoxLayout.Y_AXIS));
            cardContainer.setBackground(new Color(243, 244, 246));
            cardContainer.setBorder(new EmptyBorder(20, 20, 20, 20));

            Event[] events = {
                    new Event(1, "AI & Machine Learning Conference 2025", "Conference", "...", "2025-11-20", "9:00 AM", "Tech Convention Center", 200, 87, 2500, "...", "..."),
                    new Event(2, "Digital Marketing Mastery Workshop", "Workshop", "...", "2025-10-15", "2:00 PM", "Business Hub Sector 18", 50, 12, 1500, "...", "..."),
                    new Event(3, "Leadership Excellence Training", "Training", "...", "2025-11-05", "10:00 AM", "Corporate Learning Center", 30, 156, 3000, "...", "..."),
                    new Event(4, "Tech Startup Networking Event", "Networking", "...", "2025-10-25", "6:00 PM", "Innovation Hub", 80, 45, 500, "...", "..."),
                    new Event(5, "Product Launch Webinar", "Webinar", "...", "2025-10-30", "3:00 PM", "Online Platform", 500, 234, 0, "...", "..."),
                    new Event(6, "Business Strategy Seminar", "Seminar", "...", "2025-11-12", "11:00 AM", "Grand Hotel Conference Hall", 120, 89, 2000, "...", "...")
            };

            for (Event event : events) {
                cardContainer.add(new EventCardPanel(event));
                cardContainer.add(Box.createRigidArea(new Dimension(0, 20))); // Vertical space between cards
            }

            JScrollPane scrollPane = new JScrollPane(cardContainer);
            scrollPane.getVerticalScrollBar().setUnitIncrement(16);
            scrollPane.setBorder(null);

            frame.add(scrollPane);
            frame.setVisible(true);
        });
    }
}

// The Event data class (unchanged from your original)
class Event {
    private int id;
    private String name, category, description, date, time, venue;
    private int totalSeats, availableSeats;
    private double price;
    private String organizerName, organizerContact;

    public Event(int id, String name, String category, String description,
                 String date, String time, String venue, int totalSeats,
                 int availableSeats, double price, String organizerName, String organizerContact) {
        this.id = id; this.name = name; this.category = category; this.description = description;
        this.date = date; this.time = time; this.venue = venue; this.totalSeats = totalSeats;
        this.availableSeats = availableSeats; this.price = price; this.organizerName = organizerName;
        this.organizerContact = organizerContact;
    }

    public String getName() { return name; }
    public String getCategory() { return category; }
    public String getDate() { return date; }
    public String getTime() { return time; }
    public String getVenue() { return venue; }
    public int getAvailableSeats() { return availableSeats; }
    public double getPrice() { return price; }
}