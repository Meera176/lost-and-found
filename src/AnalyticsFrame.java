import javax.swing.*;
import java.sql.*;

public class AnalyticsFrame extends JFrame {

    public AnalyticsFrame() {
        setTitle("RecoverAI - Analytics Dashboard");
        setSize(500, 400);
        setLayout(null);
        setLocationRelativeTo(null);

        JLabel title = new JLabel("RecoverAI Analytics");
        title.setBounds(180, 20, 200, 30);
        add(title);

        JLabel totalLost = new JLabel();
        totalLost.setBounds(80, 80, 350, 30);
        add(totalLost);

        JLabel totalFound = new JLabel();
        totalFound.setBounds(80, 120, 350, 30);
        add(totalFound);

        JLabel totalClaims = new JLabel();
        totalClaims.setBounds(80, 160, 350, 30);
        add(totalClaims);

        JLabel approvedClaims = new JLabel();
        approvedClaims.setBounds(80, 200, 350, 30);
        add(approvedClaims);

        JLabel highRiskClaims = new JLabel();
        highRiskClaims.setBounds(80, 240, 350, 30);
        add(highRiskClaims);

        try {
            Connection con = DBConnection.getConnection();

            totalLost.setText("Total Lost Reports: " + getCount(con, "SELECT COUNT(*) FROM lost_items"));
            totalFound.setText("Total Found Reports: " + getCount(con, "SELECT COUNT(*) FROM found_items"));
            totalClaims.setText("Total Claim Requests: " + getCount(con, "SELECT COUNT(*) FROM claims"));
            approvedClaims.setText("Approved Claims: " + getCount(con, "SELECT COUNT(*) FROM claims WHERE status='approved'"));
            highRiskClaims.setText("High Fraud Risk Claims: " + getCount(con, "SELECT COUNT(*) FROM claims WHERE fraud_risk='High'"));

        } catch (Exception e) {
            e.printStackTrace();
        }

        setVisible(true);
    }

    int getCount(Connection con, String sql) throws Exception {
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(sql);

        if (rs.next()) {
            return rs.getInt(1);
        }

        return 0;
    }
}