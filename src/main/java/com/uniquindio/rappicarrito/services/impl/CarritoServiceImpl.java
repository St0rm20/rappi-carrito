package com.uniquindio.rappicarrito.services.impl;

import com.uniquindio.rappicarrito.model.Carrito;
import com.uniquindio.rappicarrito.model.DetalleProducto;
import com.uniquindio.rappicarrito.model.Producto;
import com.uniquindio.rappicarrito.repository.CarritoRepository;
import com.uniquindio.rappicarrito.repository.DetalleProductoRepository;
import com.uniquindio.rappicarrito.repository.ProductoRepository;
import com.uniquindio.rappicarrito.services.def.carritoService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CarritoServiceImpl implements carritoService {

    private final CarritoRepository carritoRepository;
    private final DetalleProductoRepository detalleProductoRepository;
    private final ProductoRepository productoRepository;

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


    @Override
    public void volverTienda()throws Exception {

    }

    @Override
    public float calcularTotal(List<DetalleProducto> detalleProductos) throws Exception {

        if (detalleProductos == null || detalleProductos.isEmpty()) {
            throw new Exception("La lista de detalles está vacía");
        }

        double total = detalleProductos.stream()
                .filter(d -> d != null)
                .mapToDouble(DetalleProducto::getSubtotal)
                .sum();

        return (float) total;
    }

    @Override
    public void anadirProductoAgain(int idProducto, int idCarrito) throws Exception {
        // Obtener carrito
        Carrito carrito = carritoRepository.findById( idCarrito)
                .orElseThrow(() -> new Exception("Carrito no encontrado"));

        // Obtener producto a añadir
        Producto producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new Exception("Producto no encontrado"));

        // 1. Buscar si ya existe un DetalleProducto para ese producto
        Optional<DetalleProducto> existente = carrito.getProductos()
                .stream()
                .filter(dp -> dp.getProducto().getId() == idProducto)
                .findFirst();

        if (existente.isPresent()) {
            // 2. Si existe → aumentar cantidad
            DetalleProducto detalle = existente.get();
            detalle.setCantidad(detalle.getCantidad() + 1);

            // Recalcular subtotal
            detalle.setSubtotal(detalle.getCantidad() * producto.getPrecio());

            detalleProductoRepository.save(detalle);
            return;
        }

        // 3. Si no existe → crear nuevo detalle
        DetalleProducto nuevo = new DetalleProducto();
        nuevo.setProducto(producto);
        nuevo.setCantidad(1);
        nuevo.setSubtotal(producto.getPrecio());

        // Agregarlo a la lista del carrito
        carrito.getProductos().add(nuevo);

        // Guardar ambos
        detalleProductoRepository.save(nuevo);
        carritoRepository.save(carrito);
    }


}