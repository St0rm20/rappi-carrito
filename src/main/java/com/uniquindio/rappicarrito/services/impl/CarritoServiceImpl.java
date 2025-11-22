package com.uniquindio.rappicarrito.services.impl;

import com.uniquindio.rappicarrito.model.Carrito;
import com.uniquindio.rappicarrito.model.DetalleProducto;
import com.uniquindio.rappicarrito.repository.CarritoRepository;
import com.uniquindio.rappicarrito.repository.DetalleProductoRepository;
import com.uniquindio.rappicarrito.services.def.carritoService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class CarritoServiceImpl implements carritoService {

    private final CarritoRepository carritoRepository;
    private final DetalleProductoRepository detalleProductoRepository;

    // --- MÉTODOS DE UTILIDAD PRIVADOS ---

    private Carrito buscarCarritoPorId(int idCarrito) throws Exception {
        return carritoRepository.findAll().stream()
                .filter(c -> c.getId() == idCarrito)
                .findFirst()
                .orElseThrow(() -> new Exception("Carrito no encontrado con ID: " + idCarrito));
    }

    private DetalleProducto buscarDetalleProductoPorId(int idDetalleProducto) throws Exception {
        return detalleProductoRepository.findAll().stream()
                .filter(dp -> dp.getId() == idDetalleProducto)
                .findFirst()
                .orElseThrow(() -> new Exception("Detalle producto no encontrado con ID: " + idDetalleProducto));
    }

    // --- IMPLEMENTACIÓN DE MÉTODOS PÚBLICOS ---

    @Override
    public void vaciarCarrito(int idCarrito) throws Exception {
        // Se reutiliza la búsqueda
        Carrito carritoEncontrado = buscarCarritoPorId(idCarrito);

        // Vaciar la lista
        carritoEncontrado.setProductos(new ArrayList<>());

        carritoRepository.save(carritoEncontrado);
    }

    @Override
    public void aniadirProducto(int idDetalleProducto, int idCarrito) throws Exception {
        // Se reutilizan las búsquedas
        Carrito carritoEncontrado = buscarCarritoPorId(idCarrito);
        DetalleProducto productoEncontrado = buscarDetalleProductoPorId(idDetalleProducto);

        carritoEncontrado.getProductos().add(productoEncontrado);
        carritoRepository.save(carritoEncontrado);
    }

    @Override
    public void eliminarProducto(int idDetalleProducto, int idCarrito) throws Exception {
        // Se reutilizan las búsquedas
        Carrito carritoEncontrado = buscarCarritoPorId(idCarrito);
        DetalleProducto productoEncontrado = buscarDetalleProductoPorId(idDetalleProducto);

        carritoEncontrado.getProductos().remove(productoEncontrado);
        carritoRepository.save(carritoEncontrado);
    }

    @Override
    public Carrito obtenerCarrito(int idCarrito) throws Exception {
        // Se reutiliza la búsqueda
        return buscarCarritoPorId(idCarrito);
    }

}