package com.darbot.usuarios.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UsuarioRegistroRequest(
        @NotBlank(message = "El username es obligatorio") @Size(min = 3, max = 50, message = "El username debe tener entre 3 y 50 caracteres") String username,
        @NotBlank(message = "El nombre es obligatorio") @Size(max = 100) String nombre,
        @NotBlank(message = "El apellido es obligatorio") @Size(max = 100) String apellido,
        @NotBlank(message = "El correo es obligatorio") @Email(message = "El correo no tiene un formato válido") @Size(max = 150) String correo,
        @NotBlank(message = "La contraseña es obligatoria") @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres") String password,
        String rol
) {
}
