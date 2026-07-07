package com.CitasHospital.Citas.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.CitasHospital.Citas.model.Usuario;
import com.CitasHospital.Citas.service.UsuarioService;

@Controller
public class AuthController {
    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/")
    public String raiz() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/registro")
    public String mostrarRegistro(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "registro";
    }

    @PostMapping("/registro")
    public String registrarUsuario(@ModelAttribute Usuario usuario, Model model) {
        try {
            usuarioService.registrarUsuario(usuario);
            return "redirect:/login?registroExitoso";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("usuario", usuario);
            return "registro";
        }
    }

    @GetMapping("/recuperar-password")
    public String mostrarRecuperacion() {
        return "recuperar-password";
    }

    @PostMapping("/recuperar-password")
    public String recuperarPassword(@RequestParam String correo, Model model) {
        try {
            String token = usuarioService.generarTokenRecuperacion(correo);
            model.addAttribute("mensaje", "Token generado. Copialo para cambiar tu contrasena.");
            model.addAttribute("token", token);
            return "recuperar-password";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "recuperar-password";
        }
    }

    @PostMapping("/cambiar-password")
    public String cambiarPassword(@RequestParam String token, @RequestParam String nuevaPassword, Model model) {
        try {
            usuarioService.cambiarPasswordConToken(token, nuevaPassword);
            return "redirect:/login?passwordCambiada";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "recuperar-password";
        }
    }
}
