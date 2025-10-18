import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.List;

/**
 * AI CHATBOT WINDOW - Google Gemini Integration (FIXED VERSION)
 * Beautiful modern UI with real AI responses and better error handling
 */
public class AIChatbotWindow extends JFrame {

    private User currentUser;
    private JPanel chatPanel;
    private JScrollPane scrollPane;
    private JTextField inputField;
    private JButton sendButton;

    // Google Gemini API
    private static final String GEMINI_API_KEY = getApiKey();
    private static final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent";

    private static String getApiKey() {
        // Try environment variable first
        String key = System.getenv("GEMINI_API_KEY");
        if (key != null && !key.isEmpty()) {
            return key;
        }

        // Try system property (set in IDE run configuration)
        key = System.getProperty("GEMINI_API_KEY");
        if (key != null && !key.isEmpty()) {
            return key;
        }

        return null;
    }

    // Colors
    private static final Color BACKGROUND_COLOR = new Color(243, 244, 246);
    private static final Color USER_BUBBLE_COLOR = new Color(59, 130, 246);
    private static final Color AI_BUBBLE_COLOR = Color.WHITE;
    private static final Color TEXT_COLOR = new Color(30, 30, 30);

    public AIChatbotWindow(User user) {
        this.currentUser = user;
        initializeUI();
    }

    private void initializeUI() {
        setTitle(" AI Event Assistant - Powered by Google Gemini");
        setSize(500, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(BACKGROUND_COLOR);

        JPanel header = createHeader();

        chatPanel = new JPanel();
        chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.Y_AXIS));
        chatPanel.setBackground(BACKGROUND_COLOR);
        chatPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        scrollPane = new JScrollPane(chatPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        JPanel inputPanel = createInputPanel();

        mainContainer.add(header, BorderLayout.NORTH);
        mainContainer.add(scrollPane, BorderLayout.CENTER);
        mainContainer.add(inputPanel, BorderLayout.SOUTH);

        setContentPane(mainContainer);

        // Welcome message
        addAIMessage("Hi " + currentUser.getFullName() + "! How can I help you with your events today?");

        setVisible(true);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(59, 130, 246));
        header.setBorder(new EmptyBorder(20, 25, 20, 25));

        JLabel title = new JLabel("* AI Assistant");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel("Powered by Google Gemini");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitle.setForeground(new Color(220, 220, 220));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        textPanel.add(title);
        textPanel.add(subtitle);

        JButton closeBtn = new JButton("✕");
        closeBtn.setFont(new Font("Segoe UI", Font.BOLD, 18));
        closeBtn.setForeground(Color.WHITE);
        closeBtn.setBackground(new Color(255, 255, 255, 30));
        closeBtn.setBorderPainted(false);
        closeBtn.setFocusPainted(false);
        closeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeBtn.setPreferredSize(new Dimension(40, 40));
        closeBtn.addActionListener(e -> dispose());

        header.add(textPanel, BorderLayout.WEST);
        header.add(closeBtn, BorderLayout.EAST);

