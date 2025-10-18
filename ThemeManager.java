import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.Properties;

public class ThemeManager {

    private static boolean isDarkMode = false;
    private static final String PROPERTIES_FILE = "theme.properties";

    // Light theme colors
    private static final Color LIGHT_BG = new Color(243, 244, 246);
    private static final Color LIGHT_CARD = Color.WHITE;
    private static final Color LIGHT_TEXT = new Color(30, 30, 30);
    private static final Color LIGHT_SUBTEXT = new Color(80, 80, 80);

    // Dark theme colors
    private static final Color DARK_BG = new Color(30, 30, 35);
    private static final Color DARK_CARD = new Color(45, 45, 50);
    private static final Color DARK_TEXT = new Color(240, 240, 240);
    private static final Color DARK_SUBTEXT = new Color(170, 170, 170);

    public static void applyModernTheme(JFrame frame) {
        if (frame == null) return;
        applyThemeToComponent(frame.getContentPane());
        frame.revalidate();
        frame.repaint();
    }

    private static void applyThemeToComponent(Component component) {
        if (component == null) return;

        Color bgColor = isDarkMode ? DARK_BG : LIGHT_BG;
        Color cardColor = isDarkMode ? DARK_CARD : LIGHT_CARD;
        Color textColor = isDarkMode ? DARK_TEXT : LIGHT_TEXT;
        Color subtextColor = isDarkMode ? DARK_SUBTEXT : LIGHT_SUBTEXT;

        // Apply colors based on component type
        if (component instanceof JPanel) {
            JPanel panel = (JPanel) component;
            if (panel.getBackground().equals(LIGHT_BG) || panel.getBackground().equals(DARK_BG)) {
                panel.setBackground(bgColor);
            } else if (panel.getBackground().equals(LIGHT_CARD) || panel.getBackground().equals(DARK_CARD)) {
                panel.setBackground(cardColor);
            }
        }

        if (component instanceof JLabel) {
            JLabel label = (JLabel) component;
            if (label.getForeground().equals(LIGHT_TEXT) || label.getForeground().equals(DARK_TEXT)) {
                label.setForeground(textColor);
            } else if (label.getForeground().equals(LIGHT_SUBTEXT) || label.getForeground().equals(DARK_SUBTEXT)) {
                label.setForeground(subtextColor);
            }
        }

        if (component instanceof JTextField || component instanceof JTextArea) {
            component.setBackground(cardColor);
            component.setForeground(textColor);
        }

        if (component instanceof JTable) {
            JTable table = (JTable) component;
            table.setBackground(cardColor);
            table.setForeground(textColor);
            table.getTableHeader().setBackground(bgColor);
            table.getTableHeader().setForeground(textColor);
        }

        // Recursively apply to children
        if (component instanceof Container) {
            for (Component child : ((Container) component).getComponents()) {
                applyThemeToComponent(child);
            }
        }
    }

    public static void toggleTheme() {
        isDarkMode = !isDarkMode;
        saveThemePreference();
    }

    public static boolean isDarkMode() {
        return isDarkMode;
    }

    public static void loadThemePreference() {
        Properties properties = new Properties();
        try (FileInputStream in = new FileInputStream(PROPERTIES_FILE)) {
            properties.load(in);
            isDarkMode = Boolean.parseBoolean(properties.getProperty("darkMode", "false"));
        } catch (FileNotFoundException e) {
            isDarkMode = false;
        } catch (IOException e) {
            isDarkMode = false;
        }
    }

    private static void saveThemePreference() {
        Properties properties = new Properties();
        properties.setProperty("darkMode", String.valueOf(isDarkMode));
        try (FileOutputStream out = new FileOutputStream(PROPERTIES_FILE)) {
            properties.store(out, "Theme Preferences");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Color getBackgroundColor() {
        return isDarkMode ? DARK_BG : LIGHT_BG;
    }

    public static Color getCardColor() {
        return isDarkMode ? DARK_CARD : LIGHT_CARD;
    }

    public static Color getTextColor() {
        return isDarkMode ? DARK_TEXT : LIGHT_TEXT;
    }

    public static Color getSubtextColor() {
        return isDarkMode ? DARK_SUBTEXT : LIGHT_SUBTEXT;
    }
}