package com.uniquindio.rappicarrito.services.impl;

import com.uniquindio.rappicarrito.model.Carrito;
import com.uniquindio.rappicarrito.model.DetalleProducto;
import com.uniquindio.rappicarrito.model.Producto;
import com.uniquindio.rappicarrito.repository.CarritoRepository;
import com.uniquindio.rappicarrito.repository.DetalleProductoRepository;
import com.uniquindio.rappicarrito.repository.ProductoRepository;
import com.uniquindio.rappicarrito.services.def.DetalleProductoService;
import com.uniquindio.rappicarrito.services.def.carritoService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class CarritoServiceImpl implements carritoService {
    CarritoRepository carritoRepository;
    DetalleProductoRepository detalleProductoRepository;
    @Override
    public void vaciarCarrito(int idCarrito) throws Exception {
        List<Carrito> carritos = carritoRepository.findAll();
        Carrito carritoEncontrado = carritos.stream()
                .filter(c -> c.getId() == idCarrito)
                .findAny()
                .orElse(null);
        if(carritoEncontrado == null) {
            throw new Exception("Carrito no encontrado");
        }
        List<DetalleProducto> productos = new ArrayList<>();
        carritoEncontrado.setProductos(productos);
        carritoRepository.save(carritoEncontrado);

    }

    @Override
    public void aniadirProducto(int idDetalleProducto, int idCarrito) throws Exception {
        List<Carrito> carritos = carritoRepository.findAll();
        Carrito carritoEncontrado = carritos.stream()
                .filter(c -> c.getId() == idCarrito)
                .findAny()
                .orElse(null);

        List<DetalleProducto> detalleProductos = detalleProductoRepository.findAll();
        DetalleProducto productoEncontrado = detalleProductos.stream()
                .filter(dp -> dp.getId() == idDetalleProducto)
                .findAny()
                .orElse(null);

        if(productoEncontrado == null) {
            throw new Exception("Detalle producto no encontrado");
        }
        if(carritoEncontrado == null) {
            throw new Exception("Carrito no encontrado");
        }
        carritoEncontrado.getProductos().add(productoEncontrado);
        carritoRepository.save(carritoEncontrado);
    }

    @Override
    public void eliminarProducto(int idDetalleProducto, int idCarrito) throws Exception {
        List<Carrito> carritos = carritoRepository.findAll();
        Carrito carritoEncontrado = carritos.stream()
                .filter(c -> c.getId() == idCarrito)
                .findAny()
                .orElse(null);

        List<DetalleProducto> detalleProductos = detalleProductoRepository.findAll();
        DetalleProducto productoEncontrado = detalleProductos.stream()
                .filter(dp -> dp.getId() == idDetalleProducto)
                .findAny()
                .orElse(null);
        if(productoEncontrado == null) {
            throw new Exception("Detalle producto no encontrado");
        }
        if(carritoEncontrado == null) {
            throw new Exception("Carrito no encontrado");
        }

        carritoEncontrado.getProductos().remove(productoEncontrado);
        carritoRepository.save(carritoEncontrado);
    }

}
