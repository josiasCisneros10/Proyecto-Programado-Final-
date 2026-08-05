package com.CitasHospital.Citas.service;

import java.util.List;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.CitasHospital.Citas.model.Rol;
import com.CitasHospital.Citas.model.Usuario;
import com.CitasHospital.Citas.repository.UsuarioRepository;

@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    public List<Usuario> listarUsuariosActivos() {
        return usuarioRepository.findByActivoTrue();
    }

    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No se encontro el usuario."));
    }

    public Usuario buscarPorCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new IllegalArgumentException("No se encontro el usuario con ese correo."));
    }

    public Usuario registrarUsuario(Usuario usuario) {
        validarUsuarioNuevo(usuario);

        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));

        if (usuario.getRol() == null) {
            usuario.setRol(Rol.USUARIO);
        }

        usuario.setActivo(true);
        return usuarioRepository.save(usuario);
    }

    public Usuario actualizarUsuario(Long id, Usuario datosUsuario, String nuevaPassword) {
        validarUsuarioEditado(id, datosUsuario);

        Usuario usuario = buscarPorId(id);
        usuario.setNombre(datosUsuario.getNombre());
        usuario.setCedula(datosUsuario.getCedula());
        usuario.setCorreo(datosUsuario.getCorreo());
        usuario.setContacto(datosUsuario.getContacto());

        if (nuevaPassword != null && !nuevaPassword.isBlank()) {
            usuario.setPassword(passwordEncoder.encode(nuevaPassword));
        }

        return usuarioRepository.save(usuario);
    }

    public void desactivarUsuario(Long id) {
        Usuario usuario = buscarPorId(id);
        usuario.setActivo(false);
        usuarioRepository.save(usuario);
    }

    public String generarTokenRecuperacion(String correo) {
        Usuario usuario = buscarPorCorreo(correo);
        String token = UUID.randomUUID().toString();

        usuario.setTokenRecuperacion(token);
        usuarioRepository.save(usuario);

        return token;
    }

    public void cambiarPasswordConToken(String token, String nuevaPassword) {
        if (token == null || token.trim().isEmpty()) {
            throw new IllegalArgumentException("El token es obligatorio.");
        }

        if (nuevaPassword == null || nuevaPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("La nueva contraseña es obligatoria.");
        }

        Usuario usuario = usuarioRepository.findByTokenRecuperacion(token)
                .orElseThrow(() -> new IllegalArgumentException("Token inválido."));

        usuario.setPassword(passwordEncoder.encode(nuevaPassword));
        usuario.setTokenRecuperacion(null);

        usuarioRepository.save(usuario);
    }

    // Validaciones para registrar usuarios nuevos
    private void validarUsuarioNuevo(Usuario usuario) {
        validarDatosBasicos(usuario);

        if (usuario.getPassword() == null || usuario.getPassword().isBlank()) {
            throw new IllegalArgumentException("La contrasena es obligatoria.");
        }

        if (usuarioRepository.existsByCorreo(usuario.getCorreo())) {
            throw new IllegalArgumentException("Ya existe un usuario con ese correo.");
        }

        if (usuarioRepository.existsByCedula(usuario.getCedula())) {
            throw new IllegalArgumentException("Ya existe un usuario con esa cedula.");
        }
    }

    // Validaciones para editar usuarios existentes
    private void validarUsuarioEditado(Long id, Usuario usuario) {
        validarDatosBasicos(usuario);

        if (usuarioRepository.existsByCorreoAndIdNot(usuario.getCorreo(), id)) {
            throw new IllegalArgumentException("Ya existe otro usuario con ese correo.");
        }

        if (usuarioRepository.existsByCedulaAndIdNot(usuario.getCedula(), id)) {
            throw new IllegalArgumentException("Ya existe otro usuario con esa cedula.");
        }
    }

    private void validarDatosBasicos(Usuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("Los datos del usuario son obligatorios.");
        }

        if (usuario.getNombre() == null || usuario.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio.");
        }

        if (usuario.getCedula() == null || usuario.getCedula().isBlank()) {
            throw new IllegalArgumentException("La cedula es obligatoria.");
        }

        if (usuario.getCorreo() == null || usuario.getCorreo().isBlank()) {
            throw new IllegalArgumentException("El correo es obligatorio.");
        }

        if (!correoValido(usuario.getCorreo())) {
            throw new IllegalArgumentException("El correo no tiene un formato valido.");
        }
    }

    private boolean correoValido(String correo) {
        return correo != null && correo.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    }
}
