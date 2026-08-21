package com.darbot.auth.dto;

import com.darbot.usuarios.entity.Usuario;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private String nombreCompleto;
    private String rol;
    private Boolean activo;

    public static UserResponse from(Usuario usuario) {
        String nombreCompleto = usuario.getNombre() + " " + usuario.getApellido();
        String rol = usuario.getRoles().stream().findFirst().map(r -> r.getNombre()).orElse(null);
        return UserResponse.builder()
                .id(usuario.getId())
                .username(usuario.getUsername())
                .email(usuario.getCorreo())
                .nombreCompleto(nombreCompleto)
                .rol(rol)
                .activo(usuario.getActivo())
                .build();
    }
}
