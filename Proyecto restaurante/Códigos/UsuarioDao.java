import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDao {

    // === ALTAS ===
    public int insertar(Usuario u) throws SQLException {
        validarMaxAdmins(u.getRol(), 0); // 0 = alta (no hay id existente)
        String sql = "INSERT INTO usuarios(nombre, edad, username, password, rol, puesto, sueldo, dias_trabajar, horas_por_dia) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection cn = Database.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, u.getNombre());
            ps.setInt(2, u.getEdad());
            ps.setString(3, u.getUsuario());
            ps.setString(4, u.getPassword());
            ps.setString(5, u.getRol().name());
            ps.setString(6, u.getPuesto());
            ps.setDouble(7, u.getSueldo());
            ps.setInt(8, u.getDiasTrabajar());
            ps.setDouble(9, u.getHorasPorDia());

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    // === MODIFICAR ===
    public boolean actualizar(Usuario u) throws SQLException {
        if (u.getId() == 0) return false;
        validarMaxAdmins(u.getRol(), u.getId());
        String sql = "UPDATE usuarios SET nombre=?, edad=?, username=?, password=?, rol=?, puesto=?, sueldo=?, dias_trabajar=?, horas_por_dia=? " +
                     "WHERE id=?";
        try (Connection cn = Database.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, u.getNombre());
            ps.setInt(2, u.getEdad());
            ps.setString(3, u.getUsuario());
            ps.setString(4, u.getPassword());
            ps.setString(5, u.getRol().name());
            ps.setString(6, u.getPuesto());
            ps.setDouble(7, u.getSueldo());
            ps.setInt(8, u.getDiasTrabajar());
            ps.setDouble(9, u.getHorasPorDia());
            ps.setInt(10, u.getId());

            return ps.executeUpdate() == 1;
        }
    }

    // === BAJA ===
    public boolean eliminarPorId(int id) throws SQLException {
        String sql = "DELETE FROM usuarios WHERE id=?";
        try (Connection cn = Database.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() == 1;
        }
    }

    // === CONSULTAS ===
    public Usuario buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM usuarios WHERE id=?";
        try (Connection cn = Database.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public List<Usuario> listarTodos() throws SQLException {
        String sql = "SELECT * FROM usuarios ORDER BY nombre";
        try (Connection cn = Database.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<Usuario> out = new ArrayList<>();
            while (rs.next()) out.add(map(rs));
            return out;
        }
    }

    // === Helpers internos ===
    private void validarMaxAdmins(RolUsuario rolSolicitado, int idUsuarioEditado) throws SQLException {
        if (rolSolicitado != RolUsuario.ADMIN) return; // sólo aplica si será ADMIN
        int adminsActuales = contarAdminsExcluyendo(idUsuarioEditado);
        if (adminsActuales >= 3) throw new SQLException("Maximo 3 administradores");
    }

    private int contarAdminsExcluyendo(int idExcluir) throws SQLException {
        String sql = "SELECT COUNT(*) FROM usuarios WHERE rol='ADMIN' AND id != ?";
        try (Connection cn = Database.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idExcluir);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    private Usuario map(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String nombre = rs.getString("nombre");
        int    edad   = rs.getInt("edad");
        String user   = rs.getString("username");
        String pass   = rs.getString("password");
        RolUsuario rol= RolUsuario.valueOf(rs.getString("rol"));
        String puesto = rs.getString("puesto");
        double sueldo = rs.getDouble("sueldo");
        int dias      = rs.getInt("dias_trabajar");
        double horas  = rs.getDouble("horas_por_dia");
        return new Usuario(id, nombre, edad, user, pass, rol, puesto, sueldo, dias, horas);
    }
}
/*
Explicación corta:
- CRUD completo sobre la tabla 'usuarios'.
- insertar() y actualizar() validan que no haya más de 3 ADMIN.
- map() traduce una fila de BD a un objeto Usuario.
*/
