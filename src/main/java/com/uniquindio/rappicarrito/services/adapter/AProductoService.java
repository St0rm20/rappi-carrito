package com.uniquindio.rappicarrito.services.adapter;

import com.uniquindio.rappicarrito.model.Producto;
import com.uniquindio.rappicarrito.services.def.ProductoService;
import com.uniquindio.rappicarrito.view_model.ProductosViewModel;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AProductoService {

    // Inyección del servicio original (El Adaptee)
    private final ProductoService productoService;

    /**
     * ADAPTER: Obtiene la lista de entidades y las transforma en ViewModels
     */
    public List<ProductosViewModel> listarProductos() {
        List<Producto> entidades = productoService.listarProductos();

        // Transformamos la lista de Entity -> ViewModel
        return entidades.stream()
                .map(ProductosViewModel::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * ADAPTER: Busca por ID y devuelve un ViewModel
     */
    public ProductosViewModel obtenerProductoPorId(int id) {
        Producto entidad = productoService.obtenerProductoPorId(id);
        return ProductosViewModel.fromEntity(entidad);
    }

    /**
     * ADAPTER: Recibe un ViewModel, lo convierte a Entidad para guardarlo
     * y devuelve el ViewModel actualizado (con ID generado, por ejemplo).
     */
    public ProductosViewModel guardarProducto(ProductosViewModel viewModel) {
        // 1. Convertir ViewModel -> Entity
        Producto entidad = mapToEntity(viewModel);

        // 2. Llamar al servicio original
        Producto guardado = productoService.guardarProducto(entidad);

        // 3. Convertir el resultado Entity -> ViewModel
        return ProductosViewModel.fromEntity(guardado);
    }

    /**
     * ADAPTER: Actualización recibiendo ViewModel
     */
    public void actualizarProducto(ProductosViewModel viewModel) {
        if (viewModel.getId() != null) {
            // Buscamos el original para asegurar que existe
            Producto existente = productoService.obtenerProductoPorId(viewModel.getId().intValue());

            if (existente != null) {
                // Actualizamos los campos
                existente.setNombre(viewModel.getNombre());
                existente.setPrecio(viewModel.getPrecioValor());
                existente.setDescripcion(viewModel.getDescripcion());
                existente.setImagenUrl(viewModel.getImagenUrl());
                existente.setUnidadesDisponibles(viewModel.getStock());
                // existente.setEtiquetas(...) // Si hiciera falta

                productoService.actualizarProducto(existente);
            }
        }
    }

    public void eliminarProducto(int id) {
        // Pasa directo, el ID es un tipo primitivo simple
        productoService.eliminarProducto(id);
    }

    public List<ProductosViewModel> filtrarPorPrecio(float min, float max) {
        return productoService.filtrarPorPrecio(min, max)
                .stream()
                .map(ProductosViewModel::fromEntity)
                .collect(Collectors.toList());
    }

    // --- MÉTODOS PRIVADOS DE AYUDA (MAPPING INVERSO) ---

    /**
     * Convierte manualmente de ViewModel a Entity.
     * Esto es necesario porque el repositorio solo entiende de 'Producto'.
     */
    private Producto mapToEntity(ProductosViewModel vm) {
        Producto p = new Producto();
        p.setId(vm.getId());
        p.setNombre(vm.getNombre());
        p.setPrecio(vm.getPrecioValor()); // Usamos el valor numérico, no el string formateado
        p.setDescripcion(vm.getDescripcion());
        p.setImagenUrl(vm.getImagenUrl());
        p.setUnidadesDisponibles(vm.getStock());
        p.setEtiquetas(vm.getEtiquetas());
        p.setEstado(true); // Valor por defecto al crear
        return p;
    }
}