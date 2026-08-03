package com.CitasHospital.Citas.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.CitasHospital.Citas.model.Usuario;

import jakarta.persistence.LockModeType;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByCorreo(String correo);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT u FROM Usuario u WHERE u.correo = :correo")
    Optional<Usuario> buscarPorCorreoConBloqueo(
            @Param("correo") String correo
    );

    boolean existsByCorreo(String correo);

    boolean existsByCedula(String cedula);

    Optional<Usuario> findByTokenRecuperacion(String tokenRecuperacion);

    boolean existsByCorreoAndIdNot(String correo, Long id);

    boolean existsByCedulaAndIdNot(String cedula, Long id);

    List<Usuario> findByActivoTrue();
}
