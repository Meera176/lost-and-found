import javax.swing.*;
import java.awt.*;

public class MainDashboard extends JFrame {

    JPanel contentPanel;
    int userId;

    public MainDashboard(int userId) {
        this.userId = userId;

        setTitle("RecoverAI Platform");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(260, getHeight()));
        sidebar.setBackground(new Color(10, 10, 14));
        sidebar.setLayout(null);

        JLabel title = new JLabel("RecoverAI");
        title.setForeground(Color.CYAN);
        title.setFont(new Font("Segoe UI", Font.BOLD, 30));
        title.setBounds(45, 35, 200, 45);
        sidebar.add(title);

        JLabel tag = new JLabel("AI Recovery Platform");
        tag.setForeground(new Color(180, 180, 180));
        tag.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tag.setBounds(45, 78, 180, 25);
        sidebar.add(tag);

        JButton dashboardBtn = createSidebarButton("Dashboard", 140);
        JButton lostBtn = createSidebarButton("Report Lost", 200);
        JButton foundBtn = createSidebarButton("Report Found", 260);
        JButton matchBtn = createSidebarButton("Smart Match", 320);
        JButton ocrBtn = createSidebarButton("OCR AI Scanner", 380);
        JButton logoutBtn = createSidebarButton("Logout", 560);

        sidebar.add(dashboardBtn);
        sidebar.add(lostBtn);
        sidebar.add(foundBtn);
        sidebar.add(matchBtn);
        sidebar.add(ocrBtn);
        sidebar.add(logoutBtn);

        contentPanel = new JPanel();
        contentPanel.setBackground(new Color(18, 18, 22));
        contentPanel.setLayout(null);

        add(sidebar, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);

        dashboardBtn.addActionListener(e -> showHome());
        lostBtn.addActionListener(e -> showPanel(new ReportLostPanel(userId)));
        foundBtn.addActionListener(e -> showPanel(new ReportFoundPanel(userId)));
        matchBtn.addActionListener(e -> showPanel(new SmartMatchPanel(userId)));
        ocrBtn.addActionListener(e -> showPanel(new OCRPanel()));

        logoutBtn.addActionListener(e -> {
            dispose();
            new LoginFrame();
        });

        showHome();
        setVisible(true);
    }

    void showPanel(JPanel panel) {
        contentPanel.removeAll();
        panel.setBounds(0, 0, contentPanel.getWidth(), contentPanel.getHeight());
        contentPanel.add(panel);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    void showHome() {
        contentPanel.removeAll();

        GradientPanel bg = new GradientPanel();
        bg.setBounds(0, 0, 1400, 900);
        bg.setLayout(null);
        contentPanel.add(bg);

        JLabel hero = new JLabel("RecoverAI Intelligence Hub");
        hero.setForeground(Color.WHITE);
        hero.setFont(new Font("Segoe UI", Font.BOLD, 42));
        hero.setBounds(90, 70, 800, 55);
        bg.add(hero);

        JLabel sub = new JLabel("Smart Matching • OCR Intelligence • Fraud Verification • Recovery Confidence");
        sub.setForeground(new Color(200, 200, 200));
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        sub.setBounds(90, 135, 850, 35);
        bg.add(sub);

        JLabel aiGraphic = new JLabel("◉");
        aiGraphic.setForeground(Color.CYAN);
        aiGraphic.setFont(new Font("Segoe UI", Font.BOLD, 180));
        aiGraphic.setBounds(880, 80, 250, 220);
        bg.add(aiGraphic);

        JPanel card1 = createCard("Hybrid AI Match", "LIVE", "Text + Brand + Color + Location + OCR");
        card1.setBounds(90, 260, 300, 170);
        bg.add(card1);

        JPanel card2 = createCard("Fraud Shield", "ACTIVE", "Ownership scoring and risk flags");
        card2.setBounds(430, 260, 300, 170);
        bg.add(card2);

        JPanel card3 = createCard("OCR Engine", "READY", "Python EasyOCR microservice");
        card3.setBounds(770, 260, 300, 170);
        bg.add(card3);

        JPanel card4 = createCard("Recovery Flow", "SECURE", "Admin approval before handover");
        card4.setBounds(90, 470, 300, 170);
        bg.add(card4);

        JPanel card5 = createCard("Duplicate Guard", "ON", "Detects repeated fake reports");
        card5.setBounds(430, 470, 300, 170);
        bg.add(card5);

        JPanel card6 = createCard("Recommendation", "TOP 3", "Best possible found matches");
        card6.setBounds(770, 470, 300, 170);
        bg.add(card6);

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    JButton createSidebarButton(String text, int y) {
        JButton btn = new JButton(text);
        btn.setBounds(30, y, 200, 45);
        btn.setBackground(new Color(28, 28, 35));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setBorder(BorderFactory.createLineBorder(new Color(0, 210, 255)));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(0, 210, 255));
                btn.setForeground(Color.BLACK);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(28, 28, 35));
                btn.setForeground(Color.WHITE);
            }
        });

        return btn;
    }

    JPanel createCard(String title, String value, String desc) {
        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(new Color(30, 30, 38));
        panel.setBorder(BorderFactory.createLineBorder(new Color(0, 210, 255)));

        JLabel t = new JLabel(title);
        t.setForeground(Color.CYAN);
        t.setFont(new Font("Segoe UI", Font.BOLD, 20));
        t.setBounds(22, 20, 250, 30);
        panel.add(t);

        JLabel v = new JLabel(value);
        v.setForeground(Color.WHITE);
        v.setFont(new Font("Segoe UI", Font.BOLD, 34));
        v.setBounds(22, 58, 220, 45);
        panel.add(v);

        JLabel d = new JLabel(desc);
        d.setForeground(new Color(190, 190, 190));
        d.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        d.setBounds(22, 115, 260, 30);
        panel.add(d);

        return panel;
    }

    static class GradientPanel extends JPanel {
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            GradientPaint gp = new GradientPaint(
                    0, 0, new Color(12, 12, 18),
                    getWidth(), getHeight(), new Color(0, 65, 80)
            );
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, getWidth(), getHeight());

            g2d.setColor(new Color(0, 255, 255, 25));
            g2d.fillOval(850, 80, 350, 350);
            g2d.fillOval(100, 520, 280, 280);
        }
    }
}