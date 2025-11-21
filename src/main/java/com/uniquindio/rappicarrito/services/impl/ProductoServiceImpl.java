package com.uniquindio.rappicarrito.services.impl;

import com.uniquindio.rappicarrito.model.Producto;
import com.uniquindio.rappicarrito.repository.ProductoRepository;
import com.uniquindio.rappicarrito.services.def.ProductoService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ProductoServiceImpl implements ProductoService {

    private ProductoRepository productoRepository;

    @Override
    public List<Producto> listarProductos() {
        return productoRepository.findAll();
    }

    @Override
    public Producto obtenerProductoPorId(int id) {
        return productoRepository.findById(id).orElse(null);
    }

    @Override
    public Producto guardarProducto(Producto producto) {
        return productoRepository.save(producto);
    }

    @Override
    public void eliminarProducto(int id) {
        productoRepository.deleteById(id);
    }

    @Override
    public void actualizarProducto(Producto producto) {
        productoRepository.save(producto);
    }
}
