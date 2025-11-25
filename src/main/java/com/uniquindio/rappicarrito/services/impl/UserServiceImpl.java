package com.uniquindio.rappicarrito.services.impl;

import com.uniquindio.rappicarrito.model.Usuario;
import com.uniquindio.rappicarrito.repository.UsuarioRepository;
import com.uniquindio.rappicarrito.services.def.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserServiceImpl  implements UserService {

    private final UsuarioRepository userRepository;

    @Override
    public String registarDireccion(String nombre, String direccion) {
        Usuario user = userRepository.findByNombre(nombre);
        user.setDireccion(direccion);
        userRepository.save(user);
        return direccion;
    }
}
