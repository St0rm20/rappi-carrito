package com.uniquindio.rappicarrito.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity(name = "carritos")
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

    // CAMBIO IMPORTANTE: FetchType.EAGER
    // CascadeType.ALL ayuda a que si guardas el carrito, guarde los cambios en la lista
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<DetalleProducto> productos;
}