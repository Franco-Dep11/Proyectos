import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PlatoDao {

    public int insertar(Plato p) throws SQLException {
        String sql = "INSERT INTO platos(nombre, precio, categoria, disponible) VALUES (?, ?, ?, ?)";
        try (Connection cn = Database.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, p.getNombre());
            ps.setDouble(2, p.getPrecio());
            ps.setString(3, p.getCategoria().name());
            ps.setInt(4, p.isDisponible() ? 1 : 0);

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    public List<Plato> listarTodos() throws SQLException {
        String sql = "SELECT id, nombre, precio, categoria, disponible FROM platos ORDER BY nombre";
        try (Connection cn = Database.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            List<Plato> res = new ArrayList<>();
            while (rs.next()) {
                int id = rs.getInt("id");
                String nombre = rs.getString("nombre");
                double precio = rs.getDouble("precio");
                CategoriaPlato cat = CategoriaPlato.valueOf(rs.getString("categoria"));
                boolean disp = rs.getInt("disponible") == 1;
                res.add(new Plato(id, nombre, precio, cat, disp));
            }
            return res;
        }
    }

    // (Opcionales por si luego los querés)
    public boolean actualizar(Plato p) throws SQLException {
        String sql = "UPDATE platos SET nombre=?, precio=?, categoria=?, disponible=? WHERE id=?";
        try (Connection cn = Database.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, p.getNombre());
            ps.setDouble(2, p.getPrecio());
            ps.setString(3, p.getCategoria().name());
            ps.setInt(4, p.isDisponible() ? 1 : 0);
            ps.setInt(5, p.getId());
            return ps.executeUpdate() == 1;
        }
    }

    public boolean eliminar(int id) throws SQLException {
        String sql = "DELETE FROM platos WHERE id=?";
        try (Connection cn = Database.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() == 1;
        }
    }
}
/* Explicación:
- insertar(): guarda plato y devuelve el id autogenerado.
- listarTodos(): devuelve List<Plato> mapeando desde la BD.
- actualizar()/eliminar(): utilidades extra por si las necesitás luego.
*/
