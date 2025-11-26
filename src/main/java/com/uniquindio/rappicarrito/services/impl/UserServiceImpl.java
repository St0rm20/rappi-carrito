package com.uniquindio.rappicarrito.services.impl;

import com.uniquindio.rappicarrito.model.Usuario;
import com.uniquindio.rappicarrito.repository.UsuarioRepository;
import com.uniquindio.rappicarrito.services.def.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public String registarDireccion(String direccion) {
        // REGLA DE ORO: SIEMPRE USAR ID 1
        Long idFijo = 1L;

        Usuario user = usuarioRepository.findById(Math.toIntExact(idFijo)).orElse(null);

        if (user == null) {
            // Si la base de datos está vacía, creamos el usuario 1 obligatoriamente
            user = new Usuario();
            // user.setId(1L); // Dependiendo de la DB, el ID se autogenera, pero Spring lo manejará
            user.setNombre("Usuario Default");
            user.setEmail("usuario@rappi.com");
        }

        user.setDireccion(direccion);
        usuarioRepository.save(user); // Guarda o Actualiza

        return user.getDireccion();
    }

    // Método extra útil para cargar la dirección al abrir la app
    public String obtenerDireccionActual() {
        return usuarioRepository.findById(1)
                .map(Usuario::getDireccion)
                .orElse("");
    }
}