package com.uniquindio.rappicarrito.services.def;

import org.springframework.stereotype.Service;

@Service
public interface DetalleProductoService {
    void modificarCantidad(int cantidad, int idDetalleProducto, int idProducto) throws Exception;

    double calcularSubTotal(int idDetalleProducto, int idProducto) throws Exception;

    void crearDetalleProducto( int idProducto, int cantidad) throws Exception;
}
