public class Plato {
    private final int id;                 // <-- agregado
    private final String nombre;
    private final double precio;
    private final CategoriaPlato categoria;
    private final boolean disponible;

    // Constructor con id (lo usa el DAO al leer desde la BD)
    public Plato(int id, String nombre, double precio, CategoriaPlato categoria, boolean disponible) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.categoria = categoria;
        this.disponible = disponible;
    }

    // Constructor sin id (lo usás en Main y al insertar desde la web)
    public Plato(String nombre, double precio, CategoriaPlato categoria, boolean disponible) {
        this(0, nombre, precio, categoria, disponible);
    }

    public int getId() { return id; }              // <-- getter necesario para WebServer
    public String getNombre() { return nombre; }
    public double getPrecio() { return precio; }
    public CategoriaPlato getCategoria() { return categoria; }
    public boolean isDisponible() { return disponible; }

    @Override
    public String toString() {
        return nombre + " (" + categoria + ") - $" + String.format("%.2f", precio) +
                (disponible ? " [DISP]" : " [NO DISP]");
    }
}
/* Explicación:
- Sumamos id y un constructor con id (para mapear filas de la BD).
- Mantenemos tu constructor simple sin id para seguir usando en Main.
*/
