package com.uniquindio.rappicarrito.services.def;

import com.uniquindio.rappicarrito.model.Carrito;

import com.uniquindio.rappicarrito.model.DetalleProducto;

import java.util.List;


public interface CarritoService {
    void vaciarCarrito(int idCarrito)throws Exception;
    void aniadirProducto(int idDetalleProducto, int idCarrito)throws Exception;
    void eliminarProducto(int idDetalleProducto, int idCarrito)throws Exception;
    Carrito obtenerCarrito(int idCarrito) throws Exception;
    void volverTienda()throws Exception;
    float calcularTotal(List<DetalleProducto> detalleProductos)throws Exception;
    void anadirProductoAgain(int idDetalleProducto, int idCarrito)throws Exception;
}
