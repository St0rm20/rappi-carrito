package com.uniquindio.rappicarrito.services.impl;

import com.uniquindio.rappicarrito.model.DetalleProducto;
import com.uniquindio.rappicarrito.model.Producto;
import com.uniquindio.rappicarrito.repository.DetalleProductoRepository;
import com.uniquindio.rappicarrito.repository.ProductoRepository;
import com.uniquindio.rappicarrito.services.def.DetalleProductoService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class DetalleProductoServiceImpl implements DetalleProductoService {

    private final ProductoRepository productoRepository;
    private final DetalleProductoRepository detalleProductoRepository;


    private DetalleProducto buscarDetalleProductoPorId(int idDetalleProducto) throws Exception {
        // Asumiendo que detallleProductoRepository.findAll() devuelve todos los registros
        return detalleProductoRepository.findAll().stream()
                .filter(dp -> dp.getId() == idDetalleProducto)
                .findFirst()
                .orElseThrow(() -> new Exception("No se encontró el detalle de producto con ID: " + idDetalleProducto));
    }

    private Producto buscarProductoPorId(int idProducto) throws Exception {
        // Asumiendo que productoRepository.findAll() devuelve todos los registros
        return productoRepository.findAll().stream()
                .filter(p -> p.getId() == idProducto)
                .findFirst()
                .orElseThrow(() -> new Exception("Producto no encontrado con ID: " + idProducto));
    }

    // --- IMPLEMENTACIÓN DE MÉTODOS PÚBLICOS ---

    @Override
    public void modificarCantidad(int cantidad, int idDetalleProducto, int idProducto) throws Exception {
        // Se reutiliza la búsqueda
        DetalleProducto detalleProductoEncontrado = buscarDetalleProductoPorId(idDetalleProducto);

        if (validarDatos(cantidad, idProducto)) {
            detalleProductoEncontrado.setCantidad(cantidad);
        }
        detalleProductoRepository.save(detalleProductoEncontrado);
    }

    @Override
    public double calcularSubTotal(int idDetalleProducto, int idProducto) throws Exception {
        // Se reutilizan las búsquedas
        DetalleProducto detalleProductoEncontrado = buscarDetalleProductoPorId(idDetalleProducto);
        Producto productoEncontrado = buscarProductoPorId(idProducto);

        // La validación del producto se hace dentro de buscarProductoPorId
        return detalleProductoEncontrado.getCantidad() * productoEncontrado.getPrecio();
    }

    @Override
    public void crearDetalleProducto(int idProducto, int cantidad) throws Exception {
        if (validarDatos(cantidad, idProducto)) {
            DetalleProducto detalleProducto = new DetalleProducto();
            detalleProducto.setCantidad(cantidad);

            // Se reutiliza la búsqueda
            Producto productoEncontrado = buscarProductoPorId(idProducto);
            detalleProducto.setProducto(productoEncontrado);

            detalleProductoRepository.save(detalleProducto); // Asegúrate de guardar el nuevo detalle
        } else {
        }
    }

    @Override
    public DetalleProducto obtenerDetalleProducto(int idDetalleProducto) throws Exception {
        // Se reutiliza la búsqueda
        return buscarDetalleProductoPorId(idDetalleProducto);
    }

    public boolean validarDatos(int cantidad, int idProducto) throws Exception {
        // Se reutiliza la búsqueda
        Producto productoEncontrado = buscarProductoPorId(idProducto);

        // La validación de productoEncontrado == null ya no es necesaria,
        // ya que buscarProductoPorId lanza la excepción.

        List<Producto> productos = productoRepository.findAll();

        // Validación de stock:
        if (productos.isEmpty()) {
            throw new Exception("Parece que no hay productos disponibles.");
        }
        if (cantidad < 0) {
            throw new Exception("No puedes agregar una cantidad negativa.");
        }
        if (cantidad > productoEncontrado.getCantidad()) {
            throw new Exception("No hay suficiente producto en stock (Disponible: " + productoEncontrado.getCantidad() + ")");
        }

        return true;
    }
}