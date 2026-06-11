import javax.swing.*;
import java.sql.*;
import java.io.File;

public class ReportLostFrame extends JFrame {

    JTextField nameField, categoryField, colorField, brandField;
    JTextField locationField, dateField, imageField;
    JTextArea descArea;
    int userId;

    public ReportLostFrame(int userId) {

        this.userId = userId;

        setTitle("RecoverAI - Report Lost Item");
        setSize(550, 620);
        setLayout(null);
        setLocationRelativeTo(null);

        addLabel("Item Name:", 40, 40);
        nameField = addTextField(200, 40);

        addLabel("Category:", 40, 80);
        categoryField = addTextField(200, 80);

        addLabel("Color:", 40, 120);
        colorField = addTextField(200, 120);

        addLabel("Brand:", 40, 160);
        brandField = addTextField(200, 160);

        addLabel("Description:", 40, 200);
        descArea = new JTextArea();
        JScrollPane scroll = new JScrollPane(descArea);
        scroll.setBounds(200, 200, 250, 80);
        add(scroll);

        addLabel("Lost Location:", 40, 310);
        locationField = addTextField(200, 310);

        addLabel("Lost Date:", 40, 350);
        dateField = addTextField(200, 350);

        addLabel("Image Path:", 40, 390);
        imageField = addTextField(200, 390);

        JButton browseBtn = new JButton("Browse Image");
        browseBtn.setBounds(200, 430, 150, 30);
        add(browseBtn);

        JButton submitBtn = new JButton("Submit Lost Report");
        submitBtn.setBounds(180, 500, 180, 35);
        add(submitBtn);

        browseBtn.addActionListener(e -> chooseImage());
        submitBtn.addActionListener(e -> saveLostItem());

        setVisible(true);
    }

    void chooseImage() {
        JFileChooser chooser = new JFileChooser();
        int result = chooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = chooser.getSelectedFile();
            imageField.setText(selectedFile.getAbsolutePath());
        }
    }

    void saveLostItem() {

        try {
            Connection con = DBConnection.getConnection();

            boolean duplicate = DuplicateDetector.isDuplicateLostItem(
                    userId,
                    nameField.getText(),
                    colorField.getText(),
                    brandField.getText(),
                    locationField.getText()
            );

            if (duplicate) {
                int choice = JOptionPane.showConfirmDialog(
                        this,
                        "Possible duplicate lost report detected.\nDo you still want to submit?",
                        "Duplicate Warning",
                        JOptionPane.YES_NO_OPTION
                );

                if (choice != JOptionPane.YES_OPTION) {
                    return;
                }
            }

           String ocrText = OCRService.performOCR(imageField.getText());

String sql = """
    INSERT INTO lost_items
    (user_id, item_name, category, description,
     location_lost, date_lost,
     color, brand, image_path, ocr_text)

    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
""";

            PreparedStatement pst = con.prepareStatement(sql);

            pst.setInt(1, userId);
            pst.setString(2, nameField.getText());
            pst.setString(3, categoryField.getText());
            pst.setString(4, descArea.getText());
            pst.setString(5, locationField.getText());
            pst.setString(6, dateField.getText());
            pst.setString(7, colorField.getText());
            pst.setString(8, brandField.getText());
            pst.setString(9, imageField.getText());
            pst.setString(10, ocrText);
            pst.executeUpdate();

            JOptionPane.showMessageDialog(this, "Lost Item Report Submitted");
            dispose();

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving lost item");
        }
    }

    void addLabel(String text, int x, int y) {
        JLabel label = new JLabel(text);
        label.setBounds(x, y, 150, 25);
        add(label);
    }

    JTextField addTextField(int x, int y) {
        JTextField field = new JTextField();
        field.setBounds(x, y, 250, 25);
        add(field);
        return field;
    }
}