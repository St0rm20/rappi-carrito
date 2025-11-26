package com.uniquindio.rappicarrito.model;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Boolean estado;

    private String nombre;

    private Double precio;

    private String descripcion;

    private String imagenUrl;

    private Integer unidadesDisponibles;

    private Integer cantidad;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "etiquetas_productos",
            joinColumns = @JoinColumn(name = "producto_id"))
    @Column(name = "etiqueta", length = 500)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private List<String> etiquetas;

}
