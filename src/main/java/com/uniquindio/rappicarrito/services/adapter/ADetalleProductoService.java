package com.uniquindio.rappicarrito.services.adapter;

import com.uniquindio.rappicarrito.model.DetalleProducto;
import com.uniquindio.rappicarrito.repository.DetalleProductoRepository;
import com.uniquindio.rappicarrito.services.def.DetalleProductoService;
import com.uniquindio.rappicarrito.view_model.DetalleProductoViewModel;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ADetalleProductoService {

    private final DetalleProductoService detalleService;
    private final DetalleProductoRepository detalleRepository;

    /**
     * ADAPTER: Crea un detalle basado en IDs y cantidades.
     * Retorna true si fue exitoso para que la vista sepa.
     */
    public void agregarProductoAlCarrito(int idProducto, int cantidad) throws Exception {
        // Delegamos a la lógica compleja de tu servicio original
        detalleService.crearDetalleProducto(idProducto, cantidad);
    }

    /**
     * ADAPTER: Modifica la cantidad desde la vista (ej: spinner del carrito).
     * Devuelve el ViewModel actualizado para refrescar solo esa fila si se desea.
     */
    public DetalleProductoViewModel actualizarCantidad(int idDetalle, int idProducto, int nuevaCantidad) throws Exception {
        // 1. Llamar al servicio original para validar y guardar
        detalleService.modificarCantidad(nuevaCantidad, idDetalle, idProducto);

        // 2. Recuperar el objeto actualizado para devolverlo a la vista
        // (Tu servicio original retorna void, por eso buscamos de nuevo)
        DetalleProducto actualizado = detalleService.obtenerDetalleProducto(idDetalle);

        // 3. Recalcular subtotal en la entidad si el servicio no lo hizo automáticamente
        // (Esto asegura consistencia visual)
        double subtotal = detalleService.calcularSubTotal(idDetalle, idProducto);
        actualizado.setSubtotal(subtotal);

        return DetalleProductoViewModel.fromEntity(actualizado);
    }

    /**
     * ADAPTER: Obtener todos los elementos del carrito como ViewModels.
     * Esencial para llenar la tabla del carrito.
     */
    public List<DetalleProductoViewModel> obtenerCarritoCompleto() {
        List<DetalleProducto> entidades = detalleRepository.findAll();

        return entidades.stream()
                .map(DetalleProductoViewModel::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Helper para calcular el total absoluto del carrito (Suma de subtotales)
     */
    public String calcularTotalCarrito() {
        List<DetalleProducto> lista = detalleRepository.findAll();
        double total = lista.stream()
                .mapToDouble(d -> d.getCantidad() * d.getProducto().getPrecio())
                .sum();
        return String.format("$ %.2f", total);
    }
}