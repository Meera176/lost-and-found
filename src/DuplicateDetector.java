import java.sql.*;

public class DuplicateDetector {

    public static boolean isDuplicateLostItem(
            int userId,
            String itemName,
            String color,
            String brand,
            String location
    ) {
        try {
            Connection con = DBConnection.getConnection();

            String sql = """
                SELECT item_name, color, brand, location_lost
                FROM lost_items
                WHERE user_id = ?
            """;

            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, userId);

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                int score = MatchingEngine.calculateMatchScore(
        itemName,
        rs.getString("item_name"),

        "",
        "",

        "",
        "",

        color,
        rs.getString("color"),

        brand,
        rs.getString("brand"),

        location,
        rs.getString("location_lost"),

        "",
        ""
);
                if (score >= 60) {
                    return true;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}