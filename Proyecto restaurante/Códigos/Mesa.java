public class Mesa {
    private final int id;            
    private final int numero;        
    private EstadoMesa estado;       

    public Mesa(int id, int numero, EstadoMesa estado) {
        this.id = id;
        this.numero = numero;
        this.estado = estado;
    }

    public Mesa(int numero) {
        this(0, numero, EstadoMesa.LIBRE);
    }

    public int getId() { return id; }
    public int getNumero() { return numero; }
    public EstadoMesa getEstado() { return estado; }

    public void ocupar() { estado = EstadoMesa.OCUPADA; }
    public void liberar() { estado = EstadoMesa.LIBRE; }

    @Override
    public String toString() {
        return "Mesa " + numero + " (" + estado + ")";
    }
}

/*
Explicación corta:
- id → identificador único (para BD).
- numero → número visible de la mesa.
- estado → LIBRE u OCUPADA.
- Constructores → uno completo, otro solo con número (queda LIBRE).
- ocupar() / liberar() → cambian el estado de la mesa.
- toString() → imprime algo legible, ej: "Mesa 5 (LIBRE)".
*/
