package com.uniquindio.rappicarrito.services.def;

import com.uniquindio.rappicarrito.model.Producto;
import org.springframework.stereotype.Service;

import java.util.List;


public interface ProductoService {
    List<Producto> listarProductos();

    Producto obtenerProductoPorId(int id);

    Producto guardarProducto(Producto producto);

    void eliminarProducto(int id);

    void actualizarProducto(Producto producto);

    Producto obtenerPorNombre(String nombre);

    List<Producto> filtrarPorPrecio(float precioInicial, float precioFinal);
}
