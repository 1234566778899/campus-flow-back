package com.upc.campusflow.Controller;

import com.upc.campusflow.Model.Rol;
import com.upc.campusflow.Model.Usuario;
import com.upc.campusflow.Repository.RolRepository;
import com.upc.campusflow.Repository.UsuarioRepository;
import com.upc.campusflow.Security.CustomUserDetailsService;
import com.upc.campusflow.Security.JwtTokenUtil;
import com.upc.campusflow.DTO.AuthRequest;
import com.upc.campusflow.DTO.AuthResponse;
import com.upc.campusflow.DTO.RegisterRequest;
import com.upc.campusflow.Service.RolService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;


import java.util.Collections;
import java.util.Map;
@CrossOrigin

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtTokenUtil jwtTokenUtil;
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;
    private final RolService rolService;



    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authRequest.getUsername(),
                            authRequest.getPassword()
                    )
            );

            UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUsername());
            String token = jwtTokenUtil.generateToken(userDetails);

            // Obtener el rol del usuario (asumiendo un solo rol principal)
            String userRole = userDetails.getAuthorities().stream()
                    .findFirst() // Obtiene la primera autoridad
                    .map(GrantedAuthority::getAuthority) // Mapea a su nombre de autoridad
                    .orElse("UNKNOWN_ROLE"); // Rol por defecto si no se encuentra

            // Obtener el ID del usuario
            // Necesitamos buscar el objeto Usuario para obtener su ID
            Usuario usuario = usuarioRepository.findByUsername(authRequest.getUsername())
                    .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado después de autenticación exitosa."));

            Long userId = usuario.getIdUsuario();


            // Devolver el token, el rol y el ID del usuario en la respuesta
            return ResponseEntity.ok(new AuthResponse(token, userRole, userId));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).body("Credenciales inválidas");
        } catch (UsernameNotFoundException e) {
            // Esto podría ocurrir si el CustomUserDetailsService no encuentra el usuario
            // o si el usuario se elimina justo después de la autenticación (caso raro)
            return ResponseEntity.status(404).body("Usuario no encontrado");
        } catch (Exception e) {
            // Captura cualquier otra excepción inesperada
            return ResponseEntity.status(500).body("Error interno del servidor: " + e.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        // Verificar si el nombre de usuario ya existe
        if (usuarioRepository.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("El usuario ya existe");
        }

        // Buscar el rol por ID
        Rol rolUsuario = rolRepository.findById(request.getRolId()).orElse(null);

        // Si el rol no existe, devolver error
        if (rolUsuario == null) {
            return ResponseEntity.badRequest().body("Rol no encontrado");
        }

        // Crear un nuevo usuario
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setUsername(request.getUsername());
        nuevoUsuario.setPassword(passwordEncoder.encode(request.getPassword()));
        nuevoUsuario.setEmail(request.getEmail());
        nuevoUsuario.setNombre(request.getNombre());
        nuevoUsuario.setApellido(request.getApellido());
        nuevoUsuario.setEstado(true);

        // Asignar el rol al nuevo usuario
        nuevoUsuario.setRol(rolUsuario);  // Asignamos el rol basado en su ID

        // Guardar el nuevo usuario
        usuarioRepository.save(nuevoUsuario);

        // Devolver la respuesta
        return ResponseEntity.ok("Usuario registrado correctamente");
    }
}
