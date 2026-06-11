import javax.swing.*;
import java.awt.*;
import java.io.File;

public class OCRPanel extends JPanel {

    JTextField imagePathField;
    JTextArea resultArea;

    public OCRPanel() {
        setLayout(null);
        setBackground(new Color(18, 18, 22));

        JButton backBtn = UIHelper.neonButton("← Back");
        backBtn.setBounds(950, 45, 120, 38);
        add(backBtn);

        backBtn.addActionListener(e -> {
            Window window = SwingUtilities.getWindowAncestor(this);

            if (window instanceof MainDashboard dashboard) {
                dashboard.showHome();
            }
        });

        JLabel title = UIHelper.title("OCR AI Scanner");
        title.setBounds(80, 50, 500, 45);
        add(title);

        JLabel sub = UIHelper.subtitle("Extract text from ID cards, documents, and item images using Python EasyOCR.");
        sub.setBounds(80, 100, 850, 30);
        add(sub);

        JPanel card = UIHelper.glassPanel();
        card.setBounds(80, 170, 820, 450);
        add(card);

        JLabel pathLabel = new JLabel("Selected Image");
        pathLabel.setForeground(Color.WHITE);
        pathLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        pathLabel.setBounds(50, 35, 200, 25);
        card.add(pathLabel);

        imagePathField = UIHelper.input();
        imagePathField.setBounds(50, 70, 520, 38);
        card.add(imagePathField);

        JButton browseBtn = UIHelper.neonButton("Browse Image");
        browseBtn.setBounds(590, 70, 170, 38);
        card.add(browseBtn);

        JButton scanBtn = UIHelper.neonButton("Run OCR AI");
        scanBtn.setBounds(300, 135, 200, 42);
        card.add(scanBtn);

        JLabel resultLabel = new JLabel("OCR Result");
        resultLabel.setForeground(Color.WHITE);
        resultLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        resultLabel.setBounds(50, 200, 200, 25);
        card.add(resultLabel);

        resultArea = new JTextArea();
        resultArea.setBackground(new Color(45, 45, 55));
        resultArea.setForeground(Color.WHITE);
        resultArea.setCaretColor(Color.WHITE);
        resultArea.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);

        JScrollPane scroll = new JScrollPane(resultArea);
        scroll.setBounds(50, 230, 710, 170);
        card.add(scroll);

        browseBtn.addActionListener(e -> chooseImage());
        scanBtn.addActionListener(e -> runOCR());
    }

    void chooseImage() {
        JFileChooser chooser = new JFileChooser();

        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File selected = chooser.getSelectedFile();
            imagePathField.setText(selected.getAbsolutePath());
        }
    }

    void runOCR() {
        String path = imagePathField.getText().trim();

        if (path.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Choose image first");
            return;
        }

        resultArea.setText("Running OCR AI...\nPlease wait.");

        String result = OCRService.performOCR(path);
        String cleanedText = OCRService.cleanOCRText(result);

        if (cleanedText.startsWith("OCR failed") || cleanedText.startsWith("OCR_ERROR")) {
            resultArea.setText(
                    "OCR Scan Failed\n\n" +
                    "Reason:\n" +
                    cleanedText + "\n\n" +
                    "Tip: Select a valid .jpg, .jpeg, or .png image file."
            );
        } else {
            resultArea.setText(
                    "OCR Scan Successful\n\n" +
                    "Extracted Text:\n\n" +
                    cleanedText
            );
        }
    }
}