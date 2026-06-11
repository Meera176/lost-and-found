import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class RegisterFrame extends JFrame {

    JTextField nameField, emailField;
    JPasswordField passwordField;

    public RegisterFrame() {
        setTitle("RecoverAI - Register");
        setSize(500, 430);
        setLayout(null);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(18, 18, 22));

        JLabel title = new JLabel("Create Account");
        title.setForeground(Color.CYAN);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setBounds(140, 30, 250, 40);
        add(title);

        JLabel nameLabel = label("Name");
        nameLabel.setBounds(80, 100, 120, 25);
        add(nameLabel);

        nameField = input();
        nameField.setBounds(80, 130, 320, 35);
        add(nameField);

        JLabel emailLabel = label("Email");
        emailLabel.setBounds(80, 175, 120, 25);
        add(emailLabel);

        emailField = input();
        emailField.setBounds(80, 205, 320, 35);
        add(emailField);

        JLabel passLabel = label("Password");
        passLabel.setBounds(80, 250, 120, 25);
        add(passLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(80, 280, 320, 35);
        passwordField.setBackground(new Color(45, 45, 55));
        passwordField.setForeground(Color.WHITE);
        passwordField.setCaretColor(Color.WHITE);
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        add(passwordField);

        JButton registerBtn = new JButton("Register");
        registerBtn.setBounds(160, 340, 160, 38);
        registerBtn.setBackground(Color.CYAN);
        registerBtn.setForeground(Color.BLACK);
        registerBtn.setFocusPainted(false);
        registerBtn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        add(registerBtn);

        registerBtn.addActionListener(e -> registerUser());

        setVisible(true);
    }

    JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(Color.WHITE);
        l.setFont(new Font("Segoe UI", Font.BOLD, 14));
        return l;
    }

    JTextField input() {
        JTextField f = new JTextField();
        f.setBackground(new Color(45, 45, 55));
        f.setForeground(Color.WHITE);
        f.setCaretColor(Color.WHITE);
        f.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        return f;
    }

    void registerUser() {
        if (nameField.getText().isEmpty() ||
                emailField.getText().isEmpty() ||
                String.valueOf(passwordField.getPassword()).isEmpty()) {

            JOptionPane.showMessageDialog(this, "Fill all fields");
            return;
        }

        try {
            Connection con = DBConnection.getConnection();

            String sql = """
                INSERT INTO users(name, email, password, role)
                VALUES (?, ?, ?, 'student')
            """;

            PreparedStatement pst = con.prepareStatement(sql);

            pst.setString(1, nameField.getText());
            pst.setString(2, emailField.getText());
            pst.setString(3, String.valueOf(passwordField.getPassword()));

            pst.executeUpdate();

            JOptionPane.showMessageDialog(this, "Registration successful. Please login.");
            dispose();

        } catch (SQLIntegrityConstraintViolationException e) {
            JOptionPane.showMessageDialog(this, "Email already registered.");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Registration failed. Check terminal.");
        }
    }
}