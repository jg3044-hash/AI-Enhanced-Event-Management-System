import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.io.IOException;
import javax.imageio.ImageIO;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;

    // Theme Colors
    private static final Color ACCENT_COLOR = new Color(59, 130, 246);
    private static final Color TEXT_COLOR = new Color(30, 30, 30);
    private static final Color SUBTEXT_COLOR = new Color(80, 80, 80);
    private static final Color PANEL_BACKGROUND = new Color(243, 244, 246);

    // Animation variables
    private Timer animationTimer;
    private float textAlpha = 0.0f;
    private final JPanel visualPanel;

    public LoginFrame() {
        setTitle("Event Sphere - Login");
        setSize(950, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new GridLayout(1, 2));

        visualPanel = createVisualPanel();
        add(visualPanel);
        add(createFormPanel());

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                startAnimation();
            }
        });
    }

    private JPanel createVisualPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(0, 0, new Color(55, 100, 180), 0, getHeight(), new Color(40, 70, 140));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, textAlpha));
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 42));
                g2d.setColor(Color.WHITE);
                FontMetrics fm = g2d.getFontMetrics();
                int titleWidth = fm.stringWidth("Event Sphere");
                int titleX = (getWidth() - titleWidth) / 2;
                int titleY = getHeight() / 2 - 20;
                g2d.drawString("Event Sphere", titleX, titleY);

                g2d.setFont(new Font("Segoe UI Light", Font.PLAIN, 18));
                g2d.setColor(new Color(220, 220, 220));
                fm = g2d.getFontMetrics();
                int subtitleWidth = fm.stringWidth("Your Events, Seamlessly Managed.");
                int subtitleX = (getWidth() - subtitleWidth) / 2;
                g2d.drawString("Your Events, Seamlessly Managed.", subtitleX, titleY + 40);
            }
        };
        panel.setLayout(new GridBagLayout());
        return panel;
    }

    private void startAnimation() {
        animationTimer = new Timer(20, e -> {
            textAlpha += 0.05f;
            if (textAlpha >= 1.0f) {
                textAlpha = 1.0f;
                animationTimer.stop();
            }
            visualPanel.repaint();
        });
        animationTimer.start();
    }

    private JPanel createFormPanel() {
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBackground(PANEL_BACKGROUND);

        JPanel cardPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int shadowSize = 10; int cornerRadius = 16;
                int x = shadowSize; int y = shadowSize;
                int w = getWidth() - (shadowSize * 2); int h = getHeight() - (shadowSize * 2);
                for (int i = 0; i < shadowSize; i++) {
                    g2.setColor(new Color(0, 0, 0, 15 - i));
                    g2.fillRoundRect(x, y + i, w, h, cornerRadius, cornerRadius);
                }
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(x, y, w, h, cornerRadius, cornerRadius);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        cardPanel.setOpaque(false);
        cardPanel.setPreferredSize(new Dimension(400, 550));
        cardPanel.setBorder(new EmptyBorder(12, 12, 12, 12));

        JPanel formContentPanel = new JPanel(new GridBagLayout());
        formContentPanel.setOpaque(false);
        formContentPanel.setBorder(new EmptyBorder(30, 40, 30, 40));
        cardPanel.add(formContentPanel, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 0, 8, 0);
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Welcome Back!");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.insets = new Insets(0, 0, 5, 0);
        formContentPanel.add(titleLabel, gbc);

        JLabel subtitleLabel = new JLabel("Please enter your details to sign in.");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(SUBTEXT_COLOR);
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.insets = new Insets(0, 0, 30, 0);
        formContentPanel.add(subtitleLabel, gbc);

        gbc.insets = new Insets(15, 0, 5, 0);
        JLabel userLabel = new JLabel("Username");
        styleLabel(userLabel);
        formContentPanel.add(userLabel, gbc);

        gbc.insets = new Insets(0, 0, 10, 0);
        usernameField = new RoundTextField();
        formContentPanel.add(usernameField, gbc);

        gbc.insets = new Insets(10, 0, 5, 0);
        JLabel passLabel = new JLabel("Password");
        styleLabel(passLabel);
        formContentPanel.add(passLabel, gbc);

        gbc.insets = new Insets(0, 0, 5, 0);
        JPanel passwordContainer = createPasswordContainer();
        formContentPanel.add(passwordContainer, gbc);

        gbc.insets = new Insets(0, 0, 10, 0);
        gbc.anchor = GridBagConstraints.LINE_END;
        gbc.fill = GridBagConstraints.NONE;
        JButton forgotPasswordLink = createLinkButton("Forgot Password?");
        forgotPasswordLink.addActionListener(e ->
                JOptionPane.showMessageDialog(this, "Password recovery instructions would be sent to your email.")
        );
        formContentPanel.add(forgotPasswordLink, gbc);

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(20, 0, 15, 0);
        loginButton = new RoundButton("Sign In");
        stylePrimaryButton(loginButton);
        formContentPanel.add(loginButton, gbc);

        JPanel registerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        registerPanel.setOpaque(false);
        JLabel noAccountLabel = new JLabel("Don't have an account?");
        noAccountLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        noAccountLabel.setForeground(SUBTEXT_COLOR);
        JButton registerLink = createLinkButton("Sign Up");

        registerLink.addActionListener(e -> {
            new RegisterFrame().setVisible(true);
            dispose();
        });

        registerPanel.add(noAccountLabel);
        registerPanel.add(registerLink);
        formContentPanel.add(registerPanel, gbc);

        addListeners();
        rightPanel.add(cardPanel);
        return rightPanel;
    }

    private void addListeners() {
        loginButton.addActionListener(e -> handleLogin());
        passwordField.addActionListener(e -> loginButton.doClick());
    }

    private JPanel createPasswordContainer() {
        RoundContainerPanel container = new RoundContainerPanel();
        container.setLayout(new BorderLayout());

        passwordField = new JPasswordField();
        passwordField.setOpaque(false);
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setBorder(new EmptyBorder(8, 12, 8, 5));
        passwordField.setBackground(Color.WHITE);

        passwordField.addFocusListener(container.getFocusListener());

        JToggleButton toggleButton = createPasswordToggle();

        container.add(passwordField, BorderLayout.CENTER);
        container.add(toggleButton, BorderLayout.EAST);
        return container;
    }

    private JToggleButton createPasswordToggle() {
        JToggleButton toggleButton = new JToggleButton();
        try {
            Image eyeOpen = ImageIO.read(getClass().getResource("/resources/eye_open.png")).getScaledInstance(20, 20, Image.SCALE_SMOOTH);
            Image eyeClosed = ImageIO.read(getClass().getResource("/resources/eye_closed.png")).getScaledInstance(20, 20, Image.SCALE_SMOOTH);
            toggleButton.setIcon(new ImageIcon(eyeClosed));
            toggleButton.setSelectedIcon(new ImageIcon(eyeOpen));
        } catch (IOException | IllegalArgumentException e) {
            toggleButton.setText("👁");
        }
        toggleButton.setBorder(new EmptyBorder(0, 5, 0, 10));
        toggleButton.setContentAreaFilled(false);
        toggleButton.setFocusPainted(false);
        toggleButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        toggleButton.addActionListener(e -> {
            if (toggleButton.isSelected()) {
                passwordField.setEchoChar((char) 0);
            } else {
                passwordField.setEchoChar('•');
            }
        });
        return toggleButton;
    }

    private void handleLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        User user = UserDAO.login(username, password);
        if (user != null) {
            JOptionPane.showMessageDialog(this, "Login Successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
            Main.launchDashboard(user);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid username or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void styleLabel(JLabel label) {
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(SUBTEXT_COLOR);
    }

    private JButton createLinkButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(ACCENT_COLOR);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        return button;
    }

    private void stylePrimaryButton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(ACCENT_COLOR);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(12, 18, 12, 18));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    class RoundTextField extends JTextField {
        private Color borderColor = new Color(220, 220, 220);
        public RoundTextField() {
            setOpaque(false);
            setFont(new Font("Segoe UI", Font.PLAIN, 14));
            setBorder(new EmptyBorder(8, 12, 8, 12));
            setBackground(Color.WHITE);
            addFocusListener(new FocusAdapter() {
                public void focusGained(FocusEvent e) { borderColor = ACCENT_COLOR; repaint(); }
                public void focusLost(FocusEvent e) { borderColor = new Color(220, 220, 220); repaint(); }
            });
        }
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
            super.paintComponent(g);
            g2.setColor(borderColor);
            g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 12, 12));
            g2.dispose();
        }
    }

    class RoundContainerPanel extends JPanel {
        private Color borderColor = new Color(220, 220, 220);
        public RoundContainerPanel() { setOpaque(false); setBackground(Color.WHITE); }
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
            g2.setColor(borderColor);
            g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 12, 12));
            g2.dispose();
        }
        public FocusListener getFocusListener() {
            return new FocusAdapter() {
                public void focusGained(FocusEvent e) { borderColor = ACCENT_COLOR; repaint(); }
                public void focusLost(FocusEvent e) { borderColor = new Color(220, 220, 220); repaint(); }
            };
        }
    }

    class RoundButton extends JButton {
        public RoundButton(String text) { super(text); setContentAreaFilled(false); }
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
            super.paintComponent(g2);
            g2.dispose();
        }
    }

    public static void main(String[] args) {
        System.setProperty("awt.useSystemAAFontSettings","on");
        System.setProperty("swing.aatext", "true");
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}