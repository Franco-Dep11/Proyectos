import java.time.LocalDateTime;

public class Pago {
    private final int id;              
    private final int pedidoId;        
    private final double monto;        
    private final MedioPago medio;     
    private final LocalDateTime fecha; 

    public Pago(int id, int pedidoId, double monto, MedioPago medio, LocalDateTime fecha) {
        this.id = id;
        this.pedidoId = pedidoId;
        this.monto = monto;
        this.medio = medio;
        this.fecha = fecha;
    }

    public Pago(int pedidoId, double monto, MedioPago medio) {
        this(0, pedidoId, monto, medio, LocalDateTime.now());
    }

    public int getId() { return id; }
    public int getPedidoId() { return pedidoId; }
    public double getMonto() { return monto; }
    public MedioPago getMedio() { return medio; }
    public LocalDateTime getFecha() { return fecha; }

    @Override
    public String toString() {
        return "Pago de $" + String.format("%.2f", monto) + " con " + medio + " el " + fecha;
    }
}

/*
Explicación corta:
- Representa el pago de un pedido.
- id → identificador único (para BD).
- pedidoId → referencia al pedido que se pagó.
- monto → cuánto se pagó en total.
- medio → cómo se pagó (EFECTIVO, DEBITO, etc.).
- fecha → cuándo se realizó el pago.
- toString() → imprime el detalle del pago.
*/
