import javax.swing.*;
import java.awt.*;

public class StudentDashboard extends JFrame {

    int userId;

    public StudentDashboard(int userId) {

        this.userId = userId;

        getContentPane().setBackground(Theme.BACKGROUND);

        setTitle("RecoverAI Dashboard");

        setSize(500, 450);

        setLayout(null);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLocationRelativeTo(null);

        JLabel title =
                new JLabel("RecoverAI Student Portal");

        title.setFont(Theme.TITLE_FONT);

        title.setForeground(Theme.TEXT);

        title.setBounds(110, 25, 320, 35);

        add(title);

        JButton lostBtn =
                createButton("Report Lost Item", 90);

        JButton foundBtn =
                createButton("Report Found Item", 145);

        JButton matchBtn =
                createButton("Smart AI Match", 200);

        JButton ocrBtn =
                createButton("OCR Smart ID", 255);

        JButton logoutBtn =
                createButton("Logout", 310);

        lostBtn.addActionListener(
                e -> new ReportLostFrame(userId));

        foundBtn.addActionListener(
                e -> new ReportFoundFrame(userId));

        matchBtn.addActionListener(
                e -> new MatchFrame(userId));

        ocrBtn.addActionListener(
                e -> new OCRFrame());

        logoutBtn.addActionListener(e -> {

            dispose();

            new LoginFrame();
        });

        setVisible(true);
    }

    JButton createButton(String text, int y) {

        JButton btn = new JButton(text);

        btn.setBounds(140, y, 200, 38);

        btn.setBackground(Theme.PRIMARY);

        btn.setForeground(Color.WHITE);

        btn.setFocusPainted(false);

        btn.setFont(Theme.BUTTON_FONT);

        add(btn);

        return btn;
    }
}