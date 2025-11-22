package com.uniquindio.rappicarrito.services.def;

import ch.qos.logback.core.encoder.EchoEncoder;
import org.springframework.stereotype.Service;
import com.uniquindio.rappicarrito.model.DetalleProducto;
@Service
public interface DetalleProductoService {
    void modificarCantidad(int cantidad, int idDetalleProducto, int idProducto) throws Exception;

    double calcularSubTotal(int idDetalleProducto, int idProducto) throws Exception;

    void crearDetalleProducto( int idProducto, int cantidad) throws Exception;
    DetalleProducto obtenerDetalleProducto(int idDetalleProducto) throws Exception;

}
