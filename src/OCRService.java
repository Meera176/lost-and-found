import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class OCRService {

    public static String performOCR(String imagePath) {

        try {
            if (imagePath == null || imagePath.trim().isEmpty()) {
                return "OCR_ERROR: No image selected";
            }

            imagePath = imagePath.replace("\"", "").trim();

            URL url = new URL("http://127.0.0.1:5000/ocr");

            HttpURLConnection con =
                    (HttpURLConnection) url.openConnection();

            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);

            String jsonInput =
                    "{\"image_path\":\"" +
                    imagePath.replace("\\", "\\\\") +
                    "\"}";

            try (OutputStream os = con.getOutputStream()) {
                byte[] input = jsonInput.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            BufferedReader br =
                    new BufferedReader(
                            new InputStreamReader(
                                    con.getInputStream(),
                                    "utf-8"
                            )
                    );

            StringBuilder response = new StringBuilder();
            String responseLine;

            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }

            return response.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return "OCR_ERROR: Could not connect to OCR service or invalid image path";
        }
    }

    public static String cleanOCRText(String response) {

        if (response == null) {
            return "OCR failed. No response received.";
        }

        if (response.startsWith("OCR_ERROR")) {
            return response;
        }

        if (response.contains("\"success\":true")) {
            String cleaned = response
                    .replace("{\"success\":true,\"text\":\"", "")
                    .replace("\"}", "")
                    .replace("\\n", "\n");

            return cleaned;
        }

        return "OCR failed. Please select a valid image file.";
    }
}