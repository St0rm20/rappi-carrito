package com.uniquindio.rappicarrito.view_model;

import com.uniquindio.rappicarrito.model.Producto;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder // Patrón Builder para crear instancias fácilmente
public class ProductosViewModel {

    private Long id;
    private String nombre;
    private String precioFormateado; // Ej: "$ 15,000" (String para la vista)
    private Double precioValor;      // El valor numérico para cálculos
    private String descripcion;
    private String imagenUrl;
    private Integer stock;           // Mapeado de 'unidadesDisponibles'
    private List<String> etiquetas;

    // --- Campos Exclusivos de la Vista (No existen en DB) ---
    private Integer cantidadSeleccionada; // Para controlar cuánto quiere comprar el usuario

    /**
     * Método Mágico: Convierte tu Entidad de Base de Datos a este ViewModel.
     * Esto centraliza la lógica de conversión.
     */
    public static ProductosViewModel fromEntity(Producto producto) {
        if (producto == null) return null;

        return ProductosViewModel.builder()
                .id(producto.getId())
                .nombre(producto.getNombre())
                // Guardamos el doble puro para sumas
                .precioValor(producto.getPrecio())
                // Pre-formateamos el texto para que la vista solo imprima strings
                .precioFormateado(String.format("$ %.2f", producto.getPrecio()))
                .descripcion(producto.getDescripcion())
                // Si la URL es nula, ponemos una por defecto aquí para no ensuciar el controlador
                .imagenUrl(producto.getImagenUrl() != null && !producto.getImagenUrl().isEmpty()
                        ? producto.getImagenUrl()
                        : "https://via.placeholder.com/150")
                .stock(producto.getUnidadesDisponibles())
                .etiquetas(producto.getEtiquetas())
                .cantidadSeleccionada(1) // Por defecto siempre seleccionamos 1
                .build();
    }
}