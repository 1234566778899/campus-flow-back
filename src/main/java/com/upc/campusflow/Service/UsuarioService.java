package com.upc.campusflow.Service;

import com.upc.campusflow.DTO.UsuarioDTO;
import com.upc.campusflow.Exception.RecursoNoEncontradoException;
import com.upc.campusflow.Model.Rol;
import com.upc.campusflow.Model.Usuario;
import com.upc.campusflow.Repository.UsuarioRepository;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsuarioService {
    final UsuarioRepository usuarioRepository;
    final RolService rolService;
    final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, RolService rolService, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.rolService = rolService;
        this.passwordEncoder = passwordEncoder;
    }

    // --- Listar Usuarios ---
    public List<UsuarioDTO> listar(){
        List<Usuario> usuarios = usuarioRepository.findAll().stream().filter(Usuario::isEstado).toList();
        ModelMapper modelMapper = new ModelMapper();
        return usuarios.stream()
                .map(usuario -> {
                    UsuarioDTO usuarioDTO = modelMapper.map(usuario, UsuarioDTO.class);
                    //usuarioDTO.setPassword(null);
                    if(usuario.getRol() != null) {
                        usuarioDTO.setRolId(usuario.getRol().getIdRol());
                    }
                    return usuarioDTO;
                })
                .collect(Collectors.toList());
    }

    // --- Guardar Nuevo Usuario ---
    public UsuarioDTO guardar(UsuarioDTO usuarioDTO) {
        ModelMapper modelMapper = new ModelMapper();

        // Puedes añadir una verificación si el username ya existe aquí
        if (usuarioRepository.findByUsername(usuarioDTO.getUsername()).isPresent()) {
            throw new IllegalArgumentException("El nombre de usuario '" + usuarioDTO.getUsername() + "' ya existe.");
        }

        Usuario nuevoUsuario = modelMapper.map(usuarioDTO, Usuario.class);

        // **ENCRIPTAR CONTRASEÑA al guardar**
        if (usuarioDTO.getPassword() == null || usuarioDTO.getPassword().isEmpty()) {
            throw new IllegalArgumentException("La contraseña no puede ser nula o vacía al registrar un usuario.");
        }
        nuevoUsuario.setPassword(passwordEncoder.encode(usuarioDTO.getPassword()));

        // Buscar y asignar el rol
        Rol rol = rolService.buscarRolPorId(usuarioDTO.getRolId());
        if (rol == null) { // rolService.buscarRolPorId debería lanzar una excepción si no encuentra el rol
            throw new RecursoNoEncontradoException("Rol no encontrado con ID: " + usuarioDTO.getRolId());
        }
        nuevoUsuario.setRol(rol);
        nuevoUsuario.setEstado(true); // Establecer estado a true por defecto al guardar

        Usuario savedUsuario = usuarioRepository.save(nuevoUsuario);

        UsuarioDTO dto = modelMapper.map(savedUsuario, UsuarioDTO.class);
        dto.setPassword(null); // ¡No devolver la contraseña!
        if(savedUsuario.getRol() != null) {
            dto.setRolId(savedUsuario.getRol().getIdRol());
        }
        return dto;
    }

    // --- Modificar Usuario Existente ---
    public UsuarioDTO modificar (Long id, UsuarioDTO usuarioDTO){
        ModelMapper modelMapper = new ModelMapper();

        // 1. Buscar el usuario existente por ID
        Usuario existingUsuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con ID: " + id));

        // 2. Actualizar campos: nombre, apellido, email, username, estado
        existingUsuario.setNombre(usuarioDTO.getNombre());
        existingUsuario.setApellido(usuarioDTO.getApellido());
        existingUsuario.setEmail(usuarioDTO.getEmail());

        // Cuidado al permitir cambios de username, podría tener implicaciones
        // Si el username ha cambiado, verifica que el nuevo username no exista ya
        if (!existingUsuario.getUsername().equals(usuarioDTO.getUsername())) {
            if (usuarioRepository.findByUsername(usuarioDTO.getUsername()).isPresent()) {
                throw new IllegalArgumentException("El nombre de usuario '" + usuarioDTO.getUsername() + "' ya está en uso.");
            }
            existingUsuario.setUsername(usuarioDTO.getUsername());
        }

        existingUsuario.setEstado(usuarioDTO.isEstado());

        // **ENCRIPTAR CONTRASEÑA al modificar (solo si se proporciona)**
        if (usuarioDTO.getPassword() != null && !usuarioDTO.getPassword().isEmpty()) {
            // Solo actualiza la contraseña si se envía una nueva y no está vacía
            existingUsuario.setPassword(passwordEncoder.encode(usuarioDTO.getPassword()));
        }
        // Si usuarioDTO.getPassword() es null o vacío, la contraseña existente no se modifica.

        // 3. Actualizar el rol (si se proporciona un nuevo rolId y es diferente al actual)
        if (usuarioDTO.getRolId() != null) {
            if (existingUsuario.getRol() == null || !usuarioDTO.getRolId().equals(existingUsuario.getRol().getIdRol())) {
                Rol nuevoRol = rolService.buscarRolPorId(usuarioDTO.getRolId());
                if (nuevoRol == null) {
                    throw new RecursoNoEncontradoException("Rol no encontrado con ID: " + usuarioDTO.getRolId());
                }
                existingUsuario.setRol(nuevoRol);
            }
        } else if (existingUsuario.getRol() != null) {
            // Esto maneja el caso si el DTO de actualización envía rolId NULO, pero el usuario ya tiene uno.
            // Generalmente, un usuario siempre debe tener un rol.
            throw new IllegalArgumentException("El rol del usuario no puede ser nulo.");
        }


        // 4. Guardar el usuario actualizado
        Usuario actualize = usuarioRepository.save(existingUsuario);

        // 5. Mapear a DTO para la respuesta y limpiar la contraseña
        UsuarioDTO dto = modelMapper.map(actualize, UsuarioDTO.class);
        dto.setPassword(null); // ¡No devolver la contraseña!
        if(actualize.getRol() != null) {
            dto.setRolId(actualize.getRol().getIdRol());
        }
        return dto;
    }

    // --- Eliminar Lógico de Usuario ---
    public UsuarioDTO eliminar (Long id){
        ModelMapper modelMapper = new ModelMapper();
        Usuario entidad = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con ID: " + id));

        entidad.setEstado(false); // Cambiar estado a false para eliminación lógica
        entidad = usuarioRepository.save(entidad);

        UsuarioDTO dto = modelMapper.map(entidad, UsuarioDTO.class);
        dto.setPassword(null); // ¡No devolver la contraseña!
        if(entidad.getRol() != null) {
            dto.setRolId(entidad.getRol().getIdRol());
        }
        return dto;
    }
    // buscar por ID
    public UsuarioDTO buscarPorId(Long id) {
        ModelMapper modelMapper = new ModelMapper();
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con ID: " + id));
        UsuarioDTO dto = modelMapper.map(usuario, UsuarioDTO.class);
        dto.setPassword(null); // ¡No devolver la contraseña!
        if(usuario.getRol() != null) {
            dto.setRolId(usuario.getRol().getIdRol());
        }
        return dto;
    }
}
