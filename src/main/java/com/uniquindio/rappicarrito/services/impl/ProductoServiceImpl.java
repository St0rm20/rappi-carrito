package com.uniquindio.rappicarrito.services.impl;

import com.uniquindio.rappicarrito.model.Producto;
import com.uniquindio.rappicarrito.repository.ProductoRepository;
import com.uniquindio.rappicarrito.services.def.ProductoService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;

    @Override
    public List<Producto> listarProductos() {
        // Este sí es válido (trae todo)
        return productoRepository.findAll();
    }

    @Override
    public Producto obtenerProductoPorId(int id) {
        // CORRECCIÓN: No usar findById. Traer todo y filtrar en memoria.
        List<Producto> productos = productoRepository.findAll();

        return productos.stream()
                .filter(p -> p.getId() == id)
                .findFirst()
                .orElse(null); // O lanzar una excepción si prefieres
    }

    @Override
    public Producto guardarProducto(Producto producto) {
        return productoRepository.save(producto);
    }

    @Override
    public void eliminarProducto(int id) {
        // CORRECCIÓN: No usar deleteById. Buscar primero en memoria, luego eliminar el objeto.
        Producto productoAEliminar = obtenerProductoPorId(id);

        if (productoAEliminar != null) {
            productoRepository.delete(productoAEliminar);
        } else {
            // Opcional: Manejar el caso donde no existe (log o excepción)
            // throw new RuntimeException("Producto no encontrado para eliminar");
        }
    }

    @Override
    public void actualizarProducto(Producto producto) {
        productoRepository.save(producto);
    }

    @Override
    public Producto obtenerPorNombre(String nombre) {

        Optional<Producto> producto= productoRepository.findByNombre(nombre);
        if (producto.isPresent()) {
            return producto.get();
        }
        return null;
    }

    @Override
    public List<Producto> filtrarPorPrecio(float precioInicial, float precioFinal) {
        return productoRepository.findAll()
                .stream()
                .filter(p -> p.getPrecio() >= precioInicial && p.getPrecio() <= precioFinal)
                .collect(Collectors.toList());
    }
}