import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.border.*;
import java.awt.*;
import javax.swing.table.JTableHeader;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.geom.RoundRectangle2D;
import java.util.List;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.sql.Date;

public class OrganizerDashboard extends JFrame {
    private User currentUser;
    private JTable eventsTable;
    private DefaultTableModel tableModel;
    private JLabel totalEventsLabel;
    private JLabel totalRegistrationsLabel;
    private JLabel upcomingEventsLabel;
    private JButton currentSelectedButton = null;
    private JPanel mainContentArea;
    private CardLayout cardLayout;

    // Modern Colors
    private static final Color ACCENT_COLOR = new Color(59, 130, 246);
    private static final Color GRADIENT_START = new Color(55, 100, 180);
    private static final Color GRADIENT_END = new Color(40, 70, 140);
    private static final Color BG_COLOR = new Color(243, 244, 246);
    private static final Color TEXT_COLOR = new Color(30, 30, 30);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color SUCCESS_COLOR = new Color(76, 175, 80);
    private static final Color DANGER_COLOR = new Color(239, 68, 68);
    private static final Color WARNING_COLOR = new Color(255, 152, 0);

    public OrganizerDashboard(User user) {
        this.currentUser = user;
        setTitle("Event Sphere - Organizer Dashboard");
        setSize(1400, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(BG_COLOR);
        mainContainer.add(createModernHeader(), BorderLayout.NORTH);

        JPanel bodyPanel = new JPanel(new BorderLayout());
        bodyPanel.setBackground(BG_COLOR);
        bodyPanel.add(createModernSidebar(), BorderLayout.WEST);

        // Use CardLayout for switching views
        cardLayout = new CardLayout();
        mainContentArea = new JPanel(cardLayout);
        mainContentArea.setBackground(BG_COLOR);

        mainContentArea.add(createDashboardView(), "dashboard");
        mainContentArea.add(createCalendarView(), "calendar");
        mainContentArea.add(createAnalyticsView(), "analytics");

        bodyPanel.add(mainContentArea, BorderLayout.CENTER);

        mainContainer.add(bodyPanel, BorderLayout.CENTER);
        add(mainContainer);
        setVisible(true);
        refreshEventsTable();
    }

    private JPanel createModernHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(0, 0, GRADIENT_START, getWidth(), 0, GRADIENT_END);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        headerPanel.setPreferredSize(new Dimension(0, 70));
        headerPanel.setBorder(new EmptyBorder(15, 30, 15, 30));

        JLabel titleLabel = new JLabel("📅 Event Sphere Organizer");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setForeground(Color.WHITE);

        JLabel welcomeLabel = new JLabel("Welcome, " + currentUser.getFullName());
        welcomeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        welcomeLabel.setForeground(new Color(220, 220, 220));

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(welcomeLabel, BorderLayout.EAST);
        return headerPanel;
    }

    private JPanel createModernSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(CARD_BG);
        sidebar.setBorder(new EmptyBorder(30, 15, 30, 15));
        sidebar.setPreferredSize(new Dimension(220, 0));

        JButton dashboardBtn = createModernMenuButton("+ Dashboard");
        JButton calendarBtn = createModernMenuButton("= Calendar");
        JButton analyticsBtn = createModernMenuButton("* Analytics");
        JButton createBtn = createModernMenuButton("^ Create Event");
        JButton logoutBtn = createModernMenuButton("> Logout");

        currentSelectedButton = dashboardBtn;
        dashboardBtn.setBackground(ACCENT_COLOR);
        dashboardBtn.setForeground(Color.WHITE);

        dashboardBtn.addActionListener(e -> {
            setSelectedButton(dashboardBtn);
            cardLayout.show(mainContentArea, "dashboard");
            refreshEventsTable();
        });

        calendarBtn.addActionListener(e -> {
            setSelectedButton(calendarBtn);
            cardLayout.show(mainContentArea, "calendar");
        });

        analyticsBtn.addActionListener(e -> {
            setSelectedButton(analyticsBtn);
            cardLayout.show(mainContentArea, "analytics");
            refreshAnalytics();
        });

        createBtn.addActionListener(e -> {
            new CreateEventDialog(this, currentUser).setVisible(true);
        });

        logoutBtn.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to logout?",
                    "Confirm Logout",
                    JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                dispose();
                new LoginFrame().setVisible(true);
            }
        });

        sidebar.add(dashboardBtn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(calendarBtn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(analyticsBtn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(createBtn);
        sidebar.add(Box.createVerticalGlue());
        sidebar.add(logoutBtn);

        return sidebar;
    }

    private JButton createModernMenuButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2.setColor(getBackground().darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(getBackground().brighter());
                } else {
                    g2.setColor(getBackground());
                }
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(190, 45));
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setBackground(BG_COLOR);
        button.setForeground(TEXT_COLOR);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        return button;
    }

    private void setSelectedButton(JButton selectedButton) {
        if (currentSelectedButton != null) {
            currentSelectedButton.setBackground(BG_COLOR);
            currentSelectedButton.setForeground(TEXT_COLOR);
        }
        currentSelectedButton = selectedButton;
        selectedButton.setBackground(ACCENT_COLOR);
        selectedButton.setForeground(Color.WHITE);
    }

    // DASHBOARD VIEW - Original table view
    private JPanel createDashboardView() {
        JPanel mainPanel = new JPanel(new BorderLayout(0, 20));
        mainPanel.setBackground(BG_COLOR);
        mainPanel.setBorder(new EmptyBorder(30, 30, 30, 30));
        mainPanel.add(createStatisticsPanel(), BorderLayout.NORTH);
        mainPanel.add(createEventsTablePanel(), BorderLayout.CENTER);
        return mainPanel;
    }

    // NEW: CALENDAR VIEW
    private JPanel createCalendarView() {
        JPanel calendarPanel = new JPanel(new BorderLayout(0, 20));
        calendarPanel.setBackground(BG_COLOR);
        calendarPanel.setBorder(new EmptyBorder(30, 30, 30, 30));

        JLabel titleLabel = new JLabel("# Event Calendar - " + YearMonth.now());
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_COLOR);
        calendarPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel calendarCard = createModernCard();
        calendarCard.setLayout(new BorderLayout());

        // Calendar Grid
        JPanel calendarGrid = new JPanel(new GridLayout(6, 7, 5, 5));
        calendarGrid.setBackground(CARD_BG);

        // Days header
        String[] days = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        JPanel daysHeader = new JPanel(new GridLayout(1, 7, 5, 5));
        daysHeader.setBackground(CARD_BG);
        for (String day : days) {
            JLabel dayLabel = new JLabel(day, SwingConstants.CENTER);
            dayLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
            dayLabel.setForeground(ACCENT_COLOR);
            daysHeader.add(dayLabel);
        }

        // Get current month data
        YearMonth currentMonth = YearMonth.now();
        LocalDate firstDay = currentMonth.atDay(1);
        int daysInMonth = currentMonth.lengthOfMonth();
        int startDayOfWeek = firstDay.getDayOfWeek().getValue() % 7;

        // Get events for this month
        List<Event1> events = EventDAO.getEventsByOrganizer(currentUser.getUserId());

        // Create calendar cells
        int dayCounter = 1;
        for (int i = 0; i < 42; i++) {
            JPanel dayCell = new JPanel();
            dayCell.setLayout(new BorderLayout());
            dayCell.setBackground(Color.WHITE);
            dayCell.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));

            if (i >= startDayOfWeek && dayCounter <= daysInMonth) {
                JLabel dayNum = new JLabel(String.valueOf(dayCounter));
                dayNum.setFont(new Font("Segoe UI", Font.BOLD, 14));
                dayNum.setBorder(new EmptyBorder(5, 5, 5, 5));

                // Highlight today
                LocalDate cellDate = LocalDate.of(currentMonth.getYear(), currentMonth.getMonth(), dayCounter);
                if (cellDate.equals(LocalDate.now())) {
                    dayCell.setBackground(new Color(220, 240, 255));
                    dayNum.setForeground(ACCENT_COLOR);
                }

                // Check if any events on this day
                int eventsOnDay = 0;
                for (Event1 event : events) {
                    LocalDate eventDate = event.getEventDate().toLocalDate();
                    if (eventDate.equals(cellDate)) {
                        eventsOnDay++;
                    }
                }

                dayCell.add(dayNum, BorderLayout.NORTH);

                if (eventsOnDay > 0) {
                    JLabel eventDot = new JLabel("● " + eventsOnDay + " event(s)");
                    eventDot.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                    eventDot.setForeground(SUCCESS_COLOR);
                    eventDot.setBorder(new EmptyBorder(0, 5, 5, 5));
                    dayCell.add(eventDot, BorderLayout.CENTER);
                }

                dayCounter++;
            } else {
                dayCell.setBackground(new Color(250, 250, 250));
            }

            calendarGrid.add(dayCell);
        }

        JPanel calendarContent = new JPanel(new BorderLayout(0, 10));
        calendarContent.setBackground(CARD_BG);
        calendarContent.add(daysHeader, BorderLayout.NORTH);
        calendarContent.add(calendarGrid, BorderLayout.CENTER);

        calendarCard.add(calendarContent, BorderLayout.CENTER);
        calendarPanel.add(calendarCard, BorderLayout.CENTER);

        return calendarPanel;
    }

    // NEW: ANALYTICS VIEW
    private JPanel analyticsPanel;
    private JPanel categoryBarsPanel;
    private DefaultTableModel analyticsTableModel;

    private JPanel createAnalyticsView() {
        analyticsPanel = new JPanel(new BorderLayout(0, 20));
        analyticsPanel.setBackground(BG_COLOR);
        analyticsPanel.setBorder(new EmptyBorder(30, 30, 30, 30));

        JLabel titleLabel = new JLabel("* Event Analytics");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_COLOR);
        analyticsPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new GridLayout(2, 1, 20, 20));
        contentPanel.setBackground(BG_COLOR);

        // Category breakdown with bars
        JPanel categoryCard = createModernCard();
        categoryCard.setLayout(new BorderLayout());
        JLabel catTitle = new JLabel("Events by Category");
        catTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        catTitle.setBorder(new EmptyBorder(0, 0, 15, 0));
        categoryCard.add(catTitle, BorderLayout.NORTH);

        categoryBarsPanel = new JPanel();
        categoryBarsPanel.setLayout(new BoxLayout(categoryBarsPanel, BoxLayout.Y_AXIS));
        categoryBarsPanel.setBackground(CARD_BG);
        categoryCard.add(categoryBarsPanel, BorderLayout.CENTER);

        // Popular events table
        JPanel popularCard = createModernCard();
        popularCard.setLayout(new BorderLayout());
        JLabel popTitle = new JLabel("Most Popular Events (By Registrations)");
        popTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        popTitle.setBorder(new EmptyBorder(0, 0, 15, 0));
        popularCard.add(popTitle, BorderLayout.NORTH);

        String[] columns = {"Event Name", "Category", "Registrations", "Capacity", "Fill Rate"};
        analyticsTableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable popTable = new JTable(analyticsTableModel);
        popTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        popTable.setRowHeight(35);
        popTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        popTable.getTableHeader().setBackground(ACCENT_COLOR);
        popTable.getTableHeader().setForeground(Color.WHITE);
        popularCard.add(new JScrollPane(popTable), BorderLayout.CENTER);

        contentPanel.add(categoryCard);
        contentPanel.add(popularCard);
        analyticsPanel.add(contentPanel, BorderLayout.CENTER);

        return analyticsPanel;
    }

    private void refreshAnalytics() {
        List<Event1> events = EventDAO.getEventsByOrganizer(currentUser.getUserId());

        // Clear previous data
        categoryBarsPanel.removeAll();
        analyticsTableModel.setRowCount(0);

        if (events.isEmpty()) {
            JLabel noData = new JLabel("No events created yet!");
            noData.setFont(new Font("Segoe UI", Font.ITALIC, 14));
            noData.setForeground(new Color(150, 150, 150));
            categoryBarsPanel.add(noData);
        } else {
            // Count events by category
            java.util.Map<String, Integer> categoryCount = new java.util.HashMap<>();
            for (Event1 event : events) {
                String cat = event.getCategory();
                categoryCount.put(cat, categoryCount.getOrDefault(cat, 0) + 1);
            }

            // Find max for bar scaling
            int maxCount = categoryCount.values().stream().max(Integer::compare).orElse(1);

            // Create bars for each category
            Color[] barColors = {ACCENT_COLOR, SUCCESS_COLOR, WARNING_COLOR,
                    new Color(156, 39, 176), new Color(255, 87, 34)};
            int colorIndex = 0;

            for (java.util.Map.Entry<String, Integer> entry : categoryCount.entrySet()) {
                JPanel barPanel = new JPanel(new BorderLayout(10, 5));
                barPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
                barPanel.setBackground(CARD_BG);
                barPanel.setBorder(new EmptyBorder(5, 0, 5, 0));

                JLabel categoryLabel = new JLabel(entry.getKey());
                categoryLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
                categoryLabel.setPreferredSize(new Dimension(120, 20));

                JPanel barContainer = new JPanel(new BorderLayout());
                barContainer.setBackground(new Color(240, 240, 240));
                barContainer.setPreferredSize(new Dimension(0, 30));

                int barWidth = (int)((entry.getValue() / (double)maxCount) * 100);
                JPanel colorBar = new JPanel();
                colorBar.setBackground(barColors[colorIndex % barColors.length]);
                colorBar.setPreferredSize(new Dimension(barWidth * 4, 30));

                JLabel countLabel = new JLabel(" " + entry.getValue() + " events");
                countLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
                countLabel.setForeground(Color.WHITE);
                colorBar.add(countLabel);

                barContainer.add(colorBar, BorderLayout.WEST);

                barPanel.add(categoryLabel, BorderLayout.WEST);
                barPanel.add(barContainer, BorderLayout.CENTER);

                categoryBarsPanel.add(barPanel);
                colorIndex++;
            }

            // Sort events by registration count
            events.sort((e1, e2) -> Integer.compare(e2.getCurrentAttendees(), e1.getCurrentAttendees()));

            // Show top 10 most popular events
            int limit = Math.min(10, events.size());
            for (int i = 0; i < limit; i++) {
                Event1 event = events.get(i);
                int registrations = event.getCurrentAttendees();
                int capacity = event.getMaxAttendees();
                double fillRate = (capacity > 0) ? (registrations * 100.0 / capacity) : 0;

                analyticsTableModel.addRow(new Object[]{
                        event.getEventName(),
                        event.getCategory(),
                        registrations,
                        capacity,
                        String.format("%.1f%%", fillRate)
                });
            }
        }

        categoryBarsPanel.revalidate();
        categoryBarsPanel.repaint();
    }

    private JPanel createStatisticsPanel() {
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        statsPanel.setBackground(BG_COLOR);
        statsPanel.setPreferredSize(new Dimension(0, 120));

        JPanel eventCard = createModernStatCard("Total Events", "0", ACCENT_COLOR);
        totalEventsLabel = (JLabel) eventCard.getComponent(1);

        JPanel regCard = createModernStatCard("Total Registrations", "0", SUCCESS_COLOR);
        totalRegistrationsLabel = (JLabel) regCard.getComponent(1);

        JPanel upcomingCard = createModernStatCard("Upcoming Events", "0", WARNING_COLOR);
        upcomingEventsLabel = (JLabel) upcomingCard.getComponent(1);

        statsPanel.add(eventCard);
        statsPanel.add(regCard);
        statsPanel.add(upcomingCard);
        return statsPanel;
    }

    private JPanel createModernStatCard(String title, String value, Color color) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int shadowSize = 8;
                for (int i = 0; i < shadowSize; i++) {
                    g2.setColor(new Color(0, 0, 0, 10 - i));
                    g2.fillRoundRect(i, i, getWidth() - i * 2, getHeight() - i * 2, 15, 15);
                }
                g2.setColor(color);
                g2.fillRoundRect(shadowSize, shadowSize, getWidth() - shadowSize * 2, getHeight() - shadowSize * 2, 15, 15);
                g2.dispose();
            }
        };
        card.setLayout(new GridLayout(2, 1));
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        titleLabel.setForeground(Color.WHITE);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        valueLabel.setForeground(Color.WHITE);

        card.add(titleLabel);
        card.add(valueLabel);
        return card;
    }

    private JPanel createEventsTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout(0, 15));
        tablePanel.setBackground(BG_COLOR);

        // Action buttons panel
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actionPanel.setBackground(BG_COLOR);

        JButton editBtn = createActionButton("> Edit", ACCENT_COLOR);
        JButton deleteBtn = createActionButton("> Delete", DANGER_COLOR);
        JButton viewRegBtn = createActionButton("> View Registrations", SUCCESS_COLOR);

        editBtn.addActionListener(e -> {
            if (eventsTable.getSelectedRow() != -1) {
                int eventId = (int) tableModel.getValueAt(eventsTable.getSelectedRow(), 0);
                Event1 event = EventDAO.getEventById(eventId);
                new EditEventDialog(this, event).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Please select an event!");
            }
        });

        deleteBtn.addActionListener(e -> {
            if (eventsTable.getSelectedRow() != -1) {
                int choice = JOptionPane.showConfirmDialog(this,
                        "Delete this event?", "Confirm", JOptionPane.YES_NO_OPTION);
                if (choice == JOptionPane.YES_OPTION) {
                    int eventId = (int) tableModel.getValueAt(eventsTable.getSelectedRow(), 0);
                    if (EventDAO.deleteEvent(eventId)) {
                        JOptionPane.showMessageDialog(this, "Event deleted!");
                        refreshEventsTable();
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select an event!");
            }
        });

        viewRegBtn.addActionListener(e -> {
            if (eventsTable.getSelectedRow() != -1) {
                int eventId = (int) tableModel.getValueAt(eventsTable.getSelectedRow(), 0);
                new ViewRegistrationsDialog(this, eventId).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Please select an event!");
            }
        });

        actionPanel.add(editBtn);
        actionPanel.add(deleteBtn);
        actionPanel.add(viewRegBtn);

        JLabel title = new JLabel("Your Events");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(TEXT_COLOR);

        String[] columns = {"ID", "Name", "Category", "Date", "Time", "Attendees", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };

        eventsTable = new JTable(tableModel);
        styleModernTable(eventsTable);

        JScrollPane scrollPane = new JScrollPane(eventsTable);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(CARD_BG);

        JPanel tableCard = createModernCard();
        tableCard.setLayout(new BorderLayout(0, 15));
        tableCard.add(title, BorderLayout.NORTH);
        tableCard.add(scrollPane, BorderLayout.CENTER);
        tableCard.add(actionPanel, BorderLayout.SOUTH);

        tablePanel.add(tableCard, BorderLayout.CENTER);
        return tablePanel;
    }

    private JButton createActionButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        return btn;
    }

    private void styleModernTable(JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(40);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(ACCENT_COLOR);
        table.setSelectionForeground(Color.WHITE);
        table.setBackground(CARD_BG);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(BG_COLOR);
        header.setForeground(TEXT_COLOR);
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 45));
    }

    private JPanel createModernCard() {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int shadowSize = 8;
                for (int i = 0; i < shadowSize; i++) {
                    g2.setColor(new Color(0, 0, 0, 10 - i));
                    g2.fillRoundRect(i, i, getWidth() - i * 2, getHeight() - i * 2, 16, 16);
                }
                g2.setColor(CARD_BG);
                g2.fillRoundRect(shadowSize, shadowSize, getWidth() - shadowSize * 2, getHeight() - shadowSize * 2, 16, 16);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(30, 30, 30, 30));
        return card;
    }

    public void refreshEventsTable() {
        tableModel.setRowCount(0);
        List<Event1> events = EventDAO.getEventsByOrganizer(currentUser.getUserId());

        int upcomingCount = 0;
        for (Event1 event : events) {
            tableModel.addRow(new Object[]{
                    event.getEventId(),
                    event.getEventName(),
                    event.getCategory(),
                    event.getEventDate(),
                    event.getStartTime(),
                    event.getCurrentAttendees() + "/" + event.getMaxAttendees(),
                    event.getStatus()
            });

            if ("UPCOMING".equals(event.getStatus())) {
                upcomingCount++;
            }
        }

        int totalRegs = RegistrationDAO.getRegistrationCountForOrganizer(currentUser.getUserId());
        totalEventsLabel.setText(String.valueOf(events.size()));
        totalRegistrationsLabel.setText(String.valueOf(totalRegs));
        upcomingEventsLabel.setText(String.valueOf(upcomingCount));
    }
}

