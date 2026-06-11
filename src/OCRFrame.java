import javax.swing.*;
import java.io.File;

public class OCRFrame extends JFrame {

    JTextField imagePathField;
    JTextArea resultArea;

    public OCRFrame() {
        setTitle("RecoverAI - Real OCR Smart Identification");
        setSize(650, 430);
        setLayout(null);
        setLocationRelativeTo(null);

        JLabel title = new JLabel("Real OCR Smart Identification");
        title.setBounds(220, 20, 250, 30);
        add(title);

        JLabel imageLabel = new JLabel("Image Path:");
        imageLabel.setBounds(40, 80, 100, 25);
        add(imageLabel);

        imagePathField = new JTextField();
        imagePathField.setBounds(140, 80, 350, 25);
        add(imagePathField);

        JButton browseBtn = new JButton("Browse");
        browseBtn.setBounds(500, 80, 100, 25);
        add(browseBtn);

        JButton scanBtn = new JButton("Run Real OCR");
        scanBtn.setBounds(240, 130, 160, 30);
        add(scanBtn);

        resultArea = new JTextArea();
        JScrollPane scroll = new JScrollPane(resultArea);
        scroll.setBounds(50, 190, 540, 140);
        add(scroll);

        browseBtn.addActionListener(e -> chooseImage());
        scanBtn.addActionListener(e -> runOCR());

        setVisible(true);
    }

    void chooseImage() {
        JFileChooser chooser = new JFileChooser();

        int result = chooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = chooser.getSelectedFile();
            imagePathField.setText(selectedFile.getAbsolutePath());
        }
    }

    void runOCR() {
        String imagePath = imagePathField.getText();

        if (imagePath.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Choose image first");
            return;
        }

        resultArea.setText("Running OCR... Please wait.");

        String result = OCRService.performOCR(imagePath);

        resultArea.setText("OCR API Response:\n" + result);
    }
}