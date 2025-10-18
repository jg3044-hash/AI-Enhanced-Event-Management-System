import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.print.*;
import java.io.File;

public class QRTicketWindow extends JFrame {

    private User currentUser;
    private Event1 event;
    private BufferedImage qrCodeImage;

    private static final Color ACCENT_COLOR = new Color(59, 130, 246);
    private static final Color SUCCESS_COLOR = new Color(34, 197, 94);
    private static final Color BACKGROUND = new Color(243, 244, 246);
    private static final Color TEXT_COLOR = new Color(30, 30, 30);
    private static final Color SUBTEXT = new Color(107, 114, 128);

    public QRTicketWindow(User user, Event1 event) {
        this.currentUser = user;
        this.event = event;

        // Generate QR code
        String qrData = generateQRData();
        System.out.println("Generating QR with data: " + qrData);
        this.qrCodeImage = QRCodeGenerators.generateQRCode(qrData, 300, 300);
        System.out.println("QR Image generated: " + (qrCodeImage != null));

        initializeUI();
    }

    private String generateQRData() {
        return "REG-" + currentUser.getUserId() + "-" + event.getEventId() + "\n" +
                "Event: " + event.getEventName() + "\n" +
                "Attendee: " + currentUser.getFullName() + "\n" +
                "Date: " + event.getEventDate() + "\n" +
                "Status: CONFIRMED";
    }

    private void initializeUI() {
        setTitle("Event Ticket - " + event.getEventName());
        setSize(600, 850);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(BACKGROUND);
        mainPanel.setBorder(new EmptyBorder(30, 30, 30, 30));

        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setMaximumSize(new Dimension(540, 100));
        headerPanel.setBackground(ACCENT_COLOR);
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("EVENT TICKET");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel idLabel = new JLabel("ID: REG-" + currentUser.getUserId() + "-" + event.getEventId());
        idLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        idLabel.setForeground(new Color(220, 220, 220));
        idLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        headerPanel.add(titleLabel);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        headerPanel.add(idLabel);

        mainPanel.add(headerPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        // QR Code
        if (qrCodeImage != null) {
            JLabel qrLabel = new JLabel(new ImageIcon(qrCodeImage));
            qrLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            qrLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
            mainPanel.add(qrLabel);
        } else {
            JLabel errorLabel = new JLabel("QR Code Failed to Generate");
            errorLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
            errorLabel.setForeground(Color.RED);
            errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            mainPanel.add(errorLabel);
        }

        mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        // Event Details
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setMaximumSize(new Dimension(540, 300));
        detailsPanel.setBackground(Color.WHITE);
        detailsPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        addDetailRow(detailsPanel, "Event Name:", event.getEventName());
        addDetailRow(detailsPanel, "Category:", event.getCategory());
        addDetailRow(detailsPanel, "Date:", event.getEventDate().toString());
        addDetailRow(detailsPanel, "Time:", event.getStartTime().toString());
        addDetailRow(detailsPanel, "Venue:", event.getVenue());
        addDetailRow(detailsPanel, "Attendee:", currentUser.getFullName());

        mainPanel.add(detailsPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Status
        JLabel statusLabel = new JLabel("CONFIRMED");
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        statusLabel.setForeground(SUCCESS_COLOR);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(statusLabel);

        mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setMaximumSize(new Dimension(540, 60));
        buttonPanel.setBackground(BACKGROUND);

        JButton saveBtn = new JButton("Save QR");
        saveBtn.setBackground(ACCENT_COLOR);
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        saveBtn.setFocusPainted(false);
        saveBtn.addActionListener(e -> saveQRCode());

        JButton printBtn = new JButton("Print");
        printBtn.setBackground(new Color(99, 102, 241));
        printBtn.setForeground(Color.WHITE);
        printBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        printBtn.setFocusPainted(false);
        printBtn.addActionListener(e -> printTicket());

        JButton closeBtn = new JButton("Close");
        closeBtn.setBackground(new Color(107, 114, 128));
        closeBtn.setForeground(Color.WHITE);
        closeBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        closeBtn.setFocusPainted(false);
        closeBtn.addActionListener(e -> dispose());

        buttonPanel.add(saveBtn);
        buttonPanel.add(printBtn);
        buttonPanel.add(closeBtn);

        mainPanel.add(buttonPanel);

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(null);
        add(scrollPane);

        setVisible(true);
    }

    private void addDetailRow(JPanel parent, String label, String value) {
        JPanel rowPanel = new JPanel(new BorderLayout(10, 0));
        rowPanel.setMaximumSize(new Dimension(500, 30));
        rowPanel.setBackground(Color.WHITE);

        JLabel lblLabel = new JLabel(label);
        lblLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblLabel.setForeground(SUBTEXT);

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblValue.setForeground(TEXT_COLOR);

        rowPanel.add(lblLabel, BorderLayout.WEST);
        rowPanel.add(lblValue, BorderLayout.CENTER);

        parent.add(rowPanel);
        parent.add(Box.createRigidArea(new Dimension(0, 10)));
    }

    private void saveQRCode() {
        if (qrCodeImage == null) {
            JOptionPane.showMessageDialog(this, "QR Code not available!");
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File("ticket_" + event.getEventId() + ".png"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            String filepath = fileChooser.getSelectedFile().getAbsolutePath();
            if (!filepath.endsWith(".png")) filepath += ".png";

            if (QRCodeGenerators.saveQRCode(qrCodeImage, filepath)) {
                JOptionPane.showMessageDialog(this, "Saved to: " + filepath);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to save!");
            }
        }
    }

    private void printTicket() {
        if (qrCodeImage == null) {
            JOptionPane.showMessageDialog(this, "QR Code not available!");
            return;
        }

        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPrintable((graphics, pageFormat, pageIndex) -> {
            if (pageIndex > 0) return Printable.NO_SUCH_PAGE;

            Graphics2D g2d = (Graphics2D) graphics;
            g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
            double scale = Math.min(
                    pageFormat.getImageableWidth() / getWidth(),
                    pageFormat.getImageableHeight() / getHeight()
            ) * 0.8;
            g2d.scale(scale, scale);
            printAll(g2d);

            return Printable.PAGE_EXISTS;
        });

        if (job.printDialog()) {
            try {
                job.print();
                JOptionPane.showMessageDialog(this, "Sent to printer!");
            } catch (PrinterException e) {
                JOptionPane.showMessageDialog(this, "Print failed: " + e.getMessage());
            }
        }
    }
}