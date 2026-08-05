package com.CitasHospital.Citas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.CitasHospital.Citas.model.Medico;

public interface MedicoRepository extends JpaRepository<Medico, Long> {
    List<Medico> findByActivoTrue();
}
