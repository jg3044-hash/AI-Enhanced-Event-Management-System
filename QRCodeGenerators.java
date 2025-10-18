import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class QRCodeGenerators {

    /**
     * Generates a QR code image from the given data
     *
     * @param data The text/data to encode in the QR code
     * @param width The width of the QR code image in pixels
     * @param height The height of the QR code image in pixels
     * @return BufferedImage containing the QR code
     */
    public static BufferedImage generateQRCode(String data, int width, int height) {
        try {
            // Create QR code writer
            QRCodeWriter qrCodeWriter = new QRCodeWriter();

            // Set encoding hints for better quality
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            hints.put(EncodeHintType.MARGIN, 1); // Border margin

            // Generate QR code bit matrix
            BitMatrix bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, width, height, hints);

            // Create BufferedImage
            BufferedImage qrImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

            // Convert bit matrix to image
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    // Set pixel color: black for 1, white for 0
                    qrImage.setRGB(x, y, bitMatrix.get(x, y) ? Color.BLACK.getRGB() : Color.WHITE.getRGB());
                }
            }

            System.out.println("QR Code generated successfully!");
            System.out.println("Data encoded: " + data);
            System.out.println("Dimensions: " + width + "x" + height);

            return qrImage;

        } catch (WriterException e) {
            System.err.println("Error generating QR code: " + e.getMessage());
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Generates a QR code with custom colors
     *
     * @param data The text/data to encode in the QR code
     * @param width The width of the QR code image in pixels
     * @param height The height of the QR code image in pixels
     * @param foregroundColor Color for the QR code pattern
     * @param backgroundColor Background color
     * @return BufferedImage containing the QR code
     */
    public static BufferedImage generateQRCode(String data, int width, int height,
                                               Color foregroundColor, Color backgroundColor) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();

            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            hints.put(EncodeHintType.MARGIN, 1);

            BitMatrix bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, width, height, hints);

            BufferedImage qrImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    qrImage.setRGB(x, y, bitMatrix.get(x, y) ?
                            foregroundColor.getRGB() : backgroundColor.getRGB());
                }
            }

            System.out.println("Colored QR Code generated successfully!");
            return qrImage;

        } catch (WriterException e) {
            System.err.println("Error generating QR code: " + e.getMessage());
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Saves the QR code image to a file
     *
     * @param qrImage The BufferedImage containing the QR code
     * @param filepath The complete file path including filename and extension (e.g., "qrcode.png")
     * @return true if saved successfully, false otherwise
     */
    public static boolean saveQRCode(BufferedImage qrImage, String filepath) {
        try {
            if (qrImage == null) {
                System.err.println("Error: QR code image is null");
                return false;
            }

            File outputFile = new File(filepath);

            // Create parent directories if they don't exist
            if (outputFile.getParentFile() != null) {
                outputFile.getParentFile().mkdirs();
            }

            // Determine image format from file extension
            String format = "png";
            if (filepath.toLowerCase().endsWith(".jpg") || filepath.toLowerCase().endsWith(".jpeg")) {
                format = "jpg";
            } else if (filepath.toLowerCase().endsWith(".bmp")) {
                format = "bmp";
            }

            // Save the image
            boolean success = ImageIO.write(qrImage, format, outputFile);

            if (success) {
                System.out.println("QR Code saved successfully to: " + filepath);
                System.out.println("File size: " + outputFile.length() + " bytes");
            } else {
                System.err.println("Failed to save QR code to: " + filepath);
            }

            return success;

        } catch (IOException e) {
            System.err.println("IO Error saving QR code: " + e.getMessage());
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            System.err.println("Unexpected error saving QR code: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Saves QR code with a specific format
     *
     * @param qrImage The BufferedImage containing the QR code
     * @param filepath The file path
     * @param format The image format ("png", "jpg", "bmp")
     * @return true if saved successfully, false otherwise
     */
    public static boolean saveQRCode(BufferedImage qrImage, String filepath, String format) {
        try {
            if (qrImage == null) {
                System.err.println("Error: QR code image is null");
                return false;
            }

            File outputFile = new File(filepath);

            if (outputFile.getParentFile() != null) {
                outputFile.getParentFile().mkdirs();
            }

            boolean success = ImageIO.write(qrImage, format, outputFile);

            if (success) {
                System.out.println("QR Code saved as " + format.toUpperCase() + " to: " + filepath);
            }

            return success;

        } catch (IOException e) {
            System.err.println("IO Error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Test main method
    public static void main(String[] args) {
        System.out.println("=== QR Code Generator Test ===\n");

        // Test 1: Generate simple QR code
        String eventData = "Event Registration\n" +
                "Event: Tech Conference 2025\n" +
                "Date: 2025-11-15\n" +
                "Attendee: John Doe\n" +
                "Ticket ID: TCK-2025-001";

        BufferedImage qrCode1 = generateQRCode(eventData, 400, 400);
        if (qrCode1 != null) {
            saveQRCode(qrCode1, "event_qr_code.png");
        }

        System.out.println("\n--- Test 2: Colored QR Code ---");

        // Test 2: Generate colored QR code
        String registrationData = "REGISTRATION-ID-12345";
        BufferedImage qrCode2 = generateQRCode(registrationData, 300, 300,
                new Color(33, 150, 243), Color.WHITE);
        if (qrCode2 != null) {
            saveQRCode(qrCode2, "colored_qr_code.png");
        }

        System.out.println("\n--- Test 3: Different Format ---");

        // Test 3: Save as JPG
        String urlData = "https://eventmanagement.com/verify?id=12345";
        BufferedImage qrCode3 = generateQRCode(urlData, 350, 350);
        if (qrCode3 != null) {
            saveQRCode(qrCode3, "qr_code.jpg", "jpg");
        }

        System.out.println("\n=== All tests completed! ===");
    }
}


