import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.regex.Pattern;

public class RegisterFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JTextField fullNameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JComboBox<String> roleComboBox;
    private JTextField interestsField;
    private JButton registerButton;
    private JButton backButton;

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    public RegisterFrame() {
        setTitle("Event Management System - Register");
        setSize(550, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(59, 130, 246),
                        0, getHeight(), new Color(37, 99, 235)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel();
        formPanel.setBackground(Color.WHITE);
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(25, 40, 25, 40));

        JLabel titleLabel = new JLabel("Create Account");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(59, 130, 246));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.add(titleLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        JLabel subtitleLabel = new JLabel("Fill in your details below");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitleLabel.setForeground(Color.GRAY);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.add(subtitleLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        formPanel.add(createLabel("Full Name"));
        fullNameField = createTextField();
        formPanel.add(fullNameField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        formPanel.add(createLabel("Username"));
        usernameField = createTextField();
        formPanel.add(usernameField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        formPanel.add(createLabel("Email"));
        emailField = createTextField();
        formPanel.add(emailField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        formPanel.add(createLabel("Phone Number"));
        phoneField = createTextField();
        formPanel.add(phoneField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        formPanel.add(createLabel("Password"));
        passwordField = createPasswordField();
        formPanel.add(passwordField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        formPanel.add(createLabel("Confirm Password"));
        confirmPasswordField = createPasswordField();
        formPanel.add(confirmPasswordField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        formPanel.add(createLabel("Role"));
        String[] roles = {"Attendee", "Organizer"};
        roleComboBox = new JComboBox<>(roles);
        roleComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        roleComboBox.setMaximumSize(new Dimension(370, 35));
        roleComboBox.setBackground(Color.WHITE);
        roleComboBox.setCursor(new Cursor(Cursor.HAND_CURSOR));
        roleComboBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.add(roleComboBox);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        formPanel.add(createLabel("Interests (comma-separated)"));
        interestsField = createTextField();
        formPanel.add(interestsField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setMaximumSize(new Dimension(370, 45));

        registerButton = createStyledButton("Register", new Color(59, 130, 246), true);
        backButton = createStyledButton("Back", new Color(229, 231, 235), false);

        buttonPanel.add(registerButton);
        buttonPanel.add(backButton);

        formPanel.add(buttonPanel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setOpaque(false);

        mainPanel.add(scrollPane, BorderLayout.CENTER);

        registerButton.addActionListener(e -> handleRegistration());
        backButton.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });

        add(mainPanel);
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(new Color(55, 65, 81));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    private JTextField createTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setMaximumSize(new Dimension(370, 35));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(209, 213, 219), 1),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
        field.setAlignmentX(Component.CENTER_ALIGNMENT);
        return field;
    }

    private JPasswordField createPasswordField() {
        JPasswordField field = new JPasswordField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setMaximumSize(new Dimension(370, 35));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(209, 213, 219), 1),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
        field.setAlignmentX(Component.CENTER_ALIGNMENT);
        return field;
    }

    private JButton createStyledButton(String text, Color bgColor, boolean isPrimary) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(isPrimary ? Color.WHITE : new Color(55, 65, 81));
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(140, 40));
        button.setMinimumSize(new Dimension(140, 40));
        button.setMaximumSize(new Dimension(140, 40));

        Color hoverColor = isPrimary ? new Color(37, 99, 235) : new Color(209, 213, 219);
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hoverColor);
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    private void handleRegistration() {
        String fullName = fullNameField.getText().trim();
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        String role = (String) roleComboBox.getSelectedItem();
        String interests = interestsField.getText().trim();

        // Validations
        if (fullName.isEmpty() || username.isEmpty() || email.isEmpty() ||
                phone.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "All fields are required!",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this,
                    "Passwords do not match!",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            JOptionPane.showMessageDialog(this,
                    "Invalid email format!",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (password.length() < 6) {
            JOptionPane.showMessageDialog(this,
                    "Password must be at least 6 characters!",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Convert role to uppercase for database (Attendee -> ATTENDEE, Organizer -> ORGANIZER)
        String dbRole = role.toUpperCase();

        // Create User object and save to database
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(password);
        newUser.setFullName(fullName);
        newUser.setEmail(email);
        newUser.setPhone(phone);
        newUser.setRole(dbRole);
        newUser.setInterests(interests);

        // Register user in database
        if (UserDAO.register(newUser)) {
            JOptionPane.showMessageDialog(this,
                    "Registration successful!\nPlease login with your credentials.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            new LoginFrame().setVisible(true);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Registration failed!\nUsername or email may already exist.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RegisterFrame().setVisible(true));
    }
}