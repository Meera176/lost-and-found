import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.sql.*;

public class AdminDashboard extends JFrame {

    JTable table;

    public AdminDashboard() {
        setTitle("RecoverAI - Admin Intelligence Dashboard");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(260, getHeight()));
        sidebar.setBackground(new Color(10, 10, 14));
        sidebar.setLayout(null);

        JLabel logo = new JLabel("RecoverAI");
        logo.setForeground(Color.CYAN);
        logo.setFont(new Font("Segoe UI", Font.BOLD, 30));
        logo.setBounds(45, 35, 200, 45);
        sidebar.add(logo);

        JLabel role = new JLabel("Admin Control Center");
        role.setForeground(new Color(180, 180, 180));
        role.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        role.setBounds(45, 78, 180, 25);
        sidebar.add(role);

        JButton claimsBtn = sideButton("Claims Review", 150);
        JButton analyticsBtn = sideButton("Analytics", 210);
        JButton refreshBtn = sideButton("Refresh", 270);
        JButton logoutBtn = sideButton("Logout", 560);

        sidebar.add(claimsBtn);
        sidebar.add(analyticsBtn);
        sidebar.add(refreshBtn);
        sidebar.add(logoutBtn);

        JPanel main = new JPanel();
        main.setLayout(null);
        main.setBackground(new Color(18, 18, 22));

        JLabel title = UIHelper.title("Admin Claim Approval & Fraud Review");
        title.setBounds(80, 40, 800, 45);
        main.add(title);

        JLabel sub = UIHelper.subtitle("Review ownership score, fraud risk, claim count and approve secure recovery.");
        sub.setBounds(80, 90, 900, 30);
        main.add(sub);

        JPanel card = UIHelper.glassPanel();
        card.setBounds(80, 150, 1050, 520);
        main.add(card);

        table = new JTable();
        table.setBackground(new Color(35, 35, 42));
        table.setForeground(Color.WHITE);
        table.setGridColor(Color.CYAN);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(30);

        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(0, 210, 255));
        header.setForeground(Color.BLACK);
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));

        JScrollPane pane = new JScrollPane(table);
        pane.setBounds(30, 30, 990, 350);
        card.add(pane);

        JButton approveBtn = UIHelper.neonButton("Approve Claim");
        approveBtn.setBounds(270, 420, 180, 40);
        card.add(approveBtn);

        JButton rejectBtn = UIHelper.neonButton("Reject Claim");
        rejectBtn.setBounds(500, 420, 180, 40);
        card.add(rejectBtn);

        add(sidebar, BorderLayout.WEST);
        add(main, BorderLayout.CENTER);

        loadClaims();

        approveBtn.addActionListener(e -> updateClaim("approved"));
        rejectBtn.addActionListener(e -> updateClaim("rejected"));
        refreshBtn.addActionListener(e -> loadClaims());
        analyticsBtn.addActionListener(e -> new AnalyticsFrame());

        logoutBtn.addActionListener(e -> {
            dispose();
            new LoginFrame();
        });

        setVisible(true);
    }

    JButton sideButton(String text, int y) {
        JButton btn = new JButton(text);
        btn.setBounds(30, y, 200, 45);
        btn.setBackground(new Color(28, 28, 35));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setBorder(BorderFactory.createLineBorder(new Color(0, 210, 255)));
        return btn;
    }

    void loadClaims() {
        try {
            Connection con = DBConnection.getConnection();

            String sql = """
                SELECT c.claim_id,
                       u.name,
                       u.claim_count,
                       l.item_name AS lost_item,
                       f.item_name AS found_item,
                       c.ownership_score,
                       c.fraud_risk,
                       c.status
                FROM claims c
                JOIN users u ON c.user_id = u.user_id
                JOIN lost_items l ON c.lost_id = l.lost_id
                JOIN found_items f ON c.found_id = f.found_id
            """;

            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);

            DefaultTableModel model = new DefaultTableModel();

            model.addColumn("Claim ID");
            model.addColumn("Student");
            model.addColumn("Claim Count");
            model.addColumn("Lost Item");
            model.addColumn("Found Item");
            model.addColumn("Ownership Score");
            model.addColumn("Fraud Risk");
            model.addColumn("Status");

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("claim_id"),
                        rs.getString("name"),
                        rs.getInt("claim_count"),
                        rs.getString("lost_item"),
                        rs.getString("found_item"),
                        rs.getInt("ownership_score") + "%",
                        rs.getString("fraud_risk"),
                        rs.getString("status")
                });
            }

            table.setModel(model);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    void updateClaim(String status) {
        int row = table.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a claim first");
            return;
        }

        int claimId = Integer.parseInt(table.getValueAt(row, 0).toString());

        try {
            Connection con = DBConnection.getConnection();

            String sql = "UPDATE claims SET status=? WHERE claim_id=?";
            PreparedStatement pst = con.prepareStatement(sql);

            pst.setString(1, status);
            pst.setInt(2, claimId);
            pst.executeUpdate();
            String emailSql = """
    SELECT u.email, l.item_name
    FROM claims c
    JOIN users u ON c.user_id = u.user_id
    JOIN lost_items l ON c.lost_id = l.lost_id
    WHERE c.claim_id=?
""";

PreparedStatement emailPst = con.prepareStatement(emailSql);
emailPst.setInt(1, claimId);

ResultSet emailRs = emailPst.executeQuery();

if (emailRs.next()) {
    EmailService.sendClaimStatusEmail(
            emailRs.getString("email"),
            emailRs.getString("item_name"),
            status
    );
}

            JOptionPane.showMessageDialog(this, "Claim " + status);
            loadClaims();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}