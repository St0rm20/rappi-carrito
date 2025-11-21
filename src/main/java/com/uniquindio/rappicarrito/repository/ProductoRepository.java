package com.uniquindio.rappicarrito.repository;

import com.uniquindio.rappicarrito.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoRepository extends JpaRepository<Producto,Integer> {
}
