package com.CitasHospital.Citas.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.CitasHospital.Citas.model.Rol;
import com.CitasHospital.Citas.model.Usuario;
import com.CitasHospital.Citas.repository.UsuarioRepository;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner crearAdministradorInicial(UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder) {
        return args -> {
            // Usuario inicial para probar rutas de administrador
            if (!usuarioRepository.existsByCorreo("admin@hospital.com")) {
                Usuario admin = new Usuario(
                        "Administrador",
                        "000000000",
                        "admin@hospital.com",
                        "88888888",
                        passwordEncoder.encode("admin123"),
                        Rol.ADMIN);

                admin.setActivo(true);
                usuarioRepository.save(admin);
            }
        };
    }
}
