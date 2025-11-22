package com.uniquindio.rappicarrito.services.impl;

import com.uniquindio.rappicarrito.model.DetalleProducto;
import com.uniquindio.rappicarrito.model.Producto;
import com.uniquindio.rappicarrito.repository.DetalleProductoRepository;
import com.uniquindio.rappicarrito.repository.ProductoRepository;
import com.uniquindio.rappicarrito.services.def.DetalleProductoService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@NoArgsConstructor
@AllArgsConstructor
public class DetalleProductoServiceImpl implements DetalleProductoService {

    ProductoRepository productoRepository;
    DetalleProductoRepository detalleProductoRepository;
    @Override
    public void modificarCantidad(int cantidad, int idDetalleProducto, int idProducto) throws Exception {
        List<DetalleProducto> detalleProductos = detalleProductoRepository.findAll();
        DetalleProducto detalleProductoEncontrado = detalleProductos.stream()
                .filter(p -> p.getId() == idDetalleProducto)
                .findAny()
                .orElse(null);
        if (detalleProductoEncontrado == null) {
            throw new Exception("No se encontro el detalle de producto");
        }
        if(validarDatos(cantidad, idProducto)){
            detalleProductoEncontrado.setCantidad(cantidad);
        }
        detalleProductoRepository.save(detalleProductoEncontrado);//Tiene el mismo id, se actualiza, no se crea otro registro
    }

    @Override
    public double calcularSubTotal(int idDetalleProducto, int idProducto) throws Exception {
        double subTotal = 0;
        List<DetalleProducto> detalleProductos = detalleProductoRepository.findAll();
        DetalleProducto detalleProductoEncontrado = detalleProductos.stream()
                .filter(p -> p.getId() == idDetalleProducto)
                .findAny()
                .orElse(null);
        List<Producto> productos = productoRepository.findAll();
        Producto productoEncontrado = productos.stream()
                .filter(p -> p.getId() == idProducto) // Se usa .filter() en lugar de findAny() con Predicate
                .findAny()                             // .findAny() devuelve un Optional<Producto>
                .orElse(null);                         // Se usa .orElse() para obtener el Producto o null si no se encuentra
        if(productoEncontrado == null) {
            throw new Exception("Product does not exist");
        }
        if(detalleProductoEncontrado == null) {
            throw new Exception("Detalle producto does not exist");
        }
        subTotal = detalleProductoEncontrado.getCantidad()*productoEncontrado.getPrecio();
        return subTotal;
    }

    @Override
    public void crearDetalleProducto(int idProducto, int cantidad) throws Exception {
        if(validarDatos(cantidad,idProducto)) {
            DetalleProducto detalleProducto = new DetalleProducto();
            detalleProducto.setCantidad(cantidad);
            List<Producto> productos = productoRepository.findAll();
            Producto productoEncontrado = productos.stream()
                    .filter(p -> p.getId() == idProducto) // Se usa .filter() en lugar de findAny() con Predicate
                    .findAny()                             // .findAny() devuelve un Optional<Producto>
                    .orElse(null);
            detalleProducto.setProducto(productoEncontrado);
        }else{
            throw new Exception("Cannot create detalle producto");
        }
    }

    public boolean validarDatos(int cantidad, int idProducto) throws Exception {

        boolean result = true;
        List<Producto> productos = productoRepository.findAll();
        Producto productoEncontrado = productos.stream()
                .filter(p -> p.getId() == idProducto) // Se usa .filter() en lugar de findAny() con Predicate
                .findAny()                             // .findAny() devuelve un Optional<Producto>
                .orElse(null);                         // Se usa .orElse() para obtener el Producto o null si no se encuentra
        if(productoEncontrado == null) {
            result = false;
            throw new Exception("Product does not exist");
        }
        if(productos.size()==0){
            result = false;
            throw new Exception("It seems like theres is no avaible products");
        }
        if(cantidad < 0){
            result= false;
            throw  new Exception("You cannot add a negativa amount");
        }

        if(cantidad > productoEncontrado.getCantidad()){
            result= false;
            throw new Exception("There's no such product in stock");
        }
        return result;
    }
}