        return header;
    }

    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(15, 20, 20, 20));

        inputField = new JTextField();
        inputField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        inputField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
                new EmptyBorder(12, 15, 12, 15)
        ));

        sendButton = new JButton("Send");
        sendButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        sendButton.setBackground(new Color(59, 130, 246));
        sendButton.setForeground(Color.WHITE);
        sendButton.setBorderPainted(false);
        sendButton.setFocusPainted(false);
        sendButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        sendButton.setPreferredSize(new Dimension(80, 45));

        ActionListener sendAction = e -> sendMessage();
        sendButton.addActionListener(sendAction);
        inputField.addActionListener(sendAction);

        panel.add(inputField, BorderLayout.CENTER);
        panel.add(sendButton, BorderLayout.EAST);

        return panel;
    }

    private void sendMessage() {
        String message = inputField.getText().trim();
        if (message.isEmpty()) return;

        addUserMessage(message);
        inputField.setText("");

        inputField.setEnabled(false);
        sendButton.setEnabled(false);
        sendButton.setText("...");

        SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() {
                return getAIResponse(message);
            }

            @Override
            protected void done() {
                try {
                    String response = get();
                    addAIMessage(response != null && !response.isEmpty() ? response :
                            "Sorry, I couldn't generate a response. Please try again.");
                } catch (Exception e) {
                    addAIMessage("Error: " + e.getMessage());
                } finally {
                    inputField.setEnabled(true);
                    sendButton.setEnabled(true);
                    sendButton.setText("Send");
                    inputField.requestFocus();
                }
            }
        };
        worker.execute();
    }

    private String getAIResponse(String userMessage) {
        try {
            // Check if API key exists
            if (GEMINI_API_KEY == null || GEMINI_API_KEY.isEmpty()) {
                System.err.println("ERROR: GEMINI_API_KEY not found!");
                System.err.println("Please set it in IntelliJ: Run → Edit Configurations → Environment variables");
                return " API Key not configured. Please check the console for instructions.";
            }

            System.out.println("API Key found, length: " + GEMINI_API_KEY.length());
            String context = buildEventContext();
            String fullPrompt = context + "\n\nUser question: " + userMessage;
            System.out.println("Calling Gemini API...");
            String response = callGeminiAPI(fullPrompt);
            System.out.println("API Response received: " + response.substring(0, Math.min(50, response.length())));
            return response;

        } catch (Exception e) {
            System.err.println("API Error: " + e.getMessage());
            e.printStackTrace();
            return "❌ Error: " + e.getMessage();
        }
    }

    private String callGeminiAPI(String prompt) throws Exception {
        URL url = new URL(GEMINI_API_URL + "?key=" + GEMINI_API_KEY);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        try {
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            conn.setConnectTimeout(15000);
            conn.setReadTimeout(15000);

            String jsonRequest = buildGeminiRequest(prompt);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonRequest.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();

            if (responseCode == 200) {
                String response = readResponseStream(conn.getInputStream());
                return parseGeminiResponse(response);
            } else {
                String errorMsg = readResponseStream(conn.getErrorStream());
                throw new Exception("API Error " + responseCode + ": " + errorMsg);
            }

        } finally {
            conn.disconnect();
        }
    }

    private String buildGeminiRequest(String prompt) throws Exception {
        JSONObject request = new JSONObject();
        JSONArray contents = new JSONArray();
        JSONObject content = new JSONObject();
        JSONArray parts = new JSONArray();
        JSONObject part = new JSONObject();

        part.put("text", prompt);
        parts.put(part);
        content.put("parts", parts);
        contents.put(content);
        request.put("contents", contents);

        return request.toString();
    }

    private String readResponseStream(InputStream stream) throws IOException {
        StringBuilder response = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(stream, "utf-8"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line.trim());
            }
        }
        return response.toString();
    }

    private String parseGeminiResponse(String jsonResponse) throws Exception {
        JSONObject resp = new JSONObject(jsonResponse);

        if (resp.has("error")) {
            JSONObject error = resp.getJSONObject("error");
            throw new Exception(error.getString("message"));
        }

        if (resp.has("candidates") && resp.getJSONArray("candidates").length() > 0) {
            JSONObject candidate = resp.getJSONArray("candidates").getJSONObject(0);
            if (candidate.has("content")) {
                JSONObject contentObj = candidate.getJSONObject("content");
                if (contentObj.has("parts") && contentObj.getJSONArray("parts").length() > 0) {
                    return contentObj.getJSONArray("parts").getJSONObject(0).getString("text").trim();
                }
            }
        }

        throw new Exception("Unexpected API response format");
    }

    private String buildEventContext() {
        StringBuilder context = new StringBuilder("You are a helpful event management AI assistant.\n");
        context.append("Current user: ").append(currentUser.getFullName()).append(", Role: ").append(currentUser.getRole()).append("\n\n");

        try {
            // Get user's registered events
            List<Event1> userEvents = RegistrationDAO.getUserRegisteredEvents(currentUser.getUserId());
            context.append("USER'S REGISTERED EVENTS (").append(userEvents.size()).append("):\n");
            if (userEvents.isEmpty()) {
                context.append("- None yet\n");
            } else {
                for (Event1 e : userEvents) {
                    context.append("- ").append(e.getEventName()).append(" on ").append(e.getEventDate())
                            .append(" at ").append(e.getVenue()).append("\n");
                }
            }

            // Get upcoming events
            List<Event1> upcoming = EventDAO.getUpcomingEvents();
            context.append("\nAVAILABLE UPCOMING EVENTS (").append(upcoming.size()).append("):\n");
            if (!upcoming.isEmpty()) {
                for (int i = 0; i < Math.min(5, upcoming.size()); i++) {
                    Event1 e = upcoming.get(i);
                    int seatsLeft = e.getMaxAttendees() - e.getCurrentAttendees();
                    context.append("- ").append(e.getEventName()).append(" | ").append(e.getCategory())
                            .append(" | Date: ").append(e.getEventDate()).append(" | Seats: ").append(seatsLeft)
                            .append(" | Price: $").append(e.getPrice()).append("\n");
                }
            } else {
                context.append("- No upcoming events\n");
            }

            context.append("\nBe helpful, friendly, and provide specific information from this data when answering questions.\n");

        } catch (Exception e) {
            System.err.println("Context error: " + e.getMessage());
            context.append("(Database connection issue - using fallback mode)\n");
        }

        return context.toString();
    }

    private String getLocalAIResponse(String message) {
        message = message.toLowerCase();

        if (message.contains("hello") || message.contains("hi")) {
            return "Hello! I'm your AI event assistant. How can I help you today?";
        }

        if (message.contains("help")) {
            return "I can assist with:\n• Event recommendations\n• Finding popular events\n• Registration questions\n• Event information\n\nWhat would you like to know?";
        }

        if (message.contains("event")) {
            return "I can help you find and register for events! Would you like recommendations or information about specific events?";
        }

        return "I'm here to help with event management! Try asking me about recommendations, conflicts, or specific events.";
    }

    private void addUserMessage(String message) {
        JPanel messagePanel = createMessageBubble(message, true);
        chatPanel.add(messagePanel);
        chatPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        scrollToBottom();
    }

    private void addAIMessage(String message) {
        JPanel messagePanel = createMessageBubble(message, false);
        chatPanel.add(messagePanel);
        chatPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        scrollToBottom();
    }

    private JPanel createMessageBubble(String text, boolean isUser) {
        JPanel container = new JPanel(new BorderLayout());
        container.setOpaque(false);
        container.setMaximumSize(new Dimension(450, Integer.MAX_VALUE));
        container.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextArea textArea = new JTextArea(text);
        textArea.setWrapStyleWord(true);
        textArea.setLineWrap(true);
        textArea.setEditable(false);
        textArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textArea.setBackground(isUser ? USER_BUBBLE_COLOR : AI_BUBBLE_COLOR);
        textArea.setForeground(isUser ? Color.WHITE : TEXT_COLOR);
        textArea.setBorder(new EmptyBorder(12, 15, 12, 15));

        int width = 320;
        textArea.setSize(width, Short.MAX_VALUE);
        Dimension preferredSize = textArea.getPreferredSize();
        textArea.setPreferredSize(new Dimension(width, preferredSize.height));

        JPanel bubble = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (!isUser) {
                    g2.setColor(new Color(0, 0, 0, 20));
                    g2.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 18, 18);
                }

                g2.setColor(isUser ? USER_BUBBLE_COLOR : AI_BUBBLE_COLOR);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                g2.dispose();
            }
        };
        bubble.setOpaque(false);
        bubble.add(textArea);

        if (isUser) {
            container.add(bubble, BorderLayout.EAST);
        } else {
            container.add(bubble, BorderLayout.WEST);
        }

        return container;
    }

    private void scrollToBottom() {
        SwingUtilities.invokeLater(() -> {
            chatPanel.revalidate();
            chatPanel.repaint();
            JScrollBar vertical = scrollPane.getVerticalScrollBar();
            vertical.setValue(vertical.getMaximum());
        });
    }
}