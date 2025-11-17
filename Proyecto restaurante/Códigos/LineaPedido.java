public class LineaPedido {
    private final int id;               
    private final int pedidoId;         
    private final int platoId;          
    private final String nombrePlato;   
    private final double precioUnitario;
    private final int cantidad;         

    public LineaPedido(int id, int pedidoId, int platoId, String nombrePlato, double precioUnitario, int cantidad) {
        this.id = id;
        this.pedidoId = pedidoId;
        this.platoId = platoId;
        this.nombrePlato = nombrePlato;
        this.precioUnitario = precioUnitario;
        this.cantidad = cantidad;
    }

    public LineaPedido(int pedidoId, int platoId, String nombrePlato, double precioUnitario, int cantidad) {
        this(0, pedidoId, platoId, nombrePlato, precioUnitario, cantidad);
    }

    public int getId() { return id; }
    public int getPedidoId() { return pedidoId; }
    public int getPlatoId() { return platoId; }
    public String getNombrePlato() { return nombrePlato; }
    public double getPrecioUnitario() { return precioUnitario; }
    public int getCantidad() { return cantidad; }

    @Override
    public String toString() {
        return cantidad + " x " + nombrePlato + " — $" + String.format("%.2f", precioUnitario);
    }
}

/*
Explicación corta:
- Representa una línea del pedido (ej: 2 gaseosas).
- id → identificador único (para BD).
- pedidoId → a qué pedido pertenece.
- platoId → referencia al plato original.
- nombrePlato → copia del nombre (para mantener historial).
- precioUnitario → precio del plato en ese momento.
- cantidad → cuántos se pidieron.
- toString() → imprime cantidad, nombre y precio unitario.
*/
