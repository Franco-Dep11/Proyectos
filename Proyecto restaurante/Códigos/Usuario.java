public class Usuario {
    private final int id;
    private final String nombre;
    private final int edad;
    private final String usuario;     // username
    private final String password;    // (MVP: texto plano)
    private final RolUsuario rol;     // ADMIN o EMPLEADO
    private final String puesto;      // ej: Mozo, Cajero
    private final double sueldo;      // mensual
    private final int diasTrabajar;   // 1..7
    private final double horasPorDia; // > 0

    public Usuario(int id, String nombre, int edad, String usuario, String password,
                   RolUsuario rol, String puesto, double sueldo, int diasTrabajar, double horasPorDia) {
        this.id = id;
        this.nombre = nombre;
        this.edad = edad;
        this.usuario = usuario;
        this.password = password;
        this.rol = rol;
        this.puesto = puesto;
        this.sueldo = sueldo;
        this.diasTrabajar = diasTrabajar;
        this.horasPorDia = horasPorDia;
    }

    public Usuario(String nombre, int edad, String usuario, String password,
                   RolUsuario rol, String puesto, double sueldo, int diasTrabajar, double horasPorDia) {
        this(0, nombre, edad, usuario, password, rol, puesto, sueldo, diasTrabajar, horasPorDia);
    }

    // Constructor corto para tu Main: rol como String ("ADMIN"/"EMPLEADO")
    public Usuario(String nombre, String usuario, String password, String rol) {
        this(0, nombre, 0, usuario, password,
             RolUsuario.valueOf(rol.toUpperCase()),
             "", 0.0, 0, 0.0);
    }

    // Constructor corto alternativo: rol como enum
    public Usuario(String nombre, String usuario, String password, RolUsuario rol) {
        this(0, nombre, 0, usuario, password, rol, "", 0.0, 0, 0.0);
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public int getEdad() { return edad; }
    public String getUsuario() { return usuario; }
    public String getPassword() { return password; }
    public RolUsuario getRol() { return rol; }
    public String getPuesto() { return puesto; }
    public double getSueldo() { return sueldo; }
    public int getDiasTrabajar() { return diasTrabajar; }
    public double getHorasPorDia() { return horasPorDia; }

    @Override
    public String toString() {
        String base = nombre + " [" + rol + "]";
        if (puesto != null && !puesto.isEmpty()) base += " · " + puesto;
        if (sueldo > 0) base += " · $" + String.format("%.2f", sueldo);
        return base;
    }
}
/* Comentario:
- Agregué dos constructores “cortos” para que compile con el uso de tu Main.
- El resto queda igual; toString() oculta puesto/sueldo si están vacíos.
*/
