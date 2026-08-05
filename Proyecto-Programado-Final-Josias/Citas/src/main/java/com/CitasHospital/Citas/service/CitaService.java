package com.CitasHospital.Citas.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.CitasHospital.Citas.model.Cita;
import com.CitasHospital.Citas.model.DisponibilidadMedico;
import com.CitasHospital.Citas.model.EstadoCita;
import com.CitasHospital.Citas.model.Medico;
import com.CitasHospital.Citas.model.Usuario;
import com.CitasHospital.Citas.repository.CitaRepository;
import com.CitasHospital.Citas.repository.DisponibilidadMedicoRepository;
import com.CitasHospital.Citas.repository.UsuarioRepository;

@Service
public class CitaService {
    private static final List<EstadoCita> ESTADOS_ACTIVOS =
            List.of(EstadoCita.PENDIENTE, EstadoCita.CONFIRMADA);

    private final CitaRepository citaRepository;
    private final UsuarioRepository usuarioRepository;
    private final DisponibilidadMedicoRepository disponibilidadMedicoRepository;

    public CitaService(CitaRepository citaRepository, UsuarioRepository usuarioRepository,
            DisponibilidadMedicoRepository disponibilidadMedicoRepository) {
        this.citaRepository = citaRepository;
        this.usuarioRepository = usuarioRepository;
        this.disponibilidadMedicoRepository = disponibilidadMedicoRepository;
    }

    @Transactional(readOnly = true)
    public Cita buscarPorId(Long id) {
        return citaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No se encontro la cita."));
    }

    @Transactional(readOnly = true)
    public List<Cita> listarTodas() {
        return citaRepository.findAllByOrderByFechaDescHoraInicioDesc();
    }

