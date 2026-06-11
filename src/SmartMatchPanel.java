import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.sql.*;

public class SmartMatchPanel extends JPanel {

    JTable table;
    int userId;

    public SmartMatchPanel(int userId) {
        this.userId = userId;

        setLayout(null);
        setBackground(new Color(18, 18, 22));

        JButton backBtn = UIHelper.neonButton("← Back");
        backBtn.setBounds(950, 40, 120, 38);
        add(backBtn);

        backBtn.addActionListener(e -> {
            Window window = SwingUtilities.getWindowAncestor(this);
            if (window instanceof MainDashboard dashboard) {
                dashboard.showHome();
            }
        });

        JLabel title = UIHelper.title("Smart AI Match Recommendations");
        title.setBounds(80, 40, 700, 45);
        add(title);

        JLabel sub = UIHelper.subtitle("Hybrid scoring using text, color, brand, location and OCR intelligence.");
        sub.setBounds(80, 90, 850, 30);
        add(sub);

        JPanel card = UIHelper.glassPanel();
        card.setBounds(80, 150, 1050, 520);
        add(card);

        table = new JTable();
        table.setBackground(new Color(35, 35, 42));
        table.setForeground(Color.WHITE);
        table.setGridColor(Color.CYAN);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(28);

        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(0, 210, 255));
        header.setForeground(Color.BLACK);
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBounds(30, 30, 990, 370);
        card.add(scroll);

        JButton claimBtn = UIHelper.neonButton("Claim Selected Match");
        claimBtn.setBounds(400, 430, 240, 40);
        card.add(claimBtn);

        loadMatches();
        claimBtn.addActionListener(e -> claimItem());
    }

    void loadMatches() {
        try {
            Connection con = DBConnection.getConnection();

            String sql = """
                SELECT l.lost_id, f.found_id,
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
                       f.location_found AS found_location,
                       l.ocr_text AS lost_ocr,
                       f.ocr_text AS found_ocr
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
            model.addColumn("Score");
            model.addColumn("Confidence");
            model.addColumn("Recommendation");
            model.addColumn("OCR Evidence");

            while (rs.next()) {
                String lostOCR = rs.getString("lost_ocr");
                String foundOCR = rs.getString("found_ocr");

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
                        lostOCR,
                        foundOCR
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

                    String ocrEvidence = "No OCR Match";

                    if (lostOCR != null && foundOCR != null &&
                            MatchingEngine.textSimilarity(lostOCR, foundOCR) >= 40) {
                        ocrEvidence = "OCR Match Found";
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
                            recommendation,
                            ocrEvidence
                    });
                }
            }

            table.setModel(model);

        } catch (Exception e) {
            e.printStackTrace();
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

        JTextArea proofArea = new JTextArea();
        proofArea.setLineWrap(true);
        proofArea.setWrapStyleWord(true);

        JScrollPane pane = new JScrollPane(proofArea);
        pane.setPreferredSize(new Dimension(350, 120));

        int result = JOptionPane.showConfirmDialog(
                this,
                pane,
                "Enter Ownership Proof",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        String proof = proofArea.getText();

        int ownershipScore = 70;
        String fraudRisk = "Low";

        if (proof == null || proof.length() < 10) {
            ownershipScore = 20;
            fraudRisk = "High";
        } else if (proof.length() < 25) {
            ownershipScore = 50;
            fraudRisk = "Medium";
        }

        try {
            Connection con = DBConnection.getConnection();

            String sql = """
                INSERT INTO claims
                (lost_id, found_id, user_id,
                 proof_description,
                 ownership_score,
                 fraud_risk)
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
            String updateUser = "UPDATE users SET claim_count = claim_count + 1 WHERE user_id=?";
PreparedStatement updatePst = con.prepareStatement(updateUser);
updatePst.setInt(1, userId);
updatePst.executeUpdate();

            JOptionPane.showMessageDialog(
                    this,
                    "Claim Submitted Successfully\n\n" +
                            "Ownership Score: " + ownershipScore + "%\n" +
                            "Fraud Risk: " + fraudRisk
            );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}