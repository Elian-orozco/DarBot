package com.darbot.config;

import com.darbot.usuarios.entity.Rol;
import com.darbot.usuarios.entity.Usuario;
import com.darbot.usuarios.repository.RolRepository;
import com.darbot.usuarios.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Crear roles si no existen
        Rol adminRol = rolRepository.findByNombre("ADMIN").orElseGet(() -> rolRepository.save(newRol("ADMIN")));
        rolRepository.findByNombre("USER").orElseGet(() -> rolRepository.save(newRol("USER")));
        // Rellenar usernames faltantes usando el correo (antes de crear admin)
        usuarioRepository.findAll().forEach(u -> {
            if (u.getUsername() == null || u.getUsername().isBlank()) {
                String correo = u.getCorreo();
                if (correo != null && !correo.isBlank()) {
                    String inferred = correo.contains("@") ? correo.split("@")[0] : correo;
                    // asegurar unicidad añadiendo sufijo si es necesario
                    String candidate = inferred;
                    int suffix = 1;
                    while (usuarioRepository.existsByUsername(candidate)) {
                        candidate = inferred + suffix;
                        suffix++;
                    }
                    u.setUsername(candidate);
                    usuarioRepository.save(u);
                }
            }
        });

        if (!usuarioRepository.existsByUsername("admin")) {
            Usuario admin = new Usuario();
            admin.setUsername("admin");
            admin.setCorreo("admin@darbot.com");
            admin.setNombre("Administrador");
            admin.setApellido("");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setActivo(true);
            admin.getRoles().add(adminRol);
            admin.setFechaCreacion(LocalDateTime.now());
            admin.setFechaActualizacion(LocalDateTime.now());
            usuarioRepository.save(admin);
        }
    }

    private Rol newRol(String nombre) {
        Rol r = new Rol();
        r.setNombre(nombre);
        return r;
    }
}
