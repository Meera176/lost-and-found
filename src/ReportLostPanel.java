import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.sql.*;

public class ReportLostPanel extends JPanel {

    JTextField nameField, categoryField, colorField, brandField, locationField, dateField, imageField;
    JTextArea descArea;
    int userId;

    public ReportLostPanel(int userId) {
        this.userId = userId;

        setLayout(null);
        setBackground(new Color(18, 18, 22));

        JButton backBtn = UIHelper.neonButton("← Back");
        backBtn.setBounds(900, 45, 120, 38);
        add(backBtn);

        backBtn.addActionListener(e -> {
            Window window = SwingUtilities.getWindowAncestor(this);

            if (window instanceof MainDashboard dashboard) {
                dashboard.showHome();
            }
        });

        JLabel title = UIHelper.title("Report Lost Asset");
        title.setBounds(80, 40, 500, 45);
        add(title);

        JLabel sub = UIHelper.subtitle("Submit detailed information so RecoverAI can match faster.");
        sub.setBounds(80, 90, 700, 30);
        add(sub);

        JPanel card = UIHelper.glassPanel();
        card.setBounds(80, 150, 700, 520);
        add(card);

        nameField = addInput(card, "Item Name", 40, 40);
        categoryField = addInput(card, "Category", 370, 40);
        colorField = addInput(card, "Color", 40, 120);
        brandField = addInput(card, "Brand", 370, 120);
        locationField = addInput(card, "Lost Location", 40, 200);
        dateField = addInput(card, "Lost Date YYYY-MM-DD", 370, 200);

        JLabel descLabel = label("Description");
        descLabel.setBounds(40, 280, 200, 25);
        card.add(descLabel);

        descArea = new JTextArea();
        descArea.setBackground(new Color(45,45,55));
        descArea.setForeground(Color.WHITE);
        descArea.setCaretColor(Color.WHITE);

        JScrollPane scroll = new JScrollPane(descArea);
        scroll.setBounds(40, 310, 620, 80);
        card.add(scroll);

        imageField = UIHelper.input();
        imageField.setBounds(40, 420, 430, 35);
        card.add(imageField);

        JButton browse = UIHelper.neonButton("Browse Image");
        browse.setBounds(490, 420, 170, 35);
        card.add(browse);

        JButton submit = UIHelper.neonButton("Submit Lost Report");
        submit.setBounds(230, 470, 220, 38);
        card.add(submit);

        browse.addActionListener(e -> chooseImage());
        submit.addActionListener(e -> saveLostItem());
    }

    JTextField addInput(JPanel card, String text, int x, int y) {
        JLabel l = label(text);
        l.setBounds(x, y, 220, 25);
        card.add(l);

        JTextField f = UIHelper.input();
        f.setBounds(x, y + 30, 290, 35);
        card.add(f);

        return f;
    }

    JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(Color.WHITE);
        l.setFont(new Font("Segoe UI", Font.BOLD, 14));
        return l;
    }

    void chooseImage() {
        JFileChooser chooser = new JFileChooser();

        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File selected = chooser.getSelectedFile();
            imageField.setText(selected.getAbsolutePath());
        }
    }

    void saveLostItem() {
        try {
            Connection con = DBConnection.getConnection();

            String ocrText =
                    imageField.getText().isEmpty()
                            ? ""
                            : OCRService.performOCR(imageField.getText());

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

            checkAndSendMatchEmail(con, ocrText);

            JOptionPane.showMessageDialog(
                    this,
                    "Lost report submitted with OCR intelligence."
            );

        } catch (Exception e) {
            e.printStackTrace();

            JOptionPane.showMessageDialog(
                    this,
                    "Error saving lost report."
            );
        }
    }

    void checkAndSendMatchEmail(Connection con, String lostOCR) {

    try {

        String sql = """
            SELECT
                f.user_id,
                f.item_name,
                f.description,
                f.category,
                f.color,
                f.brand,
                f.location_found,
                f.ocr_text,
                u.email

            FROM found_items f

            JOIN users u
            ON f.user_id = u.user_id
        """;

        PreparedStatement pst =
                con.prepareStatement(sql);

        ResultSet rs = pst.executeQuery();

        while (rs.next()) {

            int score =
                    MatchingEngine.calculateMatchScore(

                            nameField.getText(),
                            rs.getString("item_name"),

                            descArea.getText(),
                            rs.getString("description"),

                            categoryField.getText(),
                            rs.getString("category"),

                            colorField.getText(),
                            rs.getString("color"),

                            brandField.getText(),
                            rs.getString("brand"),

                            locationField.getText(),
                            rs.getString("location_found"),

                            lostOCR,
                            rs.getString("ocr_text")
                    );

            System.out.println(
                    "ACTUAL MATCH SCORE = " + score
            );

            if (score >= 70) {

                String receiverEmail =
                        rs.getString("email");

                System.out.println(
                        "Sending email to: "
                                + receiverEmail
                );

                EmailService.sendMatchEmail(
                        receiverEmail,
                        nameField.getText(),
                        score
                );

                JOptionPane.showMessageDialog(
                        this,

                        "Possible match found!\n\n" +

                        "Email sent to:\n" +
                        receiverEmail +

                        "\n\nMatch Score: "
                        + score + "%"
                );

                break;
            }
        }

    } catch (Exception e) {

        e.printStackTrace();
    }
}
}