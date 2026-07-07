package com.CitasHospital.Citas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.CitasHospital.Citas.model.DisponibilidadMedico;

public interface DisponibilidadMedicoRepository extends JpaRepository<DisponibilidadMedico, Long> {
    List<DisponibilidadMedico> findByMedicoId(Long medicoId);

    List<DisponibilidadMedico> findByOcupadoFalse();
}
