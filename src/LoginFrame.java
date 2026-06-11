import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class LoginFrame extends JFrame {

    JTextField emailField;
    JPasswordField passwordField;

    public LoginFrame() {

        setTitle("RecoverAI Login");

        setExtendedState(JFrame.MAXIMIZED_BOTH);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLayout(null);

        getContentPane().setBackground(new Color(10,10,10));

        // LEFT SIDE TITLE
        JLabel title = new JLabel("RecoverAI");

        title.setForeground(Color.CYAN);

        title.setFont(new Font("Segoe UI", Font.BOLD, 48));

        title.setBounds(120, 180, 400, 60);

        add(title);

        JLabel subtitle = new JLabel(
                "AI-Powered Asset Recovery Platform"
        );

        subtitle.setForeground(Color.LIGHT_GRAY);

        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 22));

        subtitle.setBounds(120, 250, 500, 40);

        add(subtitle);

        JLabel feature1 = new JLabel("• Smart AI Matching");
        feature1.setForeground(Color.WHITE);
        feature1.setBounds(140, 360, 300, 30);
        feature1.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        add(feature1);

        JLabel feature2 = new JLabel("• OCR Intelligence");
        feature2.setForeground(Color.WHITE);
        feature2.setBounds(140, 410, 300, 30);
        feature2.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        add(feature2);

        JLabel feature3 = new JLabel("• Fraud Detection");
        feature3.setForeground(Color.WHITE);
        feature3.setBounds(140, 460, 300, 30);
        feature3.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        add(feature3);

        // LOGIN PANEL
        JPanel loginPanel = new JPanel();

        loginPanel.setLayout(null);

        loginPanel.setBackground(new Color(25,25,25));

        loginPanel.setBounds(850, 180, 420, 420);

        add(loginPanel);

        JLabel loginTitle = new JLabel("Secure Login");

        loginTitle.setForeground(Color.CYAN);

        loginTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));

        loginTitle.setBounds(110, 40, 220, 40);

        loginPanel.add(loginTitle);

        JLabel emailLabel = new JLabel("Email");

        emailLabel.setForeground(Color.WHITE);

        emailLabel.setBounds(50, 120, 100, 25);

        loginPanel.add(emailLabel);

        emailField = new JTextField();

        emailField.setBounds(50, 150, 300, 35);

        emailField.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        loginPanel.add(emailField);

        JLabel passLabel = new JLabel("Password");

        passLabel.setForeground(Color.WHITE);

        passLabel.setBounds(50, 210, 100, 25);

        loginPanel.add(passLabel);

        passwordField = new JPasswordField();

        passwordField.setBounds(50, 240, 300, 35);

        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        loginPanel.add(passwordField);

        JButton loginBtn = new JButton("Login");

        loginBtn.setBounds(50, 320, 130, 40);

        styleButton(loginBtn);

        loginPanel.add(loginBtn);

        JButton registerBtn = new JButton("Register");

        registerBtn.setBounds(220, 320, 130, 40);

        styleButton(registerBtn);

        loginPanel.add(registerBtn);

        loginBtn.addActionListener(e -> login());

        registerBtn.addActionListener(e -> new RegisterFrame());

        setVisible(true);
    }

    void styleButton(JButton btn) {

        btn.setBackground(Color.CYAN);

        btn.setForeground(Color.BLACK);

        btn.setFocusPainted(false);

        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
    }

    void login() {

        try {

            Connection con = DBConnection.getConnection();

            String sql =
                    "SELECT * FROM users WHERE email=? AND password=?";

            PreparedStatement pst =
                    con.prepareStatement(sql);

            pst.setString(1, emailField.getText());

            pst.setString(
                    2,
                    String.valueOf(passwordField.getPassword())
            );

            ResultSet rs = pst.executeQuery();

            if (rs.next()) {

                int userId = rs.getInt("user_id");

                String role = rs.getString("role");

                dispose();

                if (role.equals("admin")) {

                    new AdminDashboard();

                } else {

                    new MainDashboard(userId);
                }

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "Invalid Email or Password"
                );
            }

        } catch (Exception ex) {

            ex.printStackTrace();
        }
    }
}