import javax.swing.*;
import java.sql.*;
import java.io.File;

public class ReportFoundFrame extends JFrame {

    JTextField nameField, categoryField, colorField, brandField;
    JTextField locationField, dateField, imageField;
    JTextArea descArea;

    int userId;

    public ReportFoundFrame(int userId) {

        this.userId = userId;

        setTitle("RecoverAI - Report Found Item");
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

        addLabel("Found Location:", 40, 310);
        locationField = addTextField(200, 310);

        addLabel("Found Date:", 40, 350);
        dateField = addTextField(200, 350);

        addLabel("Image Path:", 40, 390);
        imageField = addTextField(200, 390);

        JButton browseBtn = new JButton("Browse Image");
        browseBtn.setBounds(200, 430, 150, 30);
        add(browseBtn);

        JButton submitBtn = new JButton("Submit Found Report");
        submitBtn.setBounds(180, 500, 180, 35);
        add(submitBtn);

        browseBtn.addActionListener(e -> chooseImage());
        submitBtn.addActionListener(e -> saveFoundItem());

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

    void saveFoundItem() {

        try {

            Connection con = DBConnection.getConnection();
String ocrText = OCRService.performOCR(imageField.getText());

String sql = """
    INSERT INTO found_items
    (user_id, item_name, category, description,
     location_found, date_found,
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

            JOptionPane.showMessageDialog(this,
                    "Found Item Report Submitted");

            dispose();

        } catch (Exception ex) {

            ex.printStackTrace();

            JOptionPane.showMessageDialog(this,
                    "Error saving found item");
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