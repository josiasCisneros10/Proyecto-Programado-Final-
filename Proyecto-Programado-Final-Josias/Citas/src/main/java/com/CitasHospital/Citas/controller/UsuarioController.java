package com.CitasHospital.Citas.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.CitasHospital.Citas.model.Usuario;
import com.CitasHospital.Citas.service.UsuarioService;

@Controller
public class UsuarioController {
    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/perfil")
    public String verPerfil(Model model, Authentication authentication) {
        String correo = authentication.getName();
        Usuario usuario = usuarioService.buscarPorCorreo(correo);

        model.addAttribute("usuario", usuario);
        return "perfil";
    }

    @PostMapping("/perfil")
    public String actualizarPerfil(@ModelAttribute Usuario usuario,
            @RequestParam(required = false) String nuevaPassword,
            Authentication authentication,
            Model model) {
        String correo = authentication.getName();
        Usuario usuarioActual = usuarioService.buscarPorCorreo(correo);

        try {
            usuarioService.actualizarUsuario(usuarioActual.getId(), usuario, nuevaPassword);
            Usuario usuarioActualizado = usuarioService.buscarPorId(usuarioActual.getId());

            model.addAttribute("mensaje", "Perfil actualizado correctamente.");
            model.addAttribute("usuario", usuarioActualizado);
            return "perfil";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("usuario", usuario);
            return "perfil";
        }
    }

    @GetMapping("/usuarios")
    public String listarUsuarios(Model model) {
        model.addAttribute("usuarios", usuarioService.listarUsuarios());
        return "usuarios-lista";
    }

    @GetMapping("/usuarios/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        Usuario usuario = usuarioService.buscarPorId(id);

        model.addAttribute("usuario", usuario);
        return "usuario-formulario";
    }

    @PostMapping("/usuarios/editar/{id}")
    public String actualizarUsuarioAdmin(@PathVariable Long id,
            @ModelAttribute Usuario usuario,
            @RequestParam(required = false) String nuevaPassword,
            Model model) {
        try {
            usuarioService.actualizarUsuario(id, usuario, nuevaPassword);
            return "redirect:/usuarios?actualizado";
        } catch (IllegalArgumentException e) {
            usuario.setId(id);
            model.addAttribute("error", e.getMessage());
            model.addAttribute("usuario", usuario);
            return "usuario-formulario";
        }
    }

    @GetMapping("/usuarios/desactivar/{id}")
    public String desactivarUsuario(@PathVariable Long id) {
        usuarioService.desactivarUsuario(id);
        return "redirect:/usuarios?desactivado";
    }
}
