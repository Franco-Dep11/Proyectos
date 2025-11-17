import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class MainDBPing {
    public static void main(String[] args) {
        try (Connection cn = Database.getConnection();
             Statement st = cn.createStatement();
             ResultSet rs = st.executeQuery("select sqlite_version()")) {

            if (rs.next()) {
                System.out.println("✅ Conexión OK. SQLite versión: " + rs.getString(1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
