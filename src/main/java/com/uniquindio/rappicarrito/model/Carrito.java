package com.uniquindio.rappicarrito.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity(
        name = "carritos"
)

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class Carrito {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Boolean estado;

    @OneToMany(fetch = FetchType.LAZY)
    private List<Producto> productos;

}
