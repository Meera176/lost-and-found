import java.sql.*;

public class NotificationService {

    public static void addNotification(int userId, String message) {
        try {
            Connection con = DBConnection.getConnection();

            String sql = "INSERT INTO notifications(user_id, message) VALUES (?, ?)";
            PreparedStatement pst = con.prepareStatement(sql);

            pst.setInt(1, userId);
            pst.setString(2, message);

            pst.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}