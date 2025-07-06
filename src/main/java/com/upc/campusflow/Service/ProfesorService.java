package com.upc.campusflow.Service;

import com.upc.campusflow.DTO.ProfesorDTO;
import com.upc.campusflow.DTO.RegisterProfesorRequest; // Importar el nuevo DTO
import com.upc.campusflow.Exception.RecursoNoEncontradoException;
import com.upc.campusflow.Model.Carrera;
import com.upc.campusflow.Model.Profesor;
import com.upc.campusflow.Model.Rol; // Necesario para asignar el rol
import com.upc.campusflow.Model.Usuario;
import com.upc.campusflow.Repository.CarreraRepository;
import com.upc.campusflow.Repository.ProfesorRepository;
import com.upc.campusflow.Repository.RolRepository; // Necesario para buscar el rol
import com.upc.campusflow.Repository.UsuarioRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.security.crypto.password.PasswordEncoder; // Para encriptar contraseñas
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProfesorService {
    final ProfesorRepository profesorRepository;
    final UsuarioRepository usuarioRepository;
    final CarreraRepository carreraRepository;
    final RolRepository rolRepository; // Inyectar RolRepository
    final PasswordEncoder passwordEncoder; // Inyectar PasswordEncoder

    public ProfesorService(ProfesorRepository profesorRepository,
                           UsuarioRepository usuarioRepository,
                           CarreraRepository carreraRepository,
                           RolRepository rolRepository, // Añadir al constructor
                           PasswordEncoder passwordEncoder) { // Añadir al constructor
        this.profesorRepository = profesorRepository;
        this.usuarioRepository = usuarioRepository;
        this.carreraRepository = carreraRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Método auxiliar para configurar ModelMapper para mapear Profesor a ProfesorDTO
    private ModelMapper getProfesorToDTOMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        modelMapper.createTypeMap(Profesor.class, ProfesorDTO.class)
                .addMappings(mapper -> {
                    // Mapear idUsuario de Usuario a Usuario en DTO
                    mapper.map(src -> src.getUsuario().getIdUsuario(), ProfesorDTO::setUsuario);
                    // Mapear idCarrera de Carrera a idCarrera en DTO
                    mapper.map(src -> src.getCarrera().getIdCarrera(), ProfesorDTO::setIdCarrera);
                });
        return modelMapper;
    }

    //Listar
    public List<ProfesorDTO> listar(){
        // Obtener todos los profesores activos
        List<Profesor> profesores = profesorRepository.findAll().stream()
                .filter(Profesor::isEstado)
                .collect(Collectors.toList());

        List<ProfesorDTO> profesorDTOS = new ArrayList<>();
        ModelMapper modelMapper = getProfesorToDTOMapper();

        for(Profesor profesor : profesores){
            profesorDTOS.add(modelMapper.map(profesor, ProfesorDTO.class));
        }
        return profesorDTOS;
    }

    //Guardar
    public ProfesorDTO guardar(ProfesorDTO profesorDTO){
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        modelMapper.createTypeMap(ProfesorDTO.class, Profesor.class)
                .addMappings(mapper -> {
                    mapper.skip(Profesor::setUsuario);
                    mapper.skip(Profesor::setCarrera);
                });

        Profesor profesor = modelMapper.map(profesorDTO, Profesor.class);

        // Buscar y asignar Usuario
        if (profesorDTO.getUsuario() != null) {
            Usuario usuario = usuarioRepository.findById(profesorDTO.getUsuario())
                    .orElseThrow(() -> new RecursoNoEncontradoException(
                            "No se encontró el Usuario con ID: " + profesorDTO.getUsuario() + ". No se puede registrar el profesor."
                    ));
            profesor.setUsuario(usuario);
        } else {
            throw new IllegalArgumentException("El ID de usuario no puede ser nulo al registrar un profesor.");
        }

        // Buscar y asignar Carrera
        if (profesorDTO.getIdCarrera() != null) {
            Carrera carrera = carreraRepository.findById(profesorDTO.getIdCarrera())
                    .orElseThrow(() -> new RecursoNoEncontradoException(
                            "No se encontró la Carrera con ID: " + profesorDTO.getIdCarrera() + ". No se puede registrar el profesor."
                    ));
            profesor.setCarrera(carrera);
        } else {
            throw new IllegalArgumentException("El ID de carrera no puede ser nulo al registrar un profesor.");
        }

        if (profesor.getUsuario() == null) {
            throw new RuntimeException("Error interno: El objeto Usuario no pudo ser asignado al Profesor antes de guardar.");
        }
        if (profesor.getCarrera() == null) {
            throw new RuntimeException("Error interno: El objeto Carrera no pudo ser asignado al Profesor antes de guardar.");
        }

        profesor = profesorRepository.save(profesor);

        ModelMapper responseMapper = getProfesorToDTOMapper();
        return responseMapper.map(profesor, ProfesorDTO.class);
    }

    //Modificar
    public ProfesorDTO modificar (Long id, ProfesorDTO profesorDTO){
        Profesor existingProfesor = profesorRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Profesor no encontrado con ID: " + id));

        if (profesorDTO.getUsuario() == null) {
            throw new IllegalArgumentException("El ID de usuario es requerido para modificar un profesor.");
        }
        if (profesorDTO.getIdCarrera() == null) {
            throw new IllegalArgumentException("El ID de carrera es requerido para modificar un profesor.");
        }

        Usuario usuario = usuarioRepository.findById(profesorDTO.getUsuario())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Usuario con ID " + profesorDTO.getUsuario() + " no encontrado."
                ));

        Carrera carrera = carreraRepository.findById(profesorDTO.getIdCarrera())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Carrera con ID " + profesorDTO.getIdCarrera() + " no encontrada."
                ));

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        modelMapper.createTypeMap(ProfesorDTO.class, Profesor.class)
                .addMappings(mapper -> {
                    mapper.skip(Profesor::setIdProfesor);
                    mapper.skip(Profesor::setUsuario);
                    mapper.skip(Profesor::setCarrera);
                });

        modelMapper.map(profesorDTO, existingProfesor);

        existingProfesor.setUsuario(usuario);
        existingProfesor.setCarrera(carrera);

        existingProfesor.setEstado(profesorDTO.isEstado());

        Profesor updatedProfesor = profesorRepository.save(existingProfesor);

        ModelMapper responseMapper = getProfesorToDTOMapper();
        ProfesorDTO dto = responseMapper.map(updatedProfesor, ProfesorDTO.class);

        return dto;
    }

    //Eliminar
    public ProfesorDTO eliminar (Long id){
        ModelMapper modelMapper = getProfesorToDTOMapper();
        Profesor entidad = profesorRepository.findById(id).orElseThrow(() -> new RecursoNoEncontradoException("Profesor no encontrado con ID: " + id));
        entidad.setEstado(false);
        entidad =  profesorRepository.save(entidad);
        ProfesorDTO dto =  modelMapper.map(entidad, ProfesorDTO.class);
        return dto;
    }

    // --- Nuevo método para registrar Profesor y Usuario ---
    public ProfesorDTO registrarProfesor(RegisterProfesorRequest request) {
        Usuario usuarioGuardado = null; // Para intentar borrarlo si falla el profesor
        try {
            // 1. Validar si el username o email ya existen
            if (usuarioRepository.findByUsername(request.getUsername()).isPresent()) {
                throw new IllegalArgumentException("El nombre de usuario '" + request.getUsername() + "' ya está en uso.");
            }
            if (usuarioRepository.findByEmail(request.getEmail()).isPresent()) {
                throw new IllegalArgumentException("El email '" + request.getEmail() + "' ya está registrado.");
            }

            // 2. Crear y guardar Usuario
            Usuario nuevoUsuario = new Usuario();
            nuevoUsuario.setNombre(request.getNombre());
            nuevoUsuario.setApellido(request.getApellido());
            nuevoUsuario.setEmail(request.getEmail());
            nuevoUsuario.setUsername(request.getUsername());
            nuevoUsuario.setPassword(passwordEncoder.encode(request.getPassword())); // Encriptar la contraseña

            // Asignar rol por defecto (ej. PROFESOR)
            // Asegúrate de que este rol exista en tu base de datos (con nombre "PROFESOR")
            Rol rolProfesor = rolRepository.findFirstByNombre("PROFESOR") // Usamos "PROFESOR" sin "ROLE_"
                    .orElseThrow(() -> new RecursoNoEncontradoException("Rol 'PROFESOR' no encontrado. Asegúrate de que exista en la base de datos."));
            nuevoUsuario.setRol(rolProfesor);
            nuevoUsuario.setEstado(true); // Activar usuario por defecto

            usuarioGuardado = usuarioRepository.save(nuevoUsuario);

            // 3. Buscar la Carrera (si aplica para el profesor)
            Carrera carrera = null;
            if (request.getIdCarrera() != null) {
                carrera = carreraRepository.findById(request.getIdCarrera())
                        .orElseThrow(() -> new RecursoNoEncontradoException("Carrera no encontrada con ID: " + request.getIdCarrera()));
            }


            // 4. Crear y guardar Profesor, vinculando al nuevo Usuario y Carrera
            Profesor nuevoProfesor = new Profesor();
            nuevoProfesor.setEspecialidad(request.getEspecialidad());
            nuevoProfesor.setNumColegiatura(request.getNumColegiatura());
            nuevoProfesor.setUsuario(usuarioGuardado); // Vincular el usuario recién creado
            nuevoProfesor.setCarrera(carrera); // Asignar la carrera (puede ser null si no es obligatoria para el profesor)
            nuevoProfesor.setEstado(true); // Activar profesor por defecto

            // Importante: Establecer la relación inversa en el Usuario para la consistencia bidireccional
            // Asumiendo que tu entidad Usuario tiene un campo 'profesor' con @OneToOne
            usuarioGuardado.setProfesor(nuevoProfesor);
            // No es necesario guardar usuarioGuardado de nuevo aquí si cascadeType.ALL está en Usuario.profesor
            // y Profesor es el lado propietario. Pero si tienes problemas, podrías necesitarlo.
            // usuarioRepository.save(usuarioGuardado); // Descomentar si el cascade no es suficiente

            Profesor profesorGuardado = profesorRepository.save(nuevoProfesor);

            // 5. Convertir a DTO y retornar
            ModelMapper responseMapper = getProfesorToDTOMapper();
            return responseMapper.map(profesorGuardado, ProfesorDTO.class);

        } catch (Exception e) {
            // Imprime el stack trace completo para ver la causa raíz
            e.printStackTrace();

            // Si algo falla, intenta eliminar el usuario si ya fue guardado
            if (usuarioGuardado != null && usuarioGuardado.getIdUsuario() != null) {
                try {
                    usuarioRepository.delete(usuarioGuardado);
                    System.err.println("Usuario " + usuarioGuardado.getUsername() + " eliminado debido a un error en el registro del profesor.");
                } catch (Exception deleteException) {
                    System.err.println("Error al intentar eliminar el usuario " + usuarioGuardado.getUsername() + " después de un fallo en el registro del profesor: " + deleteException.getMessage());
                }
            }
            // Re-lanzar la excepción original
            throw e;
        }
    }
}
