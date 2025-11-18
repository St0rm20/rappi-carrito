package com.uniquindio.rappicarrito.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

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
    private Long id;



}
