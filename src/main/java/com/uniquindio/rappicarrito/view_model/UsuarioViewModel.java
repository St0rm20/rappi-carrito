package com.uniquindio.rappicarrito.view_model;

import com.uniquindio.rappicarrito.model.Usuario;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UsuarioViewModel {
    private String nombre;
    private String direccion;

    public static UsuarioViewModel fromEntity(Usuario usuario) {
        if (usuario == null) return new UsuarioViewModel("Usuario", "");
        return UsuarioViewModel.builder()
                .nombre(usuario.getNombre())
                .direccion(usuario.getDireccion())
                .build();
    }
}