class CreateEventDialog extends JDialog {
    private User organizer;
    private JTextField nameField, venueField, dateField, startField, endField, maxField;
    private JTextArea descArea;
    private JComboBox<String> categoryCombo;

    public CreateEventDialog(JFrame parent, User organizer) {
        super(parent, "Create New Event", true);
        this.organizer = organizer;
        setSize(550, 650);
        setLocationRelativeTo(parent);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(25, 25, 25, 25));
        panel.setBackground(new Color(243, 244, 246));

        panel.add(createLabel("Event Name:"));
        nameField = new JTextField();
        panel.add(nameField);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        panel.add(createLabel("Description:"));
        descArea = new JTextArea(3, 30);
        panel.add(new JScrollPane(descArea));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        panel.add(createLabel("Category:"));
        categoryCombo = new JComboBox<>(new String[]{"Technology", "Business", "Training", "Innovation", "Education"});
        panel.add(categoryCombo);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        panel.add(createLabel("Venue:"));
        venueField = new JTextField();
        panel.add(venueField);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        panel.add(createLabel("Date (YYYY-MM-DD):"));
        dateField = new JTextField();
        panel.add(dateField);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        panel.add(createLabel("Start Time (HH:MM:SS):"));
        startField = new JTextField();
        panel.add(startField);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        panel.add(createLabel("End Time (HH:MM:SS):"));
        endField = new JTextField();
        panel.add(endField);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        panel.add(createLabel("Max Attendees:"));
        maxField = new JTextField();
        panel.add(maxField);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        JPanel btnPanel = new JPanel();
        JButton createBtn = new JButton("Create Event");
        createBtn.setBackground(new Color(76, 175, 80));
        createBtn.setForeground(Color.WHITE);
        createBtn.addActionListener(e -> handleCreate());

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(e -> dispose());

