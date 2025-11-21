package com.uniquindio.rappicarrito.services.def;

import com.uniquindio.rappicarrito.model.Producto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ProductoService {
    List<Producto> listarProductos();

    Producto obtenerProductoPorId(int id);

    Producto guardarProducto(Producto producto);

    void eliminarProducto(int id);

    void actualizarProducto(Producto producto);
}
