import java.sql.Connection;
import java.sql.Statement;

public class DatabaseInit {
    public static void main(String[] args) {
        try (Connection cn = Database.getConnection();
             Statement st = cn.createStatement()) {

            // --- platos ---
            st.execute("CREATE TABLE IF NOT EXISTS platos (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "nombre TEXT NOT NULL," +
                    "precio REAL NOT NULL," +
                    "categoria TEXT NOT NULL," +
                    "disponible INTEGER NOT NULL)");

            // --- mesas ---
            st.execute("CREATE TABLE IF NOT EXISTS mesas (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "numero INTEGER NOT NULL UNIQUE," +
                    "estado TEXT NOT NULL)");

            // --- pedidos ---
            st.execute("CREATE TABLE IF NOT EXISTS pedidos (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "mesa_id INTEGER NOT NULL," +
                    "estado TEXT NOT NULL," +
                    "FOREIGN KEY(mesa_id) REFERENCES mesas(id))");

            // --- lineas_pedido ---
            st.execute("CREATE TABLE IF NOT EXISTS lineas_pedido (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "pedido_id INTEGER NOT NULL," +
                    "nombre_plato TEXT NOT NULL," +
                    "precio_unitario REAL NOT NULL," +
                    "cantidad INTEGER NOT NULL," +
                    "FOREIGN KEY(pedido_id) REFERENCES pedidos(id))");

            // --- pagos ---
            st.execute("CREATE TABLE IF NOT EXISTS pagos (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "pedido_id INTEGER NOT NULL," +
                    "monto REAL NOT NULL," +
                    "medio_pago TEXT NOT NULL," +
                    "fecha TEXT NOT NULL," +
                    "FOREIGN KEY(pedido_id) REFERENCES pedidos(id))");

            // --- BORRAR usuarios/triggers si existían con estructura vieja ---
            st.execute("DROP TRIGGER IF EXISTS trg_max_admins_ins");
            st.execute("DROP TRIGGER IF EXISTS trg_max_admins_upd");
            st.execute("DROP TABLE IF EXISTS usuarios");

            // --- usuarios nueva ---
            st.execute("CREATE TABLE IF NOT EXISTS usuarios (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "nombre TEXT NOT NULL," +
                    "edad INTEGER NOT NULL," +
                    "username TEXT NOT NULL UNIQUE," +
                    "password TEXT NOT NULL," +
                    "rol TEXT NOT NULL CHECK(rol IN ('ADMIN','EMPLEADO'))," +
                    "puesto TEXT NOT NULL," +
                    "sueldo REAL NOT NULL CHECK(sueldo >= 0)," +
                    "dias_trabajar INTEGER NOT NULL CHECK(dias_trabajar BETWEEN 1 AND 7)," +
                    "horas_por_dia REAL NOT NULL CHECK(horas_por_dia > 0)" +
                    ")");

            // --- triggers: máximo 3 ADMIN ---
            st.execute("CREATE TRIGGER IF NOT EXISTS trg_max_admins_ins " +
                    "BEFORE INSERT ON usuarios " +
                    "WHEN NEW.rol = 'ADMIN' AND " +
                    "(SELECT COUNT(*) FROM usuarios WHERE rol='ADMIN') >= 3 " +
                    "BEGIN SELECT RAISE(ABORT, 'Maximo 3 administradores'); END;");

            st.execute("CREATE TRIGGER IF NOT EXISTS trg_max_admins_upd " +
                    "BEFORE UPDATE ON usuarios " +
                    "WHEN NEW.rol = 'ADMIN' AND " +
                    "(SELECT COUNT(*) FROM usuarios WHERE rol='ADMIN' AND id != NEW.id) >= 3 " +
                    "BEGIN SELECT RAISE(ABORT, 'Maximo 3 administradores'); END;");

            System.out.println("✅ Tablas listas (incluye usuarios con tope de 3 ADMIN).");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
/*
Explicación corta:
- Crea todas las tablas (platos, mesas, pedidos, líneas, pagos, usuarios).
- Los usuarios tienen validaciones (edad, sueldo ≥0, 1–7 días, etc.).
- Triggers limitan máximo 3 ADMIN.
*/
