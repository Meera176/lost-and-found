import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class MatchFrame extends JFrame {

    JTable table;
    int userId;

    public MatchFrame(int userId) {
        this.userId = userId;

        setTitle("RecoverAI - Smart Match Recommendations");
        setSize(1000, 450);
        setLayout(null);
        setLocationRelativeTo(null);

        JLabel title = new JLabel("Top Smart Match Recommendations");
        title.setBounds(380, 10, 300, 30);
        add(title);

        table = new JTable();
        JScrollPane pane = new JScrollPane(table);
        pane.setBounds(20, 50, 940, 270);
        add(pane);

        JButton claimBtn = new JButton("Claim Selected Item");
        claimBtn.setBounds(390, 350, 200, 35);
        add(claimBtn);

        loadMatches();

        claimBtn.addActionListener(e -> claimItem());

        setVisible(true);
    }

    void loadMatches() {
        try {
            Connection con = DBConnection.getConnection();

            String sql = """
                SELECT l.ocr_text AS lost_ocr,
                       f.ocr_text AS found_ocr,
                       l.lost_id, f.found_id,
                       l.item_name AS lost_name,
                       f.item_name AS found_name,
                       l.description AS lost_desc,
                       f.description AS found_desc,
                       l.category AS lost_category,
                       f.category AS found_category,
                       l.color AS lost_color,
                       f.color AS found_color,
                       l.brand AS lost_brand,
                       f.brand AS found_brand,
                       l.location_lost AS lost_location,
                       f.location_found AS found_location
                FROM lost_items l, found_items f
                WHERE l.status='active'
                AND f.status='available'
            """;

            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);

            DefaultTableModel model = new DefaultTableModel();

            model.addColumn("Lost ID");
            model.addColumn("Found ID");
            model.addColumn("Lost Item");
            model.addColumn("Found Item");
            model.addColumn("Color");
            model.addColumn("Brand");
            model.addColumn("Match Score");
            model.addColumn("Confidence");
            model.addColumn("Recommendation");

            while (rs.next()) {

                int score = MatchingEngine.calculateMatchScore(
                        rs.getString("lost_name"),
                        rs.getString("found_name"),
                        rs.getString("lost_desc"),
                        rs.getString("found_desc"),
                        rs.getString("lost_category"),
                        rs.getString("found_category"),
                        rs.getString("lost_color"),
                        rs.getString("found_color"),
                        rs.getString("lost_brand"),
                        rs.getString("found_brand"),
                        rs.getString("lost_location"),
                        rs.getString("found_location"),
                        rs.getString("lost_ocr"),
                        rs.getString("found_ocr")
                );

                if (score >= 30) {
                   String recommendation;

if (score >= 80) {
    recommendation = "Top Match";
} else if (score >= 60) {
    recommendation = "Highly Recommended";
} else {
    recommendation = "Possible Match";
}

model.addRow(new Object[]{
        rs.getInt("lost_id"),
        rs.getInt("found_id"),
        rs.getString("lost_name"),
        rs.getString("found_name"),
        rs.getString("found_color"),
        rs.getString("found_brand"),
        score + "%",
        MatchingEngine.confidenceLabel(score),
        recommendation
});
                }
            }

            table.setModel(model);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    void claimItem() {
        int row = table.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a match first");
            return;
        }

        int lostId = Integer.parseInt(table.getValueAt(row, 0).toString());
        int foundId = Integer.parseInt(table.getValueAt(row, 1).toString());

        String proof = JOptionPane.showInputDialog(
                this,
                "Enter ownership proof:\nExample: unique mark, contents, brand detail"
        );

        int ownershipScore = calculateOwnershipScore(proof);
        String fraudRisk = calculateFraudRisk(proof, ownershipScore);

        try {
            Connection con = DBConnection.getConnection();

            String sql = """
                INSERT INTO claims
                (lost_id, found_id, user_id, proof_description, ownership_score, fraud_risk)
                VALUES (?, ?, ?, ?, ?, ?)
            """;

            PreparedStatement pst = con.prepareStatement(sql);

            pst.setInt(1, lostId);
            pst.setInt(2, foundId);
            pst.setInt(3, userId);
            pst.setString(4, proof);
            pst.setInt(5, ownershipScore);
            pst.setString(6, fraudRisk);

            pst.executeUpdate();
            NotificationService.addNotification(
        userId,
        "Your claim request has been submitted and is pending admin review."
);

            String updateUser = "UPDATE users SET claim_count = claim_count + 1 WHERE user_id=?";
            PreparedStatement pst2 = con.prepareStatement(updateUser);
            pst2.setInt(1, userId);
            pst2.executeUpdate();

            JOptionPane.showMessageDialog(this,
                    "Claim submitted\nOwnership Score: " + ownershipScore +
                    "%\nFraud Risk: " + fraudRisk);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    int calculateOwnershipScore(String proof) {
        if (proof == null) return 0;

        int score = 0;

        if (proof.length() >= 20) score += 40;
        if (proof.toLowerCase().contains("card")) score += 20;
        if (proof.toLowerCase().contains("id")) score += 20;
        if (proof.toLowerCase().contains("brand")) score += 10;
        if (proof.toLowerCase().contains("scratch") || proof.toLowerCase().contains("mark")) score += 10;

        return Math.min(score, 100);
    }

    String calculateFraudRisk(String proof, int ownershipScore) {
        if (proof == null || proof.length() < 10) return "High";
        if (ownershipScore < 40) return "High";
        if (ownershipScore < 70) return "Medium";
        return "Low";
    }
}