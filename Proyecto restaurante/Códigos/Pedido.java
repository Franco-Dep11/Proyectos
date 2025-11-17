import java.util.ArrayList;
import java.util.List;

public class Pedido {
    private final int id;                    
    private final int mesaId;                
    private EstadoPedido estado;             
    private final List<LineaPedido> lineas;  

    public Pedido(int id, int mesaId, EstadoPedido estado) {
        this.id = id;
        this.mesaId = mesaId;
        this.estado = estado;
        this.lineas = new ArrayList<>();
    }

    public Pedido(int mesaId) {
        this(0, mesaId, EstadoPedido.ABIERTO);
    }

    public int getId() { return id; }
    public int getMesaId() { return mesaId; }
    public EstadoPedido getEstado() { return estado; }
    public List<LineaPedido> getLineas() { return lineas; }

    public void agregarLinea(LineaPedido lp) {
        lineas.add(lp);
    }

    public double calcularTotal() {
        double total = 0;
        for (LineaPedido lp : lineas) {
            total += lp.getPrecioUnitario() * lp.getCantidad();
        }
        return total;
    }

    public void cerrar() {
        estado = EstadoPedido.CERRADO;
    }

    @Override
    public String toString() {
        return "Pedido de Mesa " + mesaId + " (" + estado + ") - Total: $" 
               + String.format("%.2f", calcularTotal());
    }
}

/*
Explicación corta:
- id → identificador único (para BD).
- mesaId → a qué mesa pertenece el pedido.
- estado → ABIERTO o CERRADO.
- lineas → lista de LineaPedido que forman el pedido.
- Constructor 1 → con todos los valores.
- Constructor 2 → solo con mesaId (queda ABIERTO).
- agregarLinea() → suma un plato al pedido.
- calcularTotal() → multiplica precio * cantidad de cada línea.
- cerrar() → cambia el estado a CERRADO.
- toString() → imprime el pedido con mesa, estado y total.
*/
