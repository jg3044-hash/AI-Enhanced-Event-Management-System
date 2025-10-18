import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * AdminDashboard - Modern UI with Export Features
 */
public class AdminDashboard extends JFrame {

    private User currentUser;
    private JPanel mainPanel;
    private CardLayout cardLayout;

    // Modern Theme Colors
    private static final Color ACCENT_COLOR = new Color(59, 130, 246);
    private static final Color GRADIENT_START = new Color(55, 100, 180);
    private static final Color GRADIENT_END = new Color(40, 70, 140);
    private static final Color TEXT_COLOR = new Color(30, 30, 30);
    private static final Color SUBTEXT_COLOR = new Color(80, 80, 80);
    private static final Color PANEL_BACKGROUND = new Color(243, 244, 246);
    private static final Color CARD_BACKGROUND = Color.WHITE;
    private static final Color SUCCESS_COLOR = new Color(76, 175, 80);
    private static final Color WARNING_COLOR = new Color(255, 152, 0);

    // Components
    private JLabel statsEventsLabel, statsUsersLabel, statsRegistrationsLabel, statsAttendanceLabel;
    private JTable eventsTable, usersTable;
    private DefaultTableModel eventsTableModel, usersTableModel;
    private JButton currentSelectedButton = null;

    public AdminDashboard(User user) {
        this.currentUser = user;
        initializeUI();
        loadDashboardData();
    }

    private void initializeUI() {
        setTitle("Admin Dashboard - Event Management System");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(PANEL_BACKGROUND);

        JPanel headerPanel = createModernHeaderPanel();
        container.add(headerPanel, BorderLayout.NORTH);

        JPanel sidebarPanel = createModernSidebarPanel();
        container.add(sidebarPanel, BorderLayout.WEST);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(PANEL_BACKGROUND);

        mainPanel.add(createDashboardPanel(), "dashboard");
        mainPanel.add(createEventsPanel(), "events");
        mainPanel.add(createUsersPanel(), "users");
        mainPanel.add(new AnalyticsPanel(), "analytics");

        container.add(mainPanel, BorderLayout.CENTER);

        add(container);
        setVisible(true);
    }

