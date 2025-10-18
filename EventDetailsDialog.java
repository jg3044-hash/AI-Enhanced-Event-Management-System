import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class EventDetailsDialog extends JDialog {
    // This now correctly refers to your Event1 class
    private final Event1 event;
    private final User user;
    private JButton actionButton;

    // Color Palette
    private static final Color PRIMARY_COLOR = new Color(59, 130, 246);
    private static final Color BG_COLOR = new Color(243, 244, 246);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color TEXT_COLOR = new Color(30, 30, 30);
    private static final Color SUBTEXT_COLOR = new Color(100, 100, 100);

    public EventDetailsDialog(Frame parent, Event1 event, User user) {
        super(parent, "Event Details", true);
        this.event = event;
        this.user = user;

        setSize(650, 700);
        setLocationRelativeTo(parent);
        setResizable(false);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BG_COLOR);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(BG_COLOR);
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        contentPanel.add(createEventHeader());
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        contentPanel.add(createEventInfoCard());
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        contentPanel.add(createOrganizerCard());
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        contentPanel.add(createAIPredictionCard());
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        contentPanel.add(createSimilarEventsCard());

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(createButtonPanel(), BorderLayout.SOUTH);
        add(mainPanel);
    }

    private JPanel createEventHeader() {
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);

        JLabel nameLabel = new JLabel(event.getEventName());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        nameLabel.setForeground(TEXT_COLOR);
        headerPanel.add(nameLabel);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        JPanel badgePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        badgePanel.setOpaque(false);
        JLabel categoryBadge = new JLabel(" " + event.getCategory().toUpperCase() + " ");
        categoryBadge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        categoryBadge.setForeground(Color.WHITE);
        categoryBadge.setBackground(getCategoryColor(event.getCategory()));
        categoryBadge.setOpaque(true);
        categoryBadge.setBorder(new EmptyBorder(6, 12, 6, 12));

        JLabel statusLabel = new JLabel(getStatusText(event));
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        statusLabel.setForeground(getStatusColor(event));
        statusLabel.setBorder(new EmptyBorder(6, 12, 6, 12));
        badgePanel.add(categoryBadge);
        badgePanel.add(Box.createRigidArea(new Dimension(8, 0)));
        badgePanel.add(statusLabel);
        headerPanel.add(badgePanel);

        return headerPanel;
    }

    private JPanel createEventInfoCard() {
        JPanel cardPanel = createCard();
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        contentPanel.add(createInfoRow("📅 Date & Time", event.getEventDate() + " at " + event.getStartTime()));
        contentPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        contentPanel.add(createInfoRow("📍 Venue", event.getVenue()));
        contentPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        int available = event.getAvailableSeats();
        contentPanel.add(createInfoRow("👥 Capacity", available + " / " + event.getMaxAttendees() + " seats available"));
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        JLabel descLabel = new JLabel("Description");
        descLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        descLabel.setForeground(TEXT_COLOR);
        contentPanel.add(descLabel);

        JTextArea descArea = new JTextArea(event.getDescription());
        descArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        descArea.setForeground(SUBTEXT_COLOR);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setEditable(false);
        descArea.setOpaque(false);
        descArea.setBorder(null);
        contentPanel.add(descArea);

        cardPanel.add(contentPanel, BorderLayout.CENTER);
        return cardPanel;
    }

    private JPanel createOrganizerCard() {
        JPanel cardPanel = createCard();
        JPanel contentPanel = new JPanel(new GridLayout(1, 1, 0, 8));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel organiserLabel = new JLabel("Organized by: " + event.getOrganizerName());
        organiserLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        organiserLabel.setForeground(TEXT_COLOR);
        contentPanel.add(organiserLabel);
        cardPanel.add(contentPanel, BorderLayout.CENTER);
        return cardPanel;
    }

    private JPanel createAIPredictionCard() {
        JPanel cardPanel = createCard();
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel titleLabel = new JLabel("🤖 AI Insights");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(PRIMARY_COLOR);
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        int predictedAttendees = (int) (event.getMaxAttendees() * 0.75);
        int confidence = 82;

        JLabel predictionLabel = new JLabel("Predicted Attendance: " + predictedAttendees + " (Confidence: " + confidence + "%)");
        predictionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        predictionLabel.setForeground(SUBTEXT_COLOR);
        contentPanel.add(predictionLabel);

        JProgressBar confidenceBar = new JProgressBar(0, 100);
        confidenceBar.setValue(confidence);
        confidenceBar.setString(confidence + "%");
        confidenceBar.setStringPainted(true);
        confidenceBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        contentPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        contentPanel.add(confidenceBar);

        cardPanel.add(contentPanel, BorderLayout.CENTER);
        return cardPanel;
    }

    private JPanel createSimilarEventsCard() {
        JPanel cardPanel = createCard();
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel titleLabel = new JLabel("✨ Similar Events");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(PRIMARY_COLOR);
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        String[] similarEvents = {"• Cloud Computing Masterclass", "• Web Development Workshop"};

        for (String eventStr : similarEvents) {
            JLabel eventLabel = new JLabel(eventStr);
            eventLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            eventLabel.setForeground(SUBTEXT_COLOR);
            contentPanel.add(eventLabel);
            contentPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        }
        cardPanel.add(contentPanel, BorderLayout.CENTER);
        return cardPanel;
    }

    private JPanel createInfoRow(String label, String value) {
        JPanel rowPanel = new JPanel(new BorderLayout(10, 0));
        rowPanel.setOpaque(false);
        rowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));

        JLabel labelComp = new JLabel(label);
        labelComp.setFont(new Font("Segoe UI", Font.BOLD, 12));
        labelComp.setForeground(TEXT_COLOR);
        labelComp.setPreferredSize(new Dimension(120, 25));

        JLabel valueComp = new JLabel(value);
        valueComp.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        valueComp.setForeground(SUBTEXT_COLOR);

        rowPanel.add(labelComp, BorderLayout.WEST);
        rowPanel.add(valueComp, BorderLayout.CENTER);
        return rowPanel;
    }

    private JPanel createCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_BG);
        card.setBorder(new EmptyBorder(10, 10, 10, 10));
        return card;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        buttonPanel.setBackground(CARD_BG);
        buttonPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 220)));

        if ("ATTENDEE".equals(user.getRole())) {
            actionButton = createButton("Register Now", PRIMARY_COLOR);
            actionButton.addActionListener(e -> handleRegistration());
        } else {
            actionButton = createButton("Edit Event", PRIMARY_COLOR);
            actionButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "Edit feature coming soon"));
        }

        JButton closeButton = createButton("Close", new Color(220, 220, 220));
        closeButton.addActionListener(e -> dispose());

        if (actionButton != null) buttonPanel.add(actionButton);
        buttonPanel.add(closeButton);

        return buttonPanel;
    }

    private JButton createButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setForeground(bgColor.equals(new Color(220, 220, 220)) ? TEXT_COLOR : Color.WHITE);
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(10, 25, 10, 25));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void handleRegistration() {
        JOptionPane.showMessageDialog(this, "Registration Successful!");
    }

    private void handleCancelRegistration() {
        JOptionPane.showMessageDialog(this, "Registration Cancelled!");
    }

    private Color getCategoryColor(String category) {
        switch (category.toLowerCase()) {
            case "technology": case "conference": return new Color(59, 130, 246);
            case "business": return new Color(34, 197, 94);
            case "training": case "workshop": return new Color(251, 146, 60);
            case "innovation": return new Color(168, 85, 247);
            case "education": return new Color(239, 68, 68);
            default: return new Color(100, 116, 139);
        }
    }

    private String getStatusText(Event1 event) {
        if (event.getAvailableSeats() <= 0) return "SOLD OUT";
        if (event.getAvailableSeats() < 20) return "LIMITED SEATS";
        return "AVAILABLE";
    }

    private Color getStatusColor(Event1 event) {
        if (event.getAvailableSeats() <= 0) return new Color(239, 68, 68);
        if (event.getAvailableSeats() < 20) return new Color(251, 146, 60);
        return new Color(34, 197, 94);
    }
}