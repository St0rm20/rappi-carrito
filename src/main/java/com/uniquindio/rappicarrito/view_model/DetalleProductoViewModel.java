package com.uniquindio.rappicarrito.view_model;

import com.uniquindio.rappicarrito.model.DetalleProducto;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DetalleProductoViewModel {

    private int id; // ID del detalle (para borrar o editar)
    private int cantidad;
    private String subtotalFormateado;
    private Double subtotalValor; // Para sumas totales del carrito

    // Anidamos el ViewModel del producto, no la entidad
    private ProductosViewModel producto;

    public static DetalleProductoViewModel fromEntity(DetalleProducto entity) {
        if (entity == null) return null;

        // Calculamos subtotal si viene nulo o usamos el de la entidad
        double subTotalCalc = (entity.getSubtotal() > 0)
                ? entity.getSubtotal()
                : (entity.getCantidad() * entity.getProducto().getPrecio());

        return DetalleProductoViewModel.builder()
                .id(entity.getId())
                .cantidad(entity.getCantidad())
                .subtotalValor(subTotalCalc)
                .subtotalFormateado(String.format("$ %.2f", subTotalCalc))
                // Reutilizamos el mapeador que ya creaste para el producto
                .producto(ProductosViewModel.fromEntity(entity.getProducto()))
                .build();
    }
}