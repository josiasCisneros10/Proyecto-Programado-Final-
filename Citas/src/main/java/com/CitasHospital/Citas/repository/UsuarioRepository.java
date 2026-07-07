package com.CitasHospital.Citas.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.CitasHospital.Citas.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByCorreo(String correo);

    boolean existsByCorreo(String correo);

    boolean existsByCedula(String cedula);

    Optional<Usuario> findByTokenRecuperacion(String tokenRecuperacion);

    boolean existsByCorreoAndIdNot(String correo, Long id);

    boolean existsByCedulaAndIdNot(String cedula, Long id);

    List<Usuario> findByActivoTrue();
}
