package com.CitasHospital.Citas.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.CitasHospital.Citas.model.DisponibilidadMedico;
import com.CitasHospital.Citas.model.Medico;
import com.CitasHospital.Citas.repository.DisponibilidadMedicoRepository;
import com.CitasHospital.Citas.repository.MedicoRepository;

@Service
public class DisponibilidadMedicoService {
    private final DisponibilidadMedicoRepository disponibilidadMedicoRepository;
    private final MedicoRepository medicoRepository;

    public DisponibilidadMedicoService(DisponibilidadMedicoRepository disponibilidadMedicoRepository,
            MedicoRepository medicoRepository) {
        this.disponibilidadMedicoRepository = disponibilidadMedicoRepository;
        this.medicoRepository = medicoRepository;
    }

    public List<DisponibilidadMedico> listarDisponibilidades() {
        return disponibilidadMedicoRepository.findAll();
    }

    public List<DisponibilidadMedico> listarPorMedico(Long medicoId) {
        return disponibilidadMedicoRepository.findByMedicoId(medicoId);
    }

    public List<DisponibilidadMedico> listarDisponibles() {
        return disponibilidadMedicoRepository.findByOcupadoFalse();
    }

    public DisponibilidadMedico buscarPorId(Long id) {
        return disponibilidadMedicoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No se encontro la disponibilidad."));
    }

    public DisponibilidadMedico crearDisponibilidad(Long medicoId, LocalDate fecha, LocalTime horaInicio,
            LocalTime horaFin) {
        Medico medico = medicoRepository.findById(medicoId)
                .orElseThrow(() -> new IllegalArgumentException("No se encontro el medico."));

        if (!medico.isActivo()) {
            throw new IllegalArgumentException("El medico no esta activo.");
        }

        validarHorario(fecha, horaInicio, horaFin);

        DisponibilidadMedico disponibilidad = new DisponibilidadMedico(medico, fecha, horaInicio, horaFin);
        disponibilidad.setOcupado(false);

        return disponibilidadMedicoRepository.save(disponibilidad);
    }

    public DisponibilidadMedico actualizarDisponibilidad(Long id, LocalDate fecha, LocalTime horaInicio,
            LocalTime horaFin) {
        validarHorario(fecha, horaInicio, horaFin);

        DisponibilidadMedico disponibilidad = buscarPorId(id);
        disponibilidad.setFecha(fecha);
        disponibilidad.setHoraInicio(horaInicio);
        disponibilidad.setHoraFin(horaFin);

        return disponibilidadMedicoRepository.save(disponibilidad);
    }

    public void marcarComoOcupado(Long id) {
        DisponibilidadMedico disponibilidad = buscarPorId(id);
        disponibilidad.setOcupado(true);
        disponibilidadMedicoRepository.save(disponibilidad);
    }

    public void marcarComoDisponible(Long id) {
        DisponibilidadMedico disponibilidad = buscarPorId(id);
        disponibilidad.setOcupado(false);
        disponibilidadMedicoRepository.save(disponibilidad);
    }

    public void eliminarDisponibilidad(Long id) {
        DisponibilidadMedico disponibilidad = buscarPorId(id);
        disponibilidadMedicoRepository.delete(disponibilidad);
    }

    // Revisa que el horario tenga datos validos
    private void validarHorario(LocalDate fecha, LocalTime horaInicio, LocalTime horaFin) {
        if (fecha == null) {
            throw new IllegalArgumentException("La fecha es obligatoria.");
        }

        if (horaInicio == null || horaFin == null) {
            throw new IllegalArgumentException("La hora de inicio y fin son obligatorias.");
        }

        if (!horaFin.isAfter(horaInicio)) {
            throw new IllegalArgumentException("La hora final debe ser despues de la hora inicial.");
        }
    }
}