    @Transactional(readOnly = true)
    public List<Cita> filtrarCitas(EstadoCita estado, Long medicoId, String especialidad, LocalDate fechaDesde,
            LocalDate fechaHasta) {
        if (fechaDesde != null && fechaHasta != null && fechaDesde.isAfter(fechaHasta)) {
            throw new IllegalArgumentException("El rango de fechas no es valido.");
        }

        return listarTodas()
                .stream()
                .filter(cita -> estado == null || cita.getEstado() == estado)
                .filter(cita -> medicoId == null
                        || (cita.getMedico() != null && cita.getMedico().getId().equals(medicoId)))
                .filter(cita -> especialidad == null || especialidad.isBlank()
                        || (cita.getMedico() != null
                                && cita.getMedico().getEspecialidad() != null
                                && cita.getMedico().getEspecialidad().toLowerCase()
                                        .contains(especialidad.toLowerCase())))
                .filter(cita -> fechaDesde == null || !cita.getFecha().isBefore(fechaDesde))
                .filter(cita -> fechaHasta == null || !cita.getFecha().isAfter(fechaHasta))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<Cita> listarHistorialUsuario(String correoUsuario) {
        validarCorreoUsuario(correoUsuario);

        Usuario usuario = buscarUsuarioPorCorreo(correoUsuario);

        return citaRepository.findByUsuarioIdOrderByFechaDescHoraInicioDesc(usuario.getId());
    }

    @Transactional
    public Cita reservarCita(String correoUsuario, Long disponibilidadId) {
        validarCorreoUsuario(correoUsuario);

        if (disponibilidadId == null) {
            throw new IllegalArgumentException("La disponibilidad es obligatoria.");
        }

        Usuario usuario = usuarioRepository.buscarPorCorreoConBloqueo(correoUsuario)
                .orElseThrow(() -> new IllegalArgumentException("No se encontro el usuario."));

        if (!usuario.isActivo()) {
            throw new IllegalArgumentException("El usuario no esta activo.");
        }

        DisponibilidadMedico disponibilidad = disponibilidadMedicoRepository.buscarPorIdConBloqueo(disponibilidadId)
                .orElseThrow(() -> new IllegalArgumentException("No se encontro la disponibilidad."));

        Medico medico = disponibilidad.getMedico();

        if (medico == null || !medico.isActivo()) {
            throw new IllegalArgumentException("El medico no esta activo.");
        }

        validarInicioDisponibilidadFuturo(disponibilidad);

        if (disponibilidad.isOcupado()) {
            throw new IllegalArgumentException("El espacio seleccionado ya no esta disponible.");
        }

        if (citaRepository.existsByDisponibilidadIdAndEstadoIn(disponibilidadId, ESTADOS_ACTIVOS)) {
            throw new IllegalArgumentException("El espacio seleccionado ya no esta disponible.");
        }

        validarSolapamientoUsuario(usuario.getId(), disponibilidad);

        Cita cita = new Cita(usuario, medico, disponibilidad, disponibilidad.getFecha(),
                disponibilidad.getHoraInicio(), disponibilidad.getHoraFin(), EstadoCita.PENDIENTE);

        disponibilidad.setOcupado(true);
        disponibilidadMedicoRepository.save(disponibilidad);

        return citaRepository.save(cita);
    }

    @Transactional
    public Cita confirmarCita(Long citaId) {
        validarCitaId(citaId);

        Cita cita = citaRepository.buscarPorIdConBloqueo(citaId)
                .orElseThrow(() -> new IllegalArgumentException("No se encontro la cita."));

        if (cita.getEstado() != EstadoCita.PENDIENTE) {
            throw new IllegalArgumentException("Solo se pueden confirmar citas pendientes.");
        }

        cita.setEstado(EstadoCita.CONFIRMADA);

        return citaRepository.save(cita);
    }

    @Transactional
    public Cita cancelarCitaUsuario(Long citaId, String correoUsuario) {
        validarCitaId(citaId);
        validarCorreoUsuario(correoUsuario);

        Cita cita = citaRepository.buscarPorIdConBloqueo(citaId)
                .orElseThrow(() -> new IllegalArgumentException("No se encontro la cita."));

        Usuario usuario = buscarUsuarioPorCorreo(correoUsuario);

        if (!cita.getUsuario().getId().equals(usuario.getId())) {
            throw new IllegalArgumentException("No tiene permiso para cancelar esta cita.");
        }

        if (cita.getEstado() == EstadoCita.CANCELADA) {
            throw new IllegalArgumentException("La cita ya se encuentra cancelada.");
        }

        LocalDateTime inicioCita = LocalDateTime.of(cita.getFecha(), cita.getHoraInicio());

        if (!inicioCita.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("No se puede cancelar una cita cuya fecha y hora ya ocurrieron.");
        }

        return cancelarYLiberarEspacio(cita);
    }

    @Transactional
    public Cita cancelarCitaAdministrador(Long citaId) {
        validarCitaId(citaId);

        Cita cita = citaRepository.buscarPorIdConBloqueo(citaId)
                .orElseThrow(() -> new IllegalArgumentException("No se encontro la cita."));

        if (cita.getEstado() == EstadoCita.CANCELADA) {
            throw new IllegalArgumentException("La cita ya se encuentra cancelada.");
        }

        return cancelarYLiberarEspacio(cita);
    }

    private Cita cancelarYLiberarEspacio(Cita cita) {
        DisponibilidadMedico disponibilidad = disponibilidadMedicoRepository
                .buscarPorIdConBloqueo(cita.getDisponibilidad().getId())
                .orElseThrow(() -> new IllegalArgumentException("No se encontro la disponibilidad asociada."));

        cita.setEstado(EstadoCita.CANCELADA);
        disponibilidad.setOcupado(false);

        disponibilidadMedicoRepository.save(disponibilidad);

        return citaRepository.save(cita);
    }

    private void validarInicioDisponibilidadFuturo(DisponibilidadMedico disponibilidad) {
        LocalDateTime inicio = LocalDateTime.of(
                disponibilidad.getFecha(),
                disponibilidad.getHoraInicio()
        );

        if (!inicio.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("No se pueden reservar citas en fechas u horas pasadas.");
        }
    }

    private void validarSolapamientoUsuario(Long usuarioId, DisponibilidadMedico disponibilidad) {
        boolean existeSolapamiento = citaRepository.findByUsuarioIdAndEstadoIn(usuarioId, ESTADOS_ACTIVOS)
                .stream()
                .filter(citaExistente -> citaExistente.getFecha().equals(disponibilidad.getFecha()))
                .anyMatch(citaExistente ->
                        disponibilidad.getHoraInicio().isBefore(citaExistente.getHoraFin())
                                && disponibilidad.getHoraFin().isAfter(citaExistente.getHoraInicio()));

        if (existeSolapamiento) {
            throw new IllegalArgumentException("El usuario ya tiene otra cita activa en ese horario.");
        }
    }

    private Usuario buscarUsuarioPorCorreo(String correoUsuario) {
        return usuarioRepository.findByCorreo(correoUsuario)
                .orElseThrow(() -> new IllegalArgumentException("No se encontro el usuario."));
    }

    private void validarCorreoUsuario(String correoUsuario) {
        if (correoUsuario == null || correoUsuario.isBlank()) {
            throw new IllegalArgumentException("El correo del usuario es obligatorio.");
        }
    }

    private void validarCitaId(Long citaId) {
        if (citaId == null) {
            throw new IllegalArgumentException("La cita es obligatoria.");
        }
    }
}
