package com.uniquindio.rappicarrito.repository;

import com.uniquindio.rappicarrito.model.Carrito;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarritoRepository extends JpaRepository<Carrito, Integer> {
}