        btnPanel.add(createBtn);
        btnPanel.add(cancelBtn);
        panel.add(btnPanel);

        add(new JScrollPane(panel));
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        return label;
    }

    private void handleCreate() {
        if (nameField.getText().trim().isEmpty() || venueField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all required fields!");
            return;
        }

        try {
            LocalDate eventDate = LocalDate.parse(dateField.getText());
            if (eventDate.isBefore(LocalDate.now())) {
                JOptionPane.showMessageDialog(this, "Event date cannot be in the past!");
                return;
            }

            int maxAttendees = Integer.parseInt(maxField.getText());
            if (maxAttendees <= 0) {
                JOptionPane.showMessageDialog(this, "Max attendees must be greater than 0!");
                return;
            }

            Event1 event = new Event1();
            event.setEventName(nameField.getText().trim());
            event.setDescription(descArea.getText().trim());
            event.setCategory((String) categoryCombo.getSelectedItem());
            event.setOrganizerId(organizer.getUserId());
            event.setVenue(venueField.getText().trim());
            event.setEventDate(java.sql.Date.valueOf(dateField.getText()));
            event.setStartTime(java.sql.Time.valueOf(startField.getText()));
            event.setEndTime(java.sql.Time.valueOf(endField.getText()));
            event.setMaxAttendees(maxAttendees);
            event.setStatus("UPCOMING");

            if (EventDAO.createEvent(event)) {
                JOptionPane.showMessageDialog(this, "Event created successfully!");
                ((OrganizerDashboard) getOwner()).refreshEventsTable();
                dispose();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
}

class EditEventDialog extends JDialog {
    private Event1 event;
    private JTextField nameField, venueField, dateField, startField, endField, maxField;
    private JTextArea descArea;
    private JComboBox<String> categoryCombo, statusCombo;

    public EditEventDialog(JFrame parent, Event1 event) {
        super(parent, "Edit Event", true);
        this.event = event;
        setSize(550, 700);
        setLocationRelativeTo(parent);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(25, 25, 25, 25));
        panel.setBackground(new Color(243, 244, 246));

        panel.add(new JLabel("Event Name:"));
        nameField = new JTextField(event.getEventName());
        panel.add(nameField);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        panel.add(new JLabel("Description:"));
        descArea = new JTextArea(event.getDescription(), 3, 30);
        panel.add(new JScrollPane(descArea));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        panel.add(new JLabel("Category:"));
        categoryCombo = new JComboBox<>(new String[]{"Technology", "Business", "Training", "Innovation", "Education"});
        categoryCombo.setSelectedItem(event.getCategory());
        panel.add(categoryCombo);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        panel.add(new JLabel("Venue:"));
        venueField = new JTextField(event.getVenue());
        panel.add(venueField);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        panel.add(new JLabel("Date (YYYY-MM-DD):"));
        dateField = new JTextField(event.getEventDate().toString());
        panel.add(dateField);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        panel.add(new JLabel("Start Time (HH:MM:SS):"));
        startField = new JTextField(event.getStartTime().toString());
        panel.add(startField);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        panel.add(new JLabel("End Time (HH:MM:SS):"));
        endField = new JTextField(event.getEndTime().toString());
        panel.add(endField);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        panel.add(new JLabel("Max Attendees:"));
        maxField = new JTextField(String.valueOf(event.getMaxAttendees()));
        panel.add(maxField);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        panel.add(new JLabel("Status:"));
        statusCombo = new JComboBox<>(new String[]{"UPCOMING", "COMPLETED", "CANCELLED"});
        statusCombo.setSelectedItem(event.getStatus());
        panel.add(statusCombo);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        JPanel btnPanel = new JPanel();
        JButton saveBtn = new JButton("Save Changes");
        saveBtn.setBackground(new Color(59, 130, 246));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.addActionListener(e -> handleSave());

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(e -> dispose());

        btnPanel.add(saveBtn);
        btnPanel.add(cancelBtn);
        panel.add(btnPanel);

        add(new JScrollPane(panel));
    }

    private void handleSave() {
        try {
            event.setEventName(nameField.getText().trim());
            event.setDescription(descArea.getText().trim());
            event.setCategory((String) categoryCombo.getSelectedItem());
            event.setVenue(venueField.getText().trim());
            event.setEventDate(java.sql.Date.valueOf(dateField.getText()));
            event.setStartTime(java.sql.Time.valueOf(startField.getText()));
            event.setEndTime(java.sql.Time.valueOf(endField.getText()));
            event.setMaxAttendees(Integer.parseInt(maxField.getText()));
            event.setStatus((String) statusCombo.getSelectedItem());

            if (EventDAO.updateEvent(event)) {
                JOptionPane.showMessageDialog(this, "Event updated successfully!");
                ((OrganizerDashboard) getOwner()).refreshEventsTable();
                dispose();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, " Error: " + ex.getMessage());
        }
    }
}

class ViewRegistrationsDialog extends JDialog {
    public ViewRegistrationsDialog(JFrame parent, int eventId) {
        super(parent, "Event Registrations", true);
        setSize(750, 500);
        setLocationRelativeTo(parent);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(243, 244, 246));

        JLabel titleLabel = new JLabel("& Registered Participants");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        String[] columns = {"User ID", "Name", "Email", "Registration Date"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };

        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(35);
        table.getTableHeader().setBackground(new Color(59, 130, 246));
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));

        List<Registration> regs = RegistrationDAO.getEventRegistrations(eventId);
        for (Registration reg : regs) {
            User user = UserDAO.getUserById(reg.getUserId());
            String email = (user != null) ? user.getEmail() : "N/A";
            model.addRow(new Object[]{
                    reg.getUserId(),
                    reg.getUserName(),
                    email,
                    reg.getRegistrationDate()
            });
        }

        JScrollPane scrollPane = new JScrollPane(table);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JLabel infoLabel = new JLabel("Total Registrations: " + regs.size());
        infoLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        mainPanel.add(infoLabel, BorderLayout.SOUTH);

        add(mainPanel);
    }
}