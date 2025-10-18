import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

public class AttendeeDashboard extends JFrame {

    private User currentUser;
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private JTable eventsTable, registrationsTable;
    private DefaultTableModel eventsModel, registrationsModel;
    private JButton currentSelectedButton = null;

    // Card containers for events
    private JPanel browseCardsContainer;

    // Profile Sidebar
    private JPanel profileSidebar;
    private boolean profileVisible = false;
    private Timer slideTimer;

    // Modern Theme Colors
    private static final Color ACCENT_COLOR = new Color(59, 130, 246);
    private static final Color GRADIENT_START = new Color(55, 100, 180);
    private static final Color GRADIENT_END = new Color(40, 70, 140);
    private static final Color TEXT_COLOR = new Color(30, 30, 30);
    private static final Color SUBTEXT_COLOR = new Color(80, 80, 80);
    private static final Color PANEL_BACKGROUND = new Color(243, 244, 246);
    private static final Color CARD_BACKGROUND = Color.WHITE;
    private static final Color SUCCESS_COLOR = new Color(76, 175, 80);

    public AttendeeDashboard(User user) {
        this.currentUser = user;
        ThemeManager.loadThemePreference();
        initializeUI();
        ThemeManager.applyModernTheme(this);
    }

    private void initializeUI() {
        setTitle("Event Explorer - Attendee Dashboard");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Use JLayeredPane for overlay effect
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(1200, 700));

        JPanel container = new JPanel(new BorderLayout());
        container.setBounds(0, 0, 1200, 700);
        container.setBackground(PANEL_BACKGROUND);
        container.add(createModernHeader(), BorderLayout.NORTH);
        container.add(createModernSidebar(), BorderLayout.WEST);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(PANEL_BACKGROUND);
        mainPanel.add(createBrowsePanel(), "browse");
        mainPanel.add(createRegistrationsPanel(), "registrations");

        container.add(mainPanel, BorderLayout.CENTER);

        // Profile Sidebar (initially hidden off-screen)
        profileSidebar = createProfileSidebar();
        profileSidebar.setBounds(1200, 70, 350, 630);

        layeredPane.add(container, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(profileSidebar, JLayeredPane.PALETTE_LAYER);

        setContentPane(layeredPane);
        setVisible(true);
        loadAllEvents();
    }

    private JPanel createModernHeader() {
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

        // Left side
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setOpaque(false);

        JLabel title = new JLabel("Event Explorer");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        String displayName = currentUser.getFullName();
        if (displayName == null || displayName.trim().isEmpty()) {
            displayName = currentUser.getUsername();
        }

        JLabel welcome = new JLabel("Welcome, " + displayName);
        welcome.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        welcome.setForeground(new Color(220, 220, 220));
        welcome.setAlignmentX(Component.LEFT_ALIGNMENT);

        leftPanel.add(title);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        leftPanel.add(welcome);

        // Right side panel with buttons
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setOpaque(false);

        JButton themeBtn = new JButton(ThemeManager.isDarkMode() ? "→" : "←");
        themeBtn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        themeBtn.setBackground(new Color(255, 255, 255, 30));
        themeBtn.setForeground(Color.WHITE);
        themeBtn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        themeBtn.setFocusPainted(false);
        themeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        themeBtn.addActionListener(e -> {
            ThemeManager.toggleTheme();
            ThemeManager.applyModernTheme(this);
            themeBtn.setText(ThemeManager.isDarkMode() ? "→" : "←");
        });

        JButton profileBtn = new JButton("Profile");
        profileBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        profileBtn.setBackground(new Color(255, 255, 255, 40));
        profileBtn.setForeground(Color.WHITE);
        profileBtn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        profileBtn.setFocusPainted(false);
        profileBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        profileBtn.addActionListener(e -> toggleProfileSidebar());

        rightPanel.add(themeBtn);
        rightPanel.add(profileBtn);

        panel.add(leftPanel, BorderLayout.WEST);
        panel.add(rightPanel, BorderLayout.EAST);
        return panel;
    }

    private JPanel createModernSidebar() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(CARD_BACKGROUND);
        panel.setPreferredSize(new Dimension(220, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 15, 30, 15));

