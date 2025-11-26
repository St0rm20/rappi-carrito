package com.uniquindio.rappicarrito.services.adapter;

import com.uniquindio.rappicarrito.services.impl.UserServiceImpl; // O la interfaz UserService
import com.uniquindio.rappicarrito.view_model.UsuarioViewModel;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AUserService {

    private final UserServiceImpl userService;

    /**
     * Guarda la dirección para el usuario ID 1
     */
    public void guardarDireccion(String nuevaDireccion) {
        userService.registarDireccion(nuevaDireccion);
    }

    /**
     * Obtiene la dirección actual para mostrarla en el TextField al iniciar
     */
    public String obtenerDireccionActual() {
        return userService.obtenerDireccionActual();
    }
}