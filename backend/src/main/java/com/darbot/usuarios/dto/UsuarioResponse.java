package com.darbot.usuarios.dto;

import com.darbot.usuarios.entity.Usuario;

import java.time.LocalDateTime;
import java.util.Set;

public record UsuarioResponse(Long id, String nombre, String apellido, String correo, Boolean activo,
                              LocalDateTime fechaCreacion, LocalDateTime fechaActualizacion, Set<String> roles) {
    public static UsuarioResponse from(Usuario usuario) {
        return new UsuarioResponse(usuario.getId(), usuario.getNombre(), usuario.getApellido(), usuario.getCorreo(),
                usuario.getActivo(), usuario.getFechaCreacion(), usuario.getFechaActualizacion(),
                usuario.getRoles().stream().map(rol -> rol.getNombre()).collect(java.util.stream.Collectors.toSet()));
    }
}
