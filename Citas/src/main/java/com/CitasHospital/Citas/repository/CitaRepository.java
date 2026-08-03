package com.CitasHospital.Citas.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.CitasHospital.Citas.model.Cita;
import com.CitasHospital.Citas.model.EstadoCita;

import jakarta.persistence.LockModeType;

public interface CitaRepository extends JpaRepository<Cita, Long> {
    List<Cita> findByUsuarioIdOrderByFechaDescHoraInicioDesc(Long usuarioId);

    List<Cita> findAllByOrderByFechaDescHoraInicioDesc();

    List<Cita> findByUsuarioIdAndEstadoIn(
            Long usuarioId,
            Collection<EstadoCita> estados
    );

    boolean existsByDisponibilidadIdAndEstadoIn(
            Long disponibilidadId,
            Collection<EstadoCita> estados
    );

    boolean existsByDisponibilidadId(Long disponibilidadId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Cita c WHERE c.id = :id")
    Optional<Cita> buscarPorIdConBloqueo(@Param("id") Long id);
}
