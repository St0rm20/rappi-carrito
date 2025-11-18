package com.uniquindio.rappicarrito.model;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

@Entity(
    name = "productos"
)

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class Producto {

    @Id
    private Long id;

    private String nombre;

    private Double precio;

    private String descripcion;

    private String imagenUrl;

    @
    private List<String> etiquetas;

}
