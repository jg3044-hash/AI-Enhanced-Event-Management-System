import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class Theme {
    public static final Color ACCENT_BLUE = new Color(59, 130, 246);
    public static final Color SIDEBAR_DARK = new Color(44, 51, 58);
    public static final Color BACKGROUND_LIGHT_GRAY = new Color(243, 244, 246);
    public static final Color TEXT_DARK = new Color(40, 40, 40);
    public static final Color BORDER_GRAY = new Color(220, 220, 220);

    public static void stylePrimaryButton(JButton btn) {
        btn.setBackground(ACCENT_BLUE);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBorder(new EmptyBorder(8, 15, 8, 15));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    public static void styleSecondaryButton(JButton btn) {
        btn.setBackground(Color.WHITE);
        btn.setForeground(TEXT_DARK);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_GRAY),
                new EmptyBorder(7, 14, 7, 14)
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}