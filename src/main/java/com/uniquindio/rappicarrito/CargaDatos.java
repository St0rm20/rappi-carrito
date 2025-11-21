package com.uniquindio.rappicarrito;

import com.uniquindio.rappicarrito.model.Producto;
import com.uniquindio.rappicarrito.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType; // <--- IMPORTANTE
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class CargaDatos implements CommandLineRunner {

    private final ProductoRepository productoRepository;

    @Autowired
    public CargaDatos(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    // --- AQUÍ ESTÁ EL CAMBIO ---
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(CargaDatos.class);
        // 1. Le decimos que NO es una app web (evita buscar puertos o Tomcat)
        app.setWebApplicationType(WebApplicationType.NONE);
        // 2. Ejecutamos
        app.run(args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("--- 🏁 INICIANDO SCRIPT DE CARGA ---");

        // Limpieza opcional: Descomenta si quieres borrar todo antes de crear
        // productoRepository.deleteAll();

        if (productoRepository.count() == 0) {
            System.out.println("... Creando productos ...");

            Producto p1 = crearp("Hamburguesa Doble", 25000.0, "Doble carne.");
            Producto p2 = crearp("Pizza Peperoni", 32000.0, "Familiar.");
            Producto p3 = crearp("Coca Cola", 8000.0, "1.5 Litros.");
            Producto p4 = crearp("Sushi Roll", 28000.0, "10 piezas.");

            List<Producto> productos = Arrays.asList(p1, p2, p3, p4);
            productoRepository.saveAll(productos);

            System.out.println("✅ ÉXITO: Se guardaron " + productos.size() + " productos.");
        } else {
            System.out.println("⚠️ INFO: La base de datos ya tenía " + productoRepository.count() + " productos.");
        }

        System.out.println("--- 🏁 FINALIZADO ---");
        System.exit(0); // Forzamos el cierre limpio
    }

    private Producto crearp(String nombre, Double precio, String desc) {
        Producto p = new Producto();
        p.setNombre(nombre);
        p.setPrecio(precio);
        p.setDescripcion(desc);
        p.setImagenUrl("https://via.placeholder.com/150");
        p.setEstado(true);
        return p;
    }
}