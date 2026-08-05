package com.CitasHospital.Citas.controller;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.CitasHospital.Citas.model.Cita;
import com.CitasHospital.Citas.model.EstadoCita;
import com.CitasHospital.Citas.service.CitaService;
import com.CitasHospital.Citas.service.DisponibilidadMedicoService;
import com.CitasHospital.Citas.service.MedicoService;

@Controller
public class CitaController {
    private final CitaService citaService;
    private final DisponibilidadMedicoService disponibilidadService;
    private final MedicoService medicoService;

    public CitaController(CitaService citaService, DisponibilidadMedicoService disponibilidadService,
            MedicoService medicoService) {
        this.citaService = citaService;
        this.disponibilidadService = disponibilidadService;
        this.medicoService = medicoService;
    }

    @GetMapping("/citas/disponibles")
    public String listarEspaciosDisponibles(Model model) {
        model.addAttribute("disponibilidades", disponibilidadService.listarDisponiblesFuturas());
        return "citas-disponibles";
    }

    @PostMapping("/citas/reservar/{disponibilidadId}")
    public String reservar(@PathVariable Long disponibilidadId, Principal principal,
            RedirectAttributes redirectAttributes) {
        try {
            citaService.reservarCita(principal.getName(), disponibilidadId);
            redirectAttributes.addFlashAttribute("exito",
                    "Reserva creada correctamente. La cita se encuentra pendiente.");
            return "redirect:/citas/mis-citas";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/citas/disponibles";
        }
    }

    @GetMapping("/citas/mis-citas")
    public String listarMisCitas(Principal principal, Model model) {
        model.addAttribute("citas", citaService.listarHistorialUsuario(principal.getName()));
        return "mis-citas";
    }

    @PostMapping("/citas/cancelar/{citaId}")
    public String cancelarCitaUsuario(@PathVariable Long citaId, Principal principal,
            RedirectAttributes redirectAttributes) {
        try {
            citaService.cancelarCitaUsuario(citaId, principal.getName());
            redirectAttributes.addFlashAttribute("exito", "Cita cancelada correctamente.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/citas/mis-citas";
    }

    @GetMapping("/admin/citas")
    public String listarCitasAdmin(@RequestParam(required = false) EstadoCita estado,
            @RequestParam(required = false) Long medicoId,
            @RequestParam(required = false) String especialidad,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta,
            Model model) {
        List<Cita> citas;

        try {
            citas = citaService.filtrarCitas(estado, medicoId, especialidad, fechaDesde, fechaHasta);
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            citas = citaService.listarTodas();
        }

        cargarModeloAdmin(model, citas, estado, medicoId, especialidad, fechaDesde, fechaHasta);
        return "citas-admin";
    }

    @PostMapping("/admin/citas/confirmar/{citaId}")
    public String confirmarCita(@PathVariable Long citaId, RedirectAttributes redirectAttributes) {
        try {
            citaService.confirmarCita(citaId);
            redirectAttributes.addFlashAttribute("exito", "Cita confirmada correctamente.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/admin/citas";
    }

    @PostMapping("/admin/citas/cancelar/{citaId}")
    public String cancelarCitaAdministrador(@PathVariable Long citaId, RedirectAttributes redirectAttributes) {
        try {
            citaService.cancelarCitaAdministrador(citaId);
            redirectAttributes.addFlashAttribute("exito", "Cita cancelada correctamente.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/admin/citas";
    }

    private void cargarModeloAdmin(Model model, List<Cita> citas, EstadoCita estado, Long medicoId,
            String especialidad, LocalDate fechaDesde, LocalDate fechaHasta) {
        model.addAttribute("citas", citas);
        model.addAttribute("estados", EstadoCita.values());
        model.addAttribute("medicos", medicoService.listarMedicos());
        model.addAttribute("estado", estado);
        model.addAttribute("medicoId", medicoId);
        model.addAttribute("especialidad", especialidad);
        model.addAttribute("fechaDesde", fechaDesde);
        model.addAttribute("fechaHasta", fechaHasta);
    }
}