        JButton browseBtn = createModernMenuButton("» Browse Events");
        JButton registrationsBtn = createModernMenuButton("» My Registrations");
        JButton chatbotBtn = createModernMenuButton("» Chatbot");
        JButton logoutBtn = createModernMenuButton("» Logout");

        currentSelectedButton = browseBtn;
        browseBtn.setBackground(ACCENT_COLOR);
        browseBtn.setForeground(Color.WHITE);

        browseBtn.addActionListener(e -> {
            setSelectedButton(browseBtn);
            loadAllEvents();
            cardLayout.show(mainPanel, "browse");
        });

        registrationsBtn.addActionListener(e -> {
            setSelectedButton(registrationsBtn);
            loadMyRegistrations();
            cardLayout.show(mainPanel, "registrations");
        });

        chatbotBtn.addActionListener(e -> showChatbot());

        logoutBtn.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to logout?",
                    "Confirm Logout",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
            if (choice == JOptionPane.YES_OPTION) {
                dispose();
                new LoginFrame().setVisible(true);
            }
        });

        panel.add(browseBtn);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(registrationsBtn);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(chatbotBtn);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(logoutBtn);
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    private JPanel createProfileSidebar() {
        JPanel sidebar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Shadow on left edge
                for (int i = 0; i < 15; i++) {
                    g2.setColor(new Color(0, 0, 0, 15 - i));
                    g2.drawLine(i, 0, i, getHeight());
                }

                g2.setColor(CARD_BACKGROUND);
                g2.fillRect(15, 0, getWidth() - 15, getHeight());
                g2.dispose();
            }
        };
        sidebar.setLayout(null);
        sidebar.setOpaque(false);

        // Close button
        JButton closeBtn = new JButton("✕");
        closeBtn.setBounds(310, 10, 30, 30);
        closeBtn.setFont(new Font("Segoe UI", Font.BOLD, 18));
        closeBtn.setForeground(SUBTEXT_COLOR);
        closeBtn.setBackground(Color.WHITE);
        closeBtn.setBorderPainted(false);
        closeBtn.setFocusPainted(false);
        closeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeBtn.addActionListener(e -> toggleProfileSidebar());
        sidebar.add(closeBtn);

        // Profile Header
        JLabel profileTitle = new JLabel("My Profile");
        profileTitle.setBounds(30, 20, 200, 35);
        profileTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        profileTitle.setForeground(TEXT_COLOR);
        sidebar.add(profileTitle);

        // Avatar Circle
        JPanel avatarPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ACCENT_COLOR);
                g2.fillOval(0, 0, 80, 80);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 32));

                String fullName = currentUser.getFullName();
                if (fullName == null || fullName.isEmpty()) {
                    fullName = currentUser.getUsername();
                }
                String initial = fullName.substring(0, 1).toUpperCase();
                FontMetrics fm = g2.getFontMetrics();
                int x = (80 - fm.stringWidth(initial)) / 2;
                int y = ((80 - fm.getHeight()) / 2) + fm.getAscent();
                g2.drawString(initial, x, y);
                g2.dispose();
            }
        };
        avatarPanel.setBounds(135, 80, 80, 80);
        avatarPanel.setOpaque(false);
        sidebar.add(avatarPanel);

        // Full Name
        String displayName = currentUser.getFullName();
        if (displayName == null || displayName.trim().isEmpty()) {
            displayName = currentUser.getUsername();
        }

        JLabel nameLabel = new JLabel(displayName);
        nameLabel.setBounds(30, 180, 290, 30);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        nameLabel.setForeground(TEXT_COLOR);
        nameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        sidebar.add(nameLabel);

        // Username
        JLabel usernameLabel = new JLabel("@" + currentUser.getUsername());
        usernameLabel.setBounds(30, 210, 290, 25);
        usernameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        usernameLabel.setForeground(SUBTEXT_COLOR);
        usernameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        sidebar.add(usernameLabel);

        int yPos = 260;

        // Info Section
        JLabel infoTitle = new JLabel("Account Information");
        infoTitle.setBounds(30, yPos, 290, 25);
        infoTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        infoTitle.setForeground(SUBTEXT_COLOR);
        sidebar.add(infoTitle);
        yPos += 35;

        // Email
        addInfoRow(sidebar, "Email", currentUser.getEmail(), yPos);
        yPos += 50;

        // Phone (if available)
        String phone = currentUser.getPhone() != null ? currentUser.getPhone() : "Not provided";
        addInfoRow(sidebar, "Phone", phone, yPos);
        yPos += 70;

        // Change Password Button
        JButton changePassBtn = createProfileButton("Change Password");
        changePassBtn.setBounds(30, yPos, 290, 45);
        changePassBtn.addActionListener(e -> showChangePasswordDialog());
        sidebar.add(changePassBtn);
        yPos += 60;

        // Logout Button
        JButton logoutBtn = createProfileButton("Logout");
        logoutBtn.setBounds(30, yPos, 290, 45);
        logoutBtn.setBackground(new Color(239, 68, 68));
        logoutBtn.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to logout?",
                    "Confirm Logout",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            if (choice == JOptionPane.YES_OPTION) {
                dispose();
                new LoginFrame().setVisible(true);
            }
        });
        sidebar.add(logoutBtn);

        return sidebar;
    }

    private void addInfoRow(JPanel parent, String label, String value, int y) {
        JLabel lblLabel = new JLabel(label);
        lblLabel.setBounds(30, y, 100, 20);
        lblLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblLabel.setForeground(SUBTEXT_COLOR);
        parent.add(lblLabel);

        JLabel valLabel = new JLabel(value);
        valLabel.setBounds(30, y + 18, 290, 25);
        valLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        valLabel.setForeground(TEXT_COLOR);
        parent.add(valLabel);
    }

    private JButton createProfileButton(String text) {
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

        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(ACCENT_COLOR);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return button;
    }

    private void toggleProfileSidebar() {
        if (slideTimer != null && slideTimer.isRunning()) return;

        profileVisible = !profileVisible;
        int startX = profileSidebar.getX();
        int endX = profileVisible ? 850 : 1200;
        int duration = 300;
        int steps = 20;
        int delay = duration / steps;
        int deltaX = (endX - startX) / steps;

        slideTimer = new Timer(delay, null);
        slideTimer.addActionListener(new ActionListener() {
            int step = 0;
            @Override
            public void actionPerformed(ActionEvent e) {
                step++;
                int newX = startX + (deltaX * step);
                profileSidebar.setLocation(newX, profileSidebar.getY());

                if (step >= steps) {
                    slideTimer.stop();
                    profileSidebar.setLocation(endX, profileSidebar.getY());
                }
            }
        });
        slideTimer.start();
    }

    private void showChangePasswordDialog() {
        JDialog dialog = new JDialog(this, "Change Password", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(CARD_BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 0);

        JLabel title = new JLabel("Change Password");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        panel.add(title, gbc);

        JLabel oldLabel = new JLabel("Current Password:");
        panel.add(oldLabel, gbc);
        JPasswordField oldPass = new JPasswordField();
        panel.add(oldPass, gbc);

        JLabel newLabel = new JLabel("New Password:");
        panel.add(newLabel, gbc);
        JPasswordField newPass = new JPasswordField();
        panel.add(newPass, gbc);

        JLabel confirmLabel = new JLabel("Confirm Password:");
        panel.add(confirmLabel, gbc);
        JPasswordField confirmPass = new JPasswordField();
        panel.add(confirmPass, gbc);

        JButton saveBtn = createProfileButton("Change Password");
        saveBtn.addActionListener(e -> {
            String oldPwd = new String(oldPass.getPassword());
            String newPwd = new String(newPass.getPassword());
            String confPwd = new String(confirmPass.getPassword());

            if (!newPwd.equals(confPwd)) {
                JOptionPane.showMessageDialog(dialog, "New passwords don't match!");
                return;
            }

            if (UserDAO.changePassword(currentUser.getUserId(), oldPwd, newPwd)) {
                JOptionPane.showMessageDialog(dialog, "Password changed successfully!");
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "Failed! Check current password.");
            }
        });
        panel.add(saveBtn, gbc);

        dialog.add(panel);
        dialog.setVisible(true);
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

    private JPanel createBrowsePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PANEL_BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel title = new JLabel("Browse All Events");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(TEXT_COLOR);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // Cards container with vertical layout
        browseCardsContainer = new JPanel();
        browseCardsContainer.setLayout(new BoxLayout(browseCardsContainer, BoxLayout.Y_AXIS));
        browseCardsContainer.setBackground(PANEL_BACKGROUND);

        JScrollPane scrollPane = new JScrollPane(browseCardsContainer);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(PANEL_BACKGROUND);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(PANEL_BACKGROUND);
        topPanel.add(title, BorderLayout.NORTH);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createRegistrationsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PANEL_BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel title = new JLabel("My Registered Events");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(TEXT_COLOR);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JButton cancelBtn = createActionButton("Cancel Registration", new Color(239, 68, 68));
        JButton qrBtn = createActionButton("View QR Ticket", ACCENT_COLOR);

        cancelBtn.addActionListener(e -> cancelRegistration());
        qrBtn.addActionListener(e -> showQRTicket());

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btnPanel.setBackground(PANEL_BACKGROUND);
        btnPanel.add(cancelBtn);
        btnPanel.add(qrBtn);

        String[] columns = {"ID", "Event Name", "Category", "Date", "Time", "Venue"};
        registrationsModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        registrationsTable = new JTable(registrationsModel);
        styleModernTable(registrationsTable);

        JScrollPane scrollPane = new JScrollPane(registrationsTable);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(CARD_BACKGROUND);

        JPanel tableCard = createModernCard();
        tableCard.setLayout(new BorderLayout());
        tableCard.add(scrollPane, BorderLayout.CENTER);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(PANEL_BACKGROUND);
        topPanel.add(title, BorderLayout.NORTH);
        topPanel.add(btnPanel, BorderLayout.CENTER);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(tableCard, BorderLayout.CENTER);
        return panel;
    }

    private void showQRTicket() {
        int row = registrationsTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select an event to view QR ticket",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int eventId = (int) registrationsModel.getValueAt(row, 0);
        Event1 event = EventDAO.getEventById(eventId);

        if (event != null) {
            new QRTicketWindow(currentUser, event);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Failed to load event details!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
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

    private void styleModernTable(JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(40);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(ACCENT_COLOR);
        table.setSelectionForeground(Color.WHITE);
        table.setBackground(CARD_BACKGROUND);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(PANEL_BACKGROUND);
        header.setForeground(TEXT_COLOR);
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 45));
        header.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }

    private void loadAllEvents() {
        browseCardsContainer.removeAll();

        List<Event1> events = EventDAO.getUpcomingEvents();

        if (events == null || events.isEmpty()) {
            JLabel noEventsLabel = new JLabel("No events available at this time.");
            noEventsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            noEventsLabel.setForeground(SUBTEXT_COLOR);
            noEventsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            browseCardsContainer.add(Box.createVerticalGlue());
            browseCardsContainer.add(noEventsLabel);
            browseCardsContainer.add(Box.createVerticalGlue());
            browseCardsContainer.revalidate();
            browseCardsContainer.repaint();
            return;
        }

        for (Event1 e : events) {
            final int eventId = e.getEventId();

            Event event = new Event(
                    e.getEventId(),
                    e.getEventName(),
                    e.getCategory(),
                    e.getDescription(),
                    e.getEventDate().toString(),
                    e.getStartTime().toString(),
                    e.getVenue(),
                    e.getMaxAttendees(),
                    e.getAvailableSeats(),
                    e.getPrice(),
                    e.getOrganizerName() != null ? e.getOrganizerName() : "N/A",
                    "N/A"
            );

            EventCardPanel card = new EventCardPanel(event);

            Component[] components = card.getComponents();
            for (Component comp : components) {
                if (comp instanceof JPanel) {
                    findButtons((JPanel) comp, eventId);
                }
            }

            browseCardsContainer.add(card);
            browseCardsContainer.add(Box.createRigidArea(new Dimension(0, 15)));
        }

        browseCardsContainer.revalidate();
        browseCardsContainer.repaint();
    }

    private void findButtons(JPanel panel, final int eventId) {
        Component[] components = panel.getComponents();
        for (Component comp : components) {
            if (comp instanceof JButton) {
                JButton btn = (JButton) comp;
                if (btn.getText().equals("Register")) {
                    for (ActionListener al : btn.getActionListeners()) {
                        btn.removeActionListener(al);
                    }
                    btn.addActionListener(e -> {
                        Event1 event = EventDAO.getEventById(eventId);
                        CompleteAISystem.ConflictPrediction prediction =
                                CompleteAISystem.predictConflictRisk(currentUser.getUserId(), event);

                        ConflictDetector.ConflictResult result = ConflictDetector.checkConflicts(currentUser.getUserId(), event);
                        String message = result.getMessage();
                        if (prediction.riskLevel.equals("HIGH")) {
                            message += "\n\nAI Conflict Warning: " + prediction.riskLevel + " risk (" +
                                    String.format("%.0f%%", prediction.riskScore * 100) + ")";
                        }

                        if (!result.canRegister() || prediction.riskLevel.equals("HIGH")) {
                            int choice = JOptionPane.showConfirmDialog(AttendeeDashboard.this,
                                    message + "\n\nRegister anyway?",
                                    "Conflict Warning", JOptionPane.YES_NO_OPTION);
                            if (choice != JOptionPane.YES_OPTION) return;
                        }

                        if (RegistrationDAO.registerForEvent(currentUser.getUserId(), eventId)) {
                            CompleteAISystem.AttendancePrediction attendance =
                                    CompleteAISystem.predictAttendance(currentUser.getUserId(), event);

                            JOptionPane.showMessageDialog(AttendeeDashboard.this,
                                    "Registration successful!\n\nAI Prediction: You have " +
                                            String.format("%.0f%%", attendance.probability * 100) +
                                            " likelihood of attending this event.",
                                    "Registration Confirmed",
                                    JOptionPane.INFORMATION_MESSAGE);

                            loadAllEvents();
                            loadMyRegistrations();
                        } else {
                            JOptionPane.showMessageDialog(AttendeeDashboard.this, "Registration failed!");
                        }
                    });
                } else if (btn.getText().equals("Details")) {
                    for (ActionListener al : btn.getActionListeners()) {
                        btn.removeActionListener(al);
                    }
                    btn.addActionListener(e -> {
                        Event1 event = EventDAO.getEventById(eventId);
                        String details = "Event: " + event.getEventName() + "\n" +
                                "Category: " + event.getCategory() + "\n" +
                                "Date: " + event.getEventDate() + "\n" +
                                "Time: " + event.getStartTime() + " - " + event.getEndTime() + "\n" +
                                "Venue: " + event.getVenue() + "\n" +
                                "Description: " + event.getDescription() + "\n" +
                                "Available: " + event.getAvailableSeats() + "/" + event.getMaxAttendees();
                        JOptionPane.showMessageDialog(AttendeeDashboard.this, details, "Event Details", JOptionPane.INFORMATION_MESSAGE);
                    });
                }
            } else if (comp instanceof JPanel) {
                findButtons((JPanel) comp, eventId);
            }
        }
    }

    private void loadMyRegistrations() {
        registrationsModel.setRowCount(0);
        List<Event1> events = RegistrationDAO.getUserRegisteredEvents(currentUser.getUserId());
        for (Event1 e : events) {
            registrationsModel.addRow(new Object[]{
                    e.getEventId(), e.getEventName(), e.getCategory(),
                    e.getEventDate(), e.getStartTime(), e.getVenue()
            });
        }
    }

    private void cancelRegistration() {
        int row = registrationsTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select an event by clicking on a row");
            return;
        }

        int eventId = (int) registrationsModel.getValueAt(row, 0);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to cancel this registration?",
                "Confirm Cancellation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            if (RegistrationDAO.cancelRegistration(currentUser.getUserId(), eventId)) {
                JOptionPane.showMessageDialog(this, "Registration cancelled successfully!");
                loadMyRegistrations();
                loadAllEvents();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to cancel registration!");
            }
        }
    }

    private void showChatbot() {
        new AIChatbotWindow(currentUser);
    }
}