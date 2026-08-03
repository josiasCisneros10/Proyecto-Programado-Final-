package com.CitasHospital.Citas.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.CitasHospital.Citas.model.DisponibilidadMedico;

import jakarta.persistence.LockModeType;

public interface DisponibilidadMedicoRepository extends JpaRepository<DisponibilidadMedico, Long> {
    List<DisponibilidadMedico> findByMedicoId(Long medicoId);

    List<DisponibilidadMedico> findByOcupadoFalse();

    List<DisponibilidadMedico> findByMedicoIdAndFecha(
            Long medicoId,
            LocalDate fecha
    );

    boolean existsByMedicoIdAndFechaAndHoraInicioAndHoraFin(
            Long medicoId,
            LocalDate fecha,
            LocalTime horaInicio,
            LocalTime horaFin
    );

    boolean existsByMedicoIdAndFechaAndHoraInicioAndHoraFinAndIdNot(
            Long medicoId,
            LocalDate fecha,
            LocalTime horaInicio,
            LocalTime horaFin,
            Long id
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT d FROM DisponibilidadMedico d WHERE d.id = :id")
    Optional<DisponibilidadMedico> buscarPorIdConBloqueo(@Param("id") Long id);
}
