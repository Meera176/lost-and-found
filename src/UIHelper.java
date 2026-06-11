import javax.swing.*;
import java.awt.*;

public class UIHelper {

    public static JButton neonButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(new Color(0, 220, 255));
        btn.setForeground(Color.BLACK);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder());
        return btn;
    }

    public static JLabel title(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Segoe UI", Font.BOLD, 34));
        return label;
    }

    public static JLabel subtitle(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(new Color(190, 190, 190));
        label.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        return label;
    }

    public static JPanel glassPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(new Color(30, 30, 35));
        panel.setBorder(BorderFactory.createLineBorder(new Color(0, 220, 255), 1));
        return panel;
    }

    public static JTextField input() {
        JTextField field = new JTextField();
        field.setBackground(new Color(45, 45, 55));
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        field.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        return field;
    }
}