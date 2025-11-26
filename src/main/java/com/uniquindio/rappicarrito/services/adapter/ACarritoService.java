package com.uniquindio.rappicarrito.services.adapter;

import com.uniquindio.rappicarrito.model.Carrito;
import com.uniquindio.rappicarrito.services.def.CarritoService;
import com.uniquindio.rappicarrito.view_model.CarritoViewModel;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ACarritoService {

    private final CarritoService carritoServiceOriginal;

    /**
     * ADAPTER: Obtiene el carrito y lo transforma a ViewModel
     * para que la vista tenga el total calculado y la lista formateada.
     */
    public CarritoViewModel obtenerCarrito(int idCarrito) {
        try {
            Carrito carrito = carritoServiceOriginal.obtenerCarrito(idCarrito);
            return CarritoViewModel.fromEntity(carrito);
        } catch (Exception e) {
            e.printStackTrace();
            return null; // O manejar con un ViewModel vacío
        }
    }

    /**
     * ADAPTER: Agrega un producto (o suma cantidad) y devuelve
     * el Carrito actualizado para refrescar la interfaz inmediatamente.
     */
    public CarritoViewModel agregarProducto(int idProducto, int idCarrito) throws Exception {
        // 1. Usamos tu lógica de "anadirProductoAgain" que ya maneja la duplicidad
        carritoServiceOriginal.anadirProductoAgain(idProducto, idCarrito);

        // 2. Devolvemos el estado actual del carrito
        return obtenerCarrito(idCarrito);
    }

    /**
     * ADAPTER: Vacía el carrito y retorna el estado limpio.
     */
    public CarritoViewModel vaciarCarrito(int idCarrito) throws Exception {
        carritoServiceOriginal.vaciarCarrito(idCarrito);
        return obtenerCarrito(idCarrito);
    }

    /**
     * ADAPTER: Elimina un item específico (fila) del carrito.
     */
    public CarritoViewModel eliminarDetalle(int idDetalleProducto, int idCarrito) throws Exception {
        carritoServiceOriginal.eliminarProducto(idDetalleProducto, idCarrito);
        return obtenerCarrito(idCarrito);
    }
}