    private JPanel createModernHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout()) {
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
        panel.setPreferredSize(new Dimension(0, 70));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 30));

        JLabel titleLabel = new JLabel("Event Sphere Admin");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setForeground(Color.WHITE);

        JLabel userLabel = new JLabel("Welcome, " + currentUser.getFullName());
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        userLabel.setForeground(new Color(220, 220, 220));

        panel.add(titleLabel, BorderLayout.WEST);
        panel.add(userLabel, BorderLayout.EAST);

        return panel;
    }

    private JPanel createModernSidebarPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(CARD_BACKGROUND);
        panel.setPreferredSize(new Dimension(220, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 15, 30, 15));

        JButton dashboardBtn = createModernMenuButton("Dashboard");
        JButton eventsBtn = createModernMenuButton("Manage Events");
        JButton usersBtn = createModernMenuButton("Manage Users");
        JButton analyticsBtn = createModernMenuButton("Analytics");
        JButton logoutBtn = createModernMenuButton("Logout");

        currentSelectedButton = dashboardBtn;
        dashboardBtn.setBackground(ACCENT_COLOR);
        dashboardBtn.setForeground(Color.WHITE);

        dashboardBtn.addActionListener(e -> {
            setSelectedButton(dashboardBtn);
            loadDashboardData();
            cardLayout.show(mainPanel, "dashboard");
        });

        eventsBtn.addActionListener(e -> {
            setSelectedButton(eventsBtn);
            loadEventsData();
            cardLayout.show(mainPanel, "events");
        });

        usersBtn.addActionListener(e -> {
            setSelectedButton(usersBtn);
            loadUsersData();
            cardLayout.show(mainPanel, "users");
        });

        analyticsBtn.addActionListener(e -> {
            setSelectedButton(analyticsBtn);
            cardLayout.show(mainPanel, "analytics");
        });

        logoutBtn.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });

        panel.add(dashboardBtn);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(eventsBtn);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(usersBtn);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(analyticsBtn);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(logoutBtn);
        panel.add(Box.createVerticalGlue());

        return panel;
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
        button.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setBackground(PANEL_BACKGROUND);
        button.setForeground(TEXT_COLOR);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        return button;
    }

    private void setSelectedButton(JButton selectedButton) {
        if (currentSelectedButton != null) {
            currentSelectedButton.setBackground(PANEL_BACKGROUND);
            currentSelectedButton.setForeground(TEXT_COLOR);
        }
        currentSelectedButton = selectedButton;
        selectedButton.setBackground(ACCENT_COLOR);
        selectedButton.setForeground(Color.WHITE);
    }

    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PANEL_BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel titleLabel = new JLabel("Dashboard Overview");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_COLOR);

        JPanel statsPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        statsPanel.setBackground(PANEL_BACKGROUND);
        statsPanel.setBorder(BorderFactory.createEmptyBorder(25, 0, 25, 0));

        statsEventsLabel = new JLabel("0", SwingConstants.CENTER);
        statsUsersLabel = new JLabel("0", SwingConstants.CENTER);
        statsRegistrationsLabel = new JLabel("0", SwingConstants.CENTER);
        statsAttendanceLabel = new JLabel("0%", SwingConstants.CENTER);

        statsPanel.add(createModernStatCard("Total Events", statsEventsLabel, ACCENT_COLOR));
        statsPanel.add(createModernStatCard("Total Users", statsUsersLabel, SUCCESS_COLOR));
        statsPanel.add(createModernStatCard("Current Registrations", statsRegistrationsLabel, WARNING_COLOR));
        statsPanel.add(createModernStatCard("Expected Attendance", statsAttendanceLabel, new Color(233, 30, 99)));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(PANEL_BACKGROUND);
        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(statsPanel, BorderLayout.CENTER);

        panel.add(topPanel, BorderLayout.NORTH);

        JPanel activityCard = createModernCard();
        activityCard.setLayout(new BorderLayout());

        JLabel activityTitle = new JLabel("System Status");
        activityTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        activityTitle.setForeground(TEXT_COLOR);
        activityTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        JTextArea activityArea = new JTextArea("System is running smoothly.\nAll events are being managed efficiently.\nAttendance predictions updated in real-time.");
        activityArea.setEditable(false);
        activityArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        activityArea.setForeground(SUBTEXT_COLOR);
        activityArea.setBackground(CARD_BACKGROUND);
        activityArea.setLineWrap(true);
        activityArea.setWrapStyleWord(true);
        activityArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(activityArea);
        scrollPane.setBorder(null);
        scrollPane.setBackground(CARD_BACKGROUND);

        activityCard.add(activityTitle, BorderLayout.NORTH);
        activityCard.add(scrollPane, BorderLayout.CENTER);

        panel.add(activityCard, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createModernStatCard(String title, JLabel valueLabel, Color color) {
        JPanel card = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int shadowSize = 8;
                for (int i = 0; i < shadowSize; i++) {
                    g2.setColor(new Color(0, 0, 0, 10 - i));
                    g2.fillRoundRect(i, i, getWidth() - i * 2, getHeight() - i * 2, 16, 16);
                }

                g2.setColor(color);
                g2.fillRoundRect(shadowSize, shadowSize, getWidth() - shadowSize * 2, getHeight() - shadowSize * 2, 16, 16);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 42));
        valueLabel.setForeground(Color.WHITE);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
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

                g2.setColor(CARD_BACKGROUND);
                g2.fillRoundRect(shadowSize, shadowSize, getWidth() - shadowSize * 2, getHeight() - shadowSize * 2, 16, 16);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        return card;
    }

    private JPanel createEventsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PANEL_BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel titleLabel = new JLabel("Manage Events");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonsPanel.setBackground(PANEL_BACKGROUND);

        JButton deleteBtn = createActionButton("Delete Event", new Color(239, 68, 68));
        JButton exportBtn = createActionButton("Export Data", ACCENT_COLOR);
        JButton refreshBtn = createActionButton("Refresh", SUBTEXT_COLOR);

        deleteBtn.addActionListener(e -> deleteSelectedEvent());
        exportBtn.addActionListener(e -> exportDataToCSV());
        refreshBtn.addActionListener(e -> loadEventsData());

        buttonsPanel.add(deleteBtn);
        buttonsPanel.add(exportBtn);
        buttonsPanel.add(refreshBtn);

        String[] columns = {"ID", "Event Name", "Category", "Date", "Time", "Venue", "Capacity", "Expected Attendance", "Status"};
        eventsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        eventsTable = new JTable(eventsTableModel);
        styleModernTable(eventsTable);

        JScrollPane scrollPane = new JScrollPane(eventsTable);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(CARD_BACKGROUND);

        JPanel tableCard = createModernCard();
        tableCard.setLayout(new BorderLayout());
        tableCard.add(scrollPane, BorderLayout.CENTER);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(PANEL_BACKGROUND);
        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(buttonsPanel, BorderLayout.CENTER);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(tableCard, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createUsersPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PANEL_BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel titleLabel = new JLabel("Manage Users");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonsPanel.setBackground(PANEL_BACKGROUND);

        JButton exportBtn = createActionButton("Export Users", ACCENT_COLOR);
        JButton refreshBtn = createActionButton("Refresh", SUBTEXT_COLOR);

        exportBtn.addActionListener(e -> exportUsersOnly());
        refreshBtn.addActionListener(e -> loadUsersData());

        buttonsPanel.add(exportBtn);
        buttonsPanel.add(refreshBtn);

        String[] columns = {"ID", "Username", "Full Name", "Email", "Role", "Interests"};
        usersTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        usersTable = new JTable(usersTableModel);
        styleModernTable(usersTable);

        JScrollPane scrollPane = new JScrollPane(usersTable);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(CARD_BACKGROUND);

        JPanel tableCard = createModernCard();
        tableCard.setLayout(new BorderLayout());
        tableCard.add(scrollPane, BorderLayout.CENTER);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(PANEL_BACKGROUND);
        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(buttonsPanel, BorderLayout.CENTER);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(tableCard, BorderLayout.CENTER);

        return panel;
    }

    private JButton createActionButton(String text, Color bgColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (getModel().isPressed()) {
                    g2.setColor(bgColor.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(bgColor.brighter());
                } else {
                    g2.setColor(bgColor);
                }

                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                super.paintComponent(g2);
                g2.dispose();
            }
        };

        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return button;
    }

    private void styleModernTable(JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(35);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(59, 130, 246, 50));
        table.setSelectionForeground(TEXT_COLOR);
        table.setBackground(CARD_BACKGROUND);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(PANEL_BACKGROUND);
        header.setForeground(TEXT_COLOR);
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 40));
        header.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                if (row >= 0) {
                    table.setRowSelectionInterval(row, row);
                }
            }
        });
    }

    private void loadDashboardData() {
        statsEventsLabel.setText(String.valueOf(EventDAO.getAllEvents().size()));
        statsUsersLabel.setText(String.valueOf(UserDAO.getAllUsers().size()));
        int totalCurrentRegistrations = RegistrationDAO.getTotalRegistrationCount();
        statsRegistrationsLabel.setText(String.valueOf(totalCurrentRegistrations));
        double attendanceRate = AnalyticsEngine.getHistoricalAttendanceRate();
        statsAttendanceLabel.setText(String.format("%.0f%%", attendanceRate));
    }

    private void loadEventsData() {
        eventsTableModel.setRowCount(0);
        List<Event1> events = EventDAO.getAllEvents();

        for (Event1 event : events) {
            String predictionString;

            if ("COMPLETED".equalsIgnoreCase(event.getStatus())) {
                predictionString = "N/A (Completed)";
            } else {
                AttendancePrediction.PredictionResult result = AttendancePrediction.predictWithConfidence(event);
                predictionString = String.format("%d (%s)",
                        result.getPredictedAttendance(),
                        result.getConfidenceLevel());
            }

            Object[] row = {
                    event.getEventId(),
                    event.getEventName(),
                    event.getCategory(),
                    event.getEventDate(),
                    event.getStartTime(),
                    event.getVenue(),
                    event.getCurrentAttendees() + "/" + event.getMaxAttendees(),
                    predictionString,
                    event.getStatus()
            };
            eventsTableModel.addRow(row);
        }
    }

    private void loadUsersData() {
        usersTableModel.setRowCount(0);
        List<User> users = UserDAO.getAllUsers();

        for (User user : users) {
            Object[] row = {
                    user.getUserId(),
                    user.getUsername(),
                    user.getFullName(),
                    user.getEmail(),
                    user.getRole(),
                    user.getInterests() != null ? user.getInterests() : ""
            };
            usersTableModel.addRow(row);
        }
    }

    private void deleteSelectedEvent() {
        int selectedRow = eventsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an event to delete by clicking on a row.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int eventId = (int) eventsTableModel.getValueAt(selectedRow, 0);
        String eventName = (String) eventsTableModel.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete event: " + eventName + "?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (EventDAO.deleteEvent(eventId)) {
                JOptionPane.showMessageDialog(this, "Event deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadEventsData();
                loadDashboardData();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete event.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // EXPORT FUNCTIONS
    private void exportDataToCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Folder to Export Data");
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int result = fileChooser.showSaveDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) {
            return;
        }

        String folderPath = fileChooser.getSelectedFile().getAbsolutePath();

        boolean usersSuccess = exportUsersToCSV(folderPath);
        boolean eventsSuccess = exportEventsToCSV(folderPath);
        boolean registrationsSuccess = exportRegistrationsToCSV(folderPath);

        if (usersSuccess && eventsSuccess && registrationsSuccess) {
            JOptionPane.showMessageDialog(this,
                    "All data exported successfully!\n\nFolder: " + folderPath,
                    "Export Complete",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Some exports failed. Check console for errors.",
                    "Export Partial",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void exportUsersOnly() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Folder to Export Users");
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int result = fileChooser.showSaveDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) {
            return;
        }

        if (exportUsersToCSV(fileChooser.getSelectedFile().getAbsolutePath())) {
            JOptionPane.showMessageDialog(this, "Users exported successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Export failed!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean exportUsersToCSV(String folderPath) {
        String filePath = folderPath + File.separator + "users_" + System.currentTimeMillis() + ".csv";

        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write("User ID,Username,Full Name,Email,Phone,Role,Interests\n");

            List<User> users = UserDAO.getAllUsers();

            for (User user : users) {
                writer.write(
                        user.getUserId() + "," +
                                escapeCSV(user.getUsername()) + "," +
                                escapeCSV(user.getFullName()) + "," +
                                escapeCSV(user.getEmail()) + "," +
                                escapeCSV(user.getPhone() != null ? user.getPhone() : "") + "," +
                                user.getRole() + "," +
                                escapeCSV(user.getInterests() != null ? user.getInterests() : "") + "\n"
                );
            }

            System.out.println("Users exported to: " + filePath);
            return true;

        } catch (IOException e) {
            System.err.println("Error exporting users: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private boolean exportEventsToCSV(String folderPath) {
        String filePath = folderPath + File.separator + "events_" + System.currentTimeMillis() + ".csv";

        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write("Event ID,Event Name,Category,Date,Start Time,Venue,Max Attendees,Current Attendees,Status,Organizer\n");

            List<Event1> events = EventDAO.getAllEvents();

            for (Event1 event : events) {
                writer.write(
                        event.getEventId() + "," +
                                escapeCSV(event.getEventName()) + "," +
                                event.getCategory() + "," +
                                event.getEventDate() + "," +
                                event.getStartTime() + "," +
                                escapeCSV(event.getVenue()) + "," +
                                event.getMaxAttendees() + "," +
                                event.getCurrentAttendees() + "," +
                                event.getStatus() + "," +
                                escapeCSV(event.getOrganizerName() != null ? event.getOrganizerName() : "") + "\n"
                );
            }

            System.out.println("Events exported to: " + filePath);
            return true;

        } catch (IOException e) {
            System.err.println("Error exporting events: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private boolean exportRegistrationsToCSV(String folderPath) {
        String filePath = folderPath + File.separator + "registrations_" + System.currentTimeMillis() + ".csv";

        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write("User ID,User Name,Event ID,Event Name,Registration Date,Attendance Status\n");

            List<User> users = UserDAO.getAllUsers();

            for (User user : users) {
                List<Registration> regs = RegistrationDAO.getUserRegistrations(user.getUserId());
                for (Registration reg : regs) {
                    writer.write(
                            reg.getUserId() + "," +
                                    escapeCSV(reg.getUserName()) + "," +
                                    reg.getEventId() + "," +
                                    escapeCSV(reg.getEventName()) + "," +
                                    reg.getRegistrationDate() + "," +
                                    reg.getAttendanceStatus() + "\n"
                    );
                }
            }

            System.out.println("Registrations exported to: " + filePath);
            return true;

        } catch (IOException e) {
            System.err.println("Error exporting registrations: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Helper method to escape CSV special characters
    private String escapeCSV(String value) {
        if (value == null) return "";

        // If contains comma, quote, or newline, wrap in quotes and escape quotes
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    public static void main(String[] args) {
        System.setProperty("awt.useSystemAAFontSettings","on");
        System.setProperty("swing.aatext", "true");

        SwingUtilities.invokeLater(() -> {
            User admin = new User();
            admin.setUserId(1);
            admin.setUsername("admin");
            admin.setFullName("System Administrator");
            admin.setRole("ADMIN");

            new AdminDashboard(admin);
        });
    }
}