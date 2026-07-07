package com.CitasHospital.Citas.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.CitasHospital.Citas.model.Medico;
import com.CitasHospital.Citas.repository.MedicoRepository;

@Service
public class MedicoService {
    private final MedicoRepository medicoRepository;

    public MedicoService(MedicoRepository medicoRepository) {
        this.medicoRepository = medicoRepository;
    }

    public List<Medico> listarMedicos() {
        return medicoRepository.findAll();
    }

    public List<Medico> listarMedicosActivos() {
        return medicoRepository.findByActivoTrue();
    }

    public Medico buscarPorId(Long id) {
        return medicoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No se encontro el medico."));
    }

    public Medico guardarMedico(Medico medico) {
        validarMedico(medico);
        medico.setActivo(true);
        return medicoRepository.save(medico);
    }

    public Medico actualizarMedico(Long id, Medico datosMedico) {
        validarMedico(datosMedico);

        Medico medico = buscarPorId(id);
        medico.setNombre(datosMedico.getNombre());
        medico.setEspecialidad(datosMedico.getEspecialidad());

        return medicoRepository.save(medico);
    }

    public void desactivarMedico(Long id) {
        Medico medico = buscarPorId(id);
        medico.setActivo(false);
        medicoRepository.save(medico);
    }

    // Validacion basica del medico
    private void validarMedico(Medico medico) {
        if (medico == null) {
            throw new IllegalArgumentException("Los datos del medico son obligatorios.");
        }

        if (medico.getNombre() == null || medico.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre del medico es obligatorio.");
        }

        if (medico.getEspecialidad() == null || medico.getEspecialidad().isBlank()) {
            throw new IllegalArgumentException("La especialidad es obligatoria.");
        }
    }
}
