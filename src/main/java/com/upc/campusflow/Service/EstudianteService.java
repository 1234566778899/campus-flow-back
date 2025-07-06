package com.upc.campusflow.Service;

import com.upc.campusflow.DTO.EstudianteDTO;
import com.upc.campusflow.DTO.RegisterEstudianteRequest;
import com.upc.campusflow.Exception.RecursoNoEncontradoException;
import com.upc.campusflow.Model.Carrera;
import com.upc.campusflow.Model.Estudiante;
import com.upc.campusflow.Model.Rol;
import com.upc.campusflow.Model.Usuario;
import com.upc.campusflow.Repository.CarreraRepository;
import com.upc.campusflow.Repository.EstudianteRepository;
import com.upc.campusflow.Repository.RolRepository;
import com.upc.campusflow.Repository.UsuarioRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class EstudianteService {

    private final EstudianteRepository estudianteRepository;
    private final CarreraRepository carreraRepository;
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    public EstudianteService(EstudianteRepository estudianteRepository,
                             CarreraRepository carreraRepository,
                             UsuarioRepository usuarioRepository,
                             RolRepository rolRepository,
                             PasswordEncoder passwordEncoder) {
        this.estudianteRepository = estudianteRepository;
        this.carreraRepository = carreraRepository;
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public EstudianteDTO guardar(EstudianteDTO estudianteDTO){
        ModelMapper modelMapper = new ModelMapper();
        Estudiante estudiante = modelMapper.map(estudianteDTO, Estudiante.class);

        // Handle Carrera association
        if (estudianteDTO.getIdCarrera() != null) {
            carreraRepository.findById(estudianteDTO.getIdCarrera())
                    .ifPresent(estudiante::setCarrera);
        } else {
            throw new RecursoNoEncontradoException("El ID de carrera no puede ser nulo al registrar un estudiante.");
        }

        // Handle Usuario association
        if (estudianteDTO.getIdUsuario() != null) {
            usuarioRepository.findById(estudianteDTO.getIdUsuario())
                    .ifPresent(estudiante::setUsuarios);
        } else {
            throw new RecursoNoEncontradoException("El ID de usuario no puede ser nulo al registrar un estudiante.");
        }

        if (estudiante.getUsuarios() == null) {
            throw new RecursoNoEncontradoException("No se encontró el Usuario con ID: " + estudianteDTO.getIdUsuario() + ". No se puede registrar el estudiante.");
        }

        // Save Estudiante.
        estudiante = estudianteRepository.save(estudiante);

        // Key change here for response mapping without PropertyMap!
        EstudianteDTO responseDto = modelMapper.map(estudiante, EstudianteDTO.class);

        // Manual assignment of idUsuario after base mapping
        if (estudiante.getUsuarios() != null) {
            responseDto.setIdUsuario(estudiante.getUsuarios().getIdUsuario());
        }

        return responseDto;
    }

    public EstudianteDTO buscarPorId(Long id) {
        Estudiante estudiante = estudianteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Estudiante no encontrado con ID: " + id));
        ModelMapper modelMapper = new ModelMapper();
        // Ensure the DTO also gets the associated IDs
        EstudianteDTO dto = modelMapper.map(estudiante, EstudianteDTO.class);
        if (estudiante.getCarrera() != null) {
            dto.setIdCarrera(estudiante.getCarrera().getIdCarrera());
        }
        if (estudiante.getUsuarios() != null) {
            dto.setIdUsuario(estudiante.getUsuarios().getIdUsuario());
        }
        return dto;
    }

    // List Students
    public List<EstudianteDTO> listar() {
        ModelMapper modelMapper = new ModelMapper();
        List<Estudiante> estudiantes = estudianteRepository.findAll();
        return estudiantes.stream()
                .map(estudiante -> {
                    EstudianteDTO estudianteDTO = modelMapper.map(estudiante, EstudianteDTO.class);
                    // Populate relationship IDs in the DTO for listing
                    if (estudiante.getCarrera() != null) {
                        estudianteDTO.setIdCarrera(estudiante.getCarrera().getIdCarrera());
                    }
                    if (estudiante.getUsuarios() != null) {
                        estudianteDTO.setIdUsuario(estudiante.getUsuarios().getIdUsuario());
                    }
                    return estudianteDTO;
                })
                .collect(Collectors.toList());
    }

    // Modify Student (ADJUSTED)
    public EstudianteDTO modificar(Long id, EstudianteDTO estudianteDTO) {
        ModelMapper modelMapper = new ModelMapper();

        // 1. Find the existing student
        Estudiante existente = estudianteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Estudiante no encontrado con ID: " + id));

        // 2. Update basic fields, including status
        existente.setCiclo(estudianteDTO.getCiclo());
        existente.setEstado(estudianteDTO.isEstado());

        // 3. Update Carrera (if a new Carrera ID is provided)
        if (estudianteDTO.getIdCarrera() != null) {
            if (existente.getCarrera() == null || !existente.getCarrera().getIdCarrera().equals(estudianteDTO.getIdCarrera())) {
                Carrera nuevaCarrera = carreraRepository.findById(estudianteDTO.getIdCarrera())
                        .orElseThrow(() -> new RecursoNoEncontradoException("Carrera no encontrada con ID: " + estudianteDTO.getIdCarrera()));
                existente.setCarrera(nuevaCarrera);
            }
        } else {
            throw new IllegalArgumentException("El ID de carrera no puede ser nulo en la actualización.");
        }

        // 4. Update Usuario (if a new Usuario ID is provided)
        if (estudianteDTO.getIdUsuario() != null) {
            if (existente.getUsuarios() == null || !existente.getUsuarios().getIdUsuario().equals(estudianteDTO.getIdUsuario())) {
                Usuario nuevoUsuario = usuarioRepository.findById(estudianteDTO.getIdUsuario())
                        .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con ID: " + estudianteDTO.getIdUsuario()));
                existente.setUsuarios(nuevoUsuario);
            }
        } else {
            throw new IllegalArgumentException("El ID de usuario no puede ser nulo en la actualización.");
        }

        // 5. Save changes
        Estudiante actualizado = estudianteRepository.save(existente);

        // 6. Map to DTO for the response and populate relationship IDs
        EstudianteDTO responseDto = modelMapper.map(actualizado, EstudianteDTO.class);
        responseDto.setIdCarrera(actualizado.getCarrera().getIdCarrera());
        responseDto.setIdUsuario(actualizado.getUsuarios().getIdUsuario());
        return responseDto;
    }

    // Logical delete
    public EstudianteDTO eliminar(Long id) {
        ModelMapper modelMapper = new ModelMapper();
        Estudiante entidad = estudianteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Estudiante no encontrado con ID: " + id));
        entidad.setEstado(false);
        entidad = estudianteRepository.save(entidad);
        return modelMapper.map(entidad, EstudianteDTO.class);
    }


    public List<EstudianteDTO> obtenerEstudiantesConNotaBaja() {
        List<Estudiante> estudiantes = estudianteRepository.findEstudiantesConNotaMenorA11();
        ModelMapper modelMapper = new ModelMapper();
        List<EstudianteDTO> dtoList = new ArrayList<>();
        for (Estudiante e : estudiantes) {
            dtoList.add(modelMapper.map(e, EstudianteDTO.class));
        }
        return dtoList;
    }


    /**
     * Returns the top 10% of students based on average grades.
     */
    private final ModelMapper mapper = new ModelMapper();

    public List<EstudianteDTO> topDecilePorNota() {
        long total = estudianteRepository.count();
        int decileCount = (int) Math.ceil(total * 0.1);
        if (decileCount < 1) decileCount = 1;

        List<Estudiante> top = estudianteRepository
                .findAllOrderByAverageNotaDesc(PageRequest.of(0, decileCount));

        return top.stream()
                .map(e -> mapper.map(e, EstudianteDTO.class))
                .toList();
    }

    public List<EstudianteDTO> obtenerEstudiantesPorRangoDeFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        List<Estudiante> estudiantes = estudianteRepository.obtenerEstudiantesPorRangoDeFechas(fechaInicio, fechaFin);
        ModelMapper modelMapper = new ModelMapper();
        return estudiantes.stream()
                .map(e -> modelMapper.map(e, EstudianteDTO.class))
                .collect(Collectors.toList());
    }


    // --- New method to register Student and User (without @Transactional) ---
    public EstudianteDTO registrarEstudiante(RegisterEstudianteRequest request) {
        Usuario usuarioGuardado = null; // Para intentar borrarlo si falla el estudiante
        try {
            // 1. Validate if username or email already exist
            if (usuarioRepository.findByUsername(request.getUsername()).isPresent()) {
                throw new IllegalArgumentException("El nombre de usuario '" + request.getUsername() + "' ya está en uso.");
            }
            if (usuarioRepository.findByEmail(request.getEmail()).isPresent()) {
                throw new IllegalArgumentException("El email '" + request.getEmail() + "' ya está registrado.");
            }

            // 2. Create and save User
            Usuario nuevoUsuario = new Usuario();
            nuevoUsuario.setNombre(request.getNombre());
            nuevoUsuario.setApellido(request.getApellido());
            nuevoUsuario.setEmail(request.getEmail());
            nuevoUsuario.setUsername(request.getUsername());
            nuevoUsuario.setPassword(passwordEncoder.encode(request.getPassword()));

            System.out.println("Intentando buscar rol 'ESTUDIANTE'..."); // DEBUG
            // *** CAMBIO CRÍTICO AQUÍ: Cambiado de "ROLE_ESTUDIANTE" a "ESTUDIANTE" ***
            Rol rolEstudiante = rolRepository.findFirstByNombre("ESTUDIANTE")
                    .orElseThrow(() -> new RecursoNoEncontradoException("Rol 'ESTUDIANTE' no encontrado. Asegúrate de que exista en la base de datos."));
            System.out.println("Rol 'ESTUDIANTE' encontrado: " + rolEstudiante.getNombre()); // DEBUG
            nuevoUsuario.setRol(rolEstudiante);
            nuevoUsuario.setEstado(true);

            usuarioGuardado = usuarioRepository.save(nuevoUsuario);
            System.out.println("Usuario guardado con ID: " + usuarioGuardado.getIdUsuario()); // DEBUG

            System.out.println("Intentando buscar Carrera con ID: " + request.getIdCarrera()); // DEBUG
            Carrera carrera = carreraRepository.findById(request.getIdCarrera())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Carrera no encontrada con ID: " + request.getIdCarrera()));
            System.out.println("Carrera encontrada: " + carrera.getNombre()); // DEBUG

            // 4. Create and save Estudiante, linking to the new User and Carrera
            Estudiante nuevoEstudiante = new Estudiante();
            nuevoEstudiante.setCiclo(request.getCiclo());
            nuevoEstudiante.setCarrera(carrera);
            nuevoEstudiante.setUsuarios(usuarioGuardado);
            nuevoEstudiante.setEstado(true);

            usuarioGuardado.setEstudiante(nuevoEstudiante); // Establecer la relación inversa

            Estudiante estudianteGuardado = estudianteRepository.save(nuevoEstudiante);
            System.out.println("Estudiante guardado con ID: " + estudianteGuardado.getIdEstudiante()); // DEBUG

            // 5. Convert to DTO and return
            return convertToEstudianteDTO(estudianteGuardado);

        } catch (Exception e) {
            // Imprime el stack trace completo para ver la causa raíz
            e.printStackTrace(); // DEBUG: Esto imprimirá la traza completa en la consola del backend.

            // Si algo falla, intenta eliminar el usuario si ya fue guardado
            if (usuarioGuardado != null && usuarioGuardado.getIdUsuario() != null) {
                try {
                    usuarioRepository.delete(usuarioGuardado);
                    System.out.println("Usuario " + usuarioGuardado.getUsername() + " eliminado debido a un error en el registro del estudiante.");
                } catch (Exception deleteException) {
                    System.err.println("Error al intentar eliminar el usuario " + usuarioGuardado.getUsername() + " después de un fallo en el registro del estudiante: " + deleteException.getMessage());
                }
            }
            // Re-lanzar la excepción original
            throw e;
        }
    }

    // Entity to DTO conversion method
    private EstudianteDTO convertToEstudianteDTO(Estudiante estudiante) {
        EstudianteDTO dto = new EstudianteDTO();
        dto.setIdEstudiante(estudiante.getIdEstudiante());
        dto.setCiclo(estudiante.getCiclo());
        dto.setEstado(estudiante.isEstado());
        if (estudiante.getCarrera() != null) {
            dto.setIdCarrera(estudiante.getCarrera().getIdCarrera());
        }
        if (estudiante.getUsuarios() != null) {
            dto.setIdUsuario(estudiante.getUsuarios().getIdUsuario());
        }
        return dto;
    }


    // Nuevo: Método para buscar estudiante por ID de usuario, manejando la List
    public EstudianteDTO buscarEstudiantePorIdUsuario(Long idUsuario) {
        List<Estudiante> estudiantes = estudianteRepository.findByUsuariosIdUsuario(idUsuario);
        if (estudiantes.isEmpty()) {
            throw new RecursoNoEncontradoException("Estudiante no encontrado para el ID de usuario: " + idUsuario);
        }
        // Asumimos que, debido a la relación OneToOne, solo habrá un estudiante (o ninguno)
        return convertToEstudianteDTO(estudiantes.get(0));
    }
}
