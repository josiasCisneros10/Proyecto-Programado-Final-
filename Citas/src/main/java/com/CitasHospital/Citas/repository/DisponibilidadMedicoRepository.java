package com.CitasHospital.Citas.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.CitasHospital.Citas.model.DisponibilidadMedico;

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
}
