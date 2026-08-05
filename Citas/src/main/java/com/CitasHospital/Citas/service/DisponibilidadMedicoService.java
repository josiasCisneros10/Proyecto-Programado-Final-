package com.CitasHospital.Citas.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.CitasHospital.Citas.model.DisponibilidadMedico;
import com.CitasHospital.Citas.model.EstadoCita;
import com.CitasHospital.Citas.model.Medico;
import com.CitasHospital.Citas.repository.CitaRepository;
import com.CitasHospital.Citas.repository.DisponibilidadMedicoRepository;
import com.CitasHospital.Citas.repository.MedicoRepository;

@Service
public class DisponibilidadMedicoService {
    private static final List<EstadoCita> ESTADOS_ACTIVOS =
            List.of(EstadoCita.PENDIENTE, EstadoCita.CONFIRMADA);

    private final DisponibilidadMedicoRepository disponibilidadMedicoRepository;
    private final MedicoRepository medicoRepository;
    private final CitaRepository citaRepository;

    public DisponibilidadMedicoService(DisponibilidadMedicoRepository disponibilidadMedicoRepository,
            MedicoRepository medicoRepository, CitaRepository citaRepository) {
        this.disponibilidadMedicoRepository = disponibilidadMedicoRepository;
        this.medicoRepository = medicoRepository;
        this.citaRepository = citaRepository;
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

    public List<DisponibilidadMedico> listarDisponiblesFuturas() {
        LocalDateTime ahora = LocalDateTime.now();

        return disponibilidadMedicoRepository.findByOcupadoFalse()
                .stream()
                .filter(disponibilidad -> disponibilidad.getMedico() != null)
                .filter(disponibilidad -> disponibilidad.getMedico().isActivo())
                .filter(disponibilidad -> LocalDateTime.of(
                        disponibilidad.getFecha(),
                        disponibilidad.getHoraInicio()).isAfter(ahora))
                .sorted(Comparator.comparing(DisponibilidadMedico::getFecha)
                        .thenComparing(DisponibilidadMedico::getHoraInicio)
                        .thenComparing(disponibilidad -> disponibilidad.getMedico().getNombre(),
                                Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)))
                .toList();
    }

    public DisponibilidadMedico buscarPorId(Long id) {
        return disponibilidadMedicoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No se encontro la disponibilidad."));
    }

    @Transactional
    public DisponibilidadMedico crearDisponibilidad(Long medicoId, LocalDate fecha, LocalTime horaInicio,
            LocalTime horaFin) {
        Medico medico = medicoRepository.findById(medicoId)
                .orElseThrow(() -> new IllegalArgumentException("No se encontro el medico."));

        if (!medico.isActivo()) {
            throw new IllegalArgumentException("El medico no esta activo.");
        }

        validarHorario(fecha, horaInicio, horaFin);
        validarHorarioFuturo(fecha, horaInicio);

        if (disponibilidadMedicoRepository
                .existsByMedicoIdAndFechaAndHoraInicioAndHoraFin(
                        medicoId, fecha, horaInicio, horaFin)) {
            throw new IllegalArgumentException(
                    "El médico ya tiene una disponibilidad registrada en esa fecha y horario.");
        }

        validarCruceHorario(medicoId, fecha, horaInicio, horaFin, null);

        DisponibilidadMedico disponibilidad = new DisponibilidadMedico(medico, fecha, horaInicio, horaFin);
        disponibilidad.setOcupado(false);

        return disponibilidadMedicoRepository.save(disponibilidad);
    }

    @Transactional
    public DisponibilidadMedico actualizarDisponibilidad(Long id, LocalDate fecha, LocalTime horaInicio,
            LocalTime horaFin) {
        DisponibilidadMedico disponibilidad = buscarPorId(id);

        if (citaRepository.existsByDisponibilidadIdAndEstadoIn(id, ESTADOS_ACTIVOS)) {
            throw new IllegalArgumentException("No se puede editar una disponibilidad con una cita activa.");
        }

        validarHorario(fecha, horaInicio, horaFin);
        validarHorarioFuturo(fecha, horaInicio);

        Long medicoId = disponibilidad.getMedico().getId();

        if (disponibilidadMedicoRepository
                .existsByMedicoIdAndFechaAndHoraInicioAndHoraFinAndIdNot(
                        medicoId, fecha, horaInicio, horaFin, id)) {
            throw new IllegalArgumentException(
                    "El médico ya tiene una disponibilidad registrada en esa fecha y horario.");
        }

        validarCruceHorario(medicoId, fecha, horaInicio, horaFin, id);

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

    @Transactional
    public void marcarComoDisponible(Long id) {
        if (citaRepository.existsByDisponibilidadIdAndEstadoIn(id, ESTADOS_ACTIVOS)) {
            throw new IllegalArgumentException("No se puede liberar una disponibilidad con una cita activa.");
        }

        DisponibilidadMedico disponibilidad = buscarPorId(id);
        disponibilidad.setOcupado(false);
        disponibilidadMedicoRepository.save(disponibilidad);
    }

    @Transactional
    public void eliminarDisponibilidad(Long id) {
        DisponibilidadMedico disponibilidad = buscarPorId(id);

        if (citaRepository.existsByDisponibilidadId(id)) {
            throw new IllegalArgumentException("No se puede eliminar una disponibilidad con citas asociadas.");
        }

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

    private void validarHorarioFuturo(LocalDate fecha, LocalTime horaInicio) {
        LocalDateTime inicioDisponibilidad = LocalDateTime.of(fecha, horaInicio);

        if (!inicioDisponibilidad.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException(
                    "La disponibilidad debe registrarse en una fecha y hora futura.");
        }
    }

    private void validarCruceHorario(Long medicoId, LocalDate fecha, LocalTime horaInicio, LocalTime horaFin,
            Long idExcluir) {
        boolean existeCruceHorario = disponibilidadMedicoRepository.findByMedicoIdAndFecha(medicoId, fecha)
                .stream()
                .filter(disponibilidad -> idExcluir == null || !idExcluir.equals(disponibilidad.getId()))
                .anyMatch(disponibilidadExistente ->
                        horaInicio.isBefore(disponibilidadExistente.getHoraFin())
                                && horaFin.isAfter(disponibilidadExistente.getHoraInicio()));

        if (existeCruceHorario) {
            throw new IllegalArgumentException(
                    "El médico ya tiene una disponibilidad que se cruza con ese horario.");
        }
    }
}
