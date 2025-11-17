public class Main {
    public static void main(String[] args) {
        // Platos
        Plato milanesa = new Plato("Milanesa con papas", 5200, CategoriaPlato.PRINCIPAL, true);
        Plato empanadas = new Plato("Empanadas (3u)", 2100, CategoriaPlato.ENTRADA, true);
        Plato gaseosa = new Plato("Gaseosa 500ml", 900, CategoriaPlato.BEBIDA, true);

        System.out.println("Platos del menú:");
        System.out.println(" - " + milanesa);
        System.out.println(" - " + empanadas);
        System.out.println(" - " + gaseosa);

        // Mesa y pedido
        Mesa mesa1 = new Mesa(1);
        System.out.println("\n" + mesa1);

        Pedido pedido1 = new Pedido(mesa1.getId());
        pedido1.agregarLinea(new LineaPedido(pedido1.getId(), 1, milanesa.getNombre(), milanesa.getPrecio(), 2));
        pedido1.agregarLinea(new LineaPedido(pedido1.getId(), 2, gaseosa.getNombre(), gaseosa.getPrecio(), 3));

        System.out.println("\nPedido actual:");
        for (LineaPedido lp : pedido1.getLineas()) System.out.println(" - " + lp);
        System.out.println("Total: $" + pedido1.calcularTotal());

        // Pago y cierre
        Pago pago1 = new Pago(pedido1.getId(), pedido1.calcularTotal(), MedioPago.QR);
        pedido1.cerrar();
        System.out.println("\n" + pago1);
        System.out.println("Estado del pedido: " + pedido1.getEstado());

        // Usuarios (usa el constructor corto agregado)
        Usuario admin = new Usuario("Juan Gómez", "admin", "1234", "ADMIN");
        Usuario empleado = new Usuario("María Pérez", "mperez", "abcd", "EMPLEADO");

        System.out.println("\nUsuarios:");
        System.out.println(" - " + admin);
        System.out.println(" - " + empleado);
    }
}
/* Comentario:
- No cambié tu uso: ahora compila porque Usuario tiene constructores cortos.
*/
