package com.uniquindio.rappicarrito.view_model;

import com.uniquindio.rappicarrito.model.Carrito;
import com.uniquindio.rappicarrito.model.DetalleProducto;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CarritoViewModel {

    private Long id;
    private Boolean estado;

    // Usamos una lista de ViewModels, no de Entidades
    private List<DetalleProductoViewModel> items;

    // Datos listos para mostrar en la interfaz
    private String totalFormateado;
    private Double totalValor;      // Valor numérico para validaciones
    private Integer cantidadTotalItems; // Ej: "Tienes 5 productos"

    public static CarritoViewModel fromEntity(Carrito entity) {
        if (entity == null) return null;

        // 1. Convertir la lista de Detalles a ViewModels
        List<DetalleProductoViewModel> itemsVM = new ArrayList<>();
        if (entity.getProductos() != null) {
            itemsVM = entity.getProductos().stream()
                    .map(DetalleProductoViewModel::fromEntity)
                    .collect(Collectors.toList());
        }

        // 2. Calcular el total sumando los subtotales de los items
        double totalCalculado = 0.0;
        int cantidadItems = 0;

        if (entity.getProductos() != null) {
            totalCalculado = entity.getProductos().stream()
                    .mapToDouble(DetalleProducto::getSubtotal)
                    .sum();

            cantidadItems = entity.getProductos().stream()
                    .mapToInt(DetalleProducto::getCantidad)
                    .sum();
        }

        return CarritoViewModel.builder()
                .id(entity.getId())
                .estado(entity.getEstado())
                .items(itemsVM)
                .totalValor(totalCalculado)
                .totalFormateado(String.format("$ %.2f", totalCalculado))
                .cantidadTotalItems(cantidadItems)
                .build();
    }
}