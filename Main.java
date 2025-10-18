import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Main entry point for the AI-Enhanced Event Management System.
 * Initializes the application with a modern UI theme and a splash screen.
 */
public class Main {

    public static void main(String[] args) {
        // Set FlatLaf modern look and feel for the entire application
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());

            // Optional: Customize UI properties for a more polished look
            UIManager.put("Button.arc", 12);
            UIManager.put("Component.arc", 12);
            UIManager.put("ProgressBar.arc", 12);
            UIManager.put("TextComponent.arc", 12);

        } catch (Exception e) {
            System.err.println("Failed to initialize FlatLaf theme. Using default.");
            e.printStackTrace();
        }

        // Launch the application on the Event Dispatch Thread (best practice for Swing)
        SwingUtilities.invokeLater(Main::showModernSplashScreen);
    }

    /**
     * Shows a modern, themed splash screen while the application initializes.
     */
    private static void showModernSplashScreen() {
        JWindow splash = new JWindow();

        // A custom panel with a gradient background, matching the LoginFrame
        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(0, 0, new Color(55, 100, 180), getWidth(), getHeight(), new Color(40, 70, 140));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panel.setBorder(new EmptyBorder(40, 40, 40, 40));

        JLabel titleLabel = new JLabel("Event Sphere", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);

        JLabel subtitleLabel = new JLabel("AI-Enhanced Event Management", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(220, 220, 220));

        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setString("Loading Application...");
        progressBar.setStringPainted(true);
        progressBar.setBorder(null);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(subtitleLabel, BorderLayout.CENTER);
        panel.add(progressBar, BorderLayout.SOUTH);

        splash.setContentPane(panel);
        splash.pack();
        splash.setSize(450, 200);
        splash.setLocationRelativeTo(null);
        splash.setVisible(true);

        // Simulate loading time (2 seconds)
        Timer timer = new Timer(2000, e -> {
            splash.dispose();
            showLoginScreen();
        });
        timer.setRepeats(false);
        timer.start();
    }

    /**
     * Shows the main LoginFrame. This is now the only way to log in.
     */
    private static void showLoginScreen() {
        // We now directly create an instance of your beautiful LoginFrame
        new LoginFrame().setVisible(true);
    }

    /**
     * This public method is called by the LoginFrame after a user successfully logs in.
     * It launches the appropriate dashboard based on the user's role.
     *
     * @param user The successfully authenticated User object.
     */
    public static void launchDashboard(User user) {
        if (user == null || user.getRole() == null) {
            JOptionPane.showMessageDialog(null, "Cannot launch dashboard: invalid user data.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        SwingUtilities.invokeLater(() -> {
            // Replaced reflection with direct, safer instantiation
            switch (user.getRole().toUpperCase()) {
                case "ADMIN":
                    new AdminDashboard(user).setVisible(true);
                    break;
                case "ORGANIZER":
                    new OrganizerDashboard(user).setVisible(true);
                    break;
                case "ATTENDEE":
                    new AttendeeDashboard(user).setVisible(true);
                    break;
                default:
                    JOptionPane.showMessageDialog(null, "Unknown user role: " + user.getRole(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}