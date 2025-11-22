package com.uniquindio.rappicarrito.services.def;

import com.uniquindio.rappicarrito.model.Carrito;

import com.uniquindio.rappicarrito.model.Producto;
import org.springframework.stereotype.Service;

@Service
public interface carritoService {
    void vaciarCarrito(int idCarrito)throws Exception;
    void aniadirProducto(int idDetalleProducto, int idCarrito)throws Exception;
    void eliminarProducto(int idDetalleProducto, int idCarrito)throws Exception;
    Carrito obtenerCarrito(int idCarrito) throws Exception;
}
