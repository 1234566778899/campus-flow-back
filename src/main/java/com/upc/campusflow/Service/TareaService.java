package com.upc.campusflow.Service;

import com.upc.campusflow.DTO.TareaDTO;
import com.upc.campusflow.Exception.RecursoNoEncontradoException; // Assuming you have this exception
import com.upc.campusflow.Model.Tarea;
import com.upc.campusflow.Model.Estudiante;
import com.upc.campusflow.Model.Horario;
import com.upc.campusflow.Repository.AsignaturaRepository; // You have this, but not used in this service for Tarea directly
import com.upc.campusflow.Repository.TareaRepository;
import com.upc.campusflow.Repository.EstudianteRepository; // <--- INYECTA ESTE
import com.upc.campusflow.Repository.HorarioRepository;   // <--- INYECTA ESTE
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors; // Added for .collect(Collectors.toList())

@Service
public class TareaService {
    final TareaRepository tareaRepository;
    final EstudianteRepository estudianteRepository; // <--- INYECTADO
    final HorarioRepository horarioRepository;     // <--- INYECTADO

    // Constructor actualizado para incluir los nuevos repositorios
    public TareaService(TareaRepository tareaRepository,
                        AsignaturaRepository asignaturaRepository, // Keep this if used elsewhere in class
                        EstudianteRepository estudianteRepository, // <--- Añade esto
                        HorarioRepository horarioRepository) {     // <--- Añade esto
        this.tareaRepository = tareaRepository;
        this.estudianteRepository = estudianteRepository; // <--- Inicializa
        this.horarioRepository = horarioRepository;       // <--- Inicializa
    }

    // RELACION CON ESTUDIANTE Y HORARIO
    // listar
    public List<TareaDTO> listar() {
        List<Tarea> tareas = tareaRepository.findAll();

        List<TareaDTO> tareaDTOS = new ArrayList<>();
        ModelMapper modelMapper = new ModelMapper();
        for (Tarea tarea : tareas) {
            TareaDTO tareaDTO = modelMapper.map(tarea, TareaDTO.class);
            // Asegúrate de que los IDs se extraigan de las entidades relacionadas
            if (tarea.getEstudiante() != null) {
                tareaDTO.setId_estudiante(tarea.getEstudiante().getIdEstudiante());
            }
            if (tarea.getHorario() != null) {
                tareaDTO.setId_horario(tarea.getHorario().getIdHorario());
            }
            tareaDTOS.add(tareaDTO);
        }
        return tareaDTOS;
    }

    // guardar
    public TareaDTO guardar(TareaDTO tareaDTO) {
        ModelMapper modelMapper = new ModelMapper();
        Tarea tarea = modelMapper.map(tareaDTO, Tarea.class);

        // --- MODIFICACIONES CLAVE AQUÍ ---
        // Validar que al menos uno de los IDs (o ambos si es mandatorio) estén presentes en el DTO
        if (tareaDTO.getId_estudiante() == null && tareaDTO.getId_horario() == null) {
            throw new IllegalArgumentException("Se debe proporcionar al menos un ID de Estudiante o Horario para guardar una tarea.");
        }

        // Buscar y establecer el Estudiante
        if (tareaDTO.getId_estudiante() != null) {
            Estudiante estudiante = estudianteRepository.findById(tareaDTO.getId_estudiante())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Estudiante no encontrado con ID: " + tareaDTO.getId_estudiante()));
            tarea.setEstudiante(estudiante);
        } else {
            tarea.setEstudiante(null); // Si el ID es nulo en el DTO, desvincular
        }

        // Buscar y establecer el Horario
        if (tareaDTO.getId_horario() != null) {
            Horario horario = horarioRepository.findById(tareaDTO.getId_horario())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Horario no encontrado con ID: " + tareaDTO.getId_horario()));
            tarea.setHorario(horario);
        } else {
            tarea.setHorario(null); // Si el ID es nulo en el DTO, desvincular
        }
        // ---------------------------------

        tarea = tareaRepository.save(tarea);

        // Mapear de vuelta al DTO y asegurar que los IDs de las relaciones se establezcan
        TareaDTO responseDTO = modelMapper.map(tarea, TareaDTO.class);
        if (tarea.getEstudiante() != null) {
            responseDTO.setId_estudiante(tarea.getEstudiante().getIdEstudiante());
        }
        if (tarea.getHorario() != null) {
            responseDTO.setId_horario(tarea.getHorario().getIdHorario());
        }
        return responseDTO;
    }

    // Modificar
    public TareaDTO modificar(Long id, TareaDTO tareaDTO) {
        ModelMapper modelMapper = new ModelMapper();
        // Verificar si la tarea existe
        Tarea existente = tareaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Tarea no encontrada con ID: " + id)); // Usar RecursoNoEncontradoException

        // Guardar el ID original para evitar el error "identifier of an instance was altered"
        Long originalId = existente.getIdTarea(); // Asegúrate de que este es el getter correcto para el ID de Tarea
        modelMapper.map(tareaDTO, existente); // sobreescribir los datos
        existente.setIdTarea(originalId);     // Restaurar el ID original

        // --- MODIFICACIONES CLAVE AQUÍ ---
        // Validar que al menos uno de los IDs (o ambos si es mandatorio) estén presentes en el DTO
        if (tareaDTO.getId_estudiante() == null && tareaDTO.getId_horario() == null) {
            throw new IllegalArgumentException("Al modificar, se debe proporcionar al menos un ID de Estudiante o Horario para vincular la tarea.");
        }

        // Buscar y establecer el Estudiante
        if (tareaDTO.getId_estudiante() != null) {
            Estudiante estudiante = estudianteRepository.findById(tareaDTO.getId_estudiante())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Estudiante no encontrado con ID: " + tareaDTO.getId_estudiante()));
            existente.setEstudiante(estudiante);
        } else {
            existente.setEstudiante(null); // Si el ID es nulo en el DTO, desvincular
        }

        // Buscar y establecer el Horario
        if (tareaDTO.getId_horario() != null) {
            Horario horario = horarioRepository.findById(tareaDTO.getId_horario())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Horario no encontrado con ID: " + tareaDTO.getId_horario()));
            existente.setHorario(horario);
        } else {
            existente.setHorario(null); // Si el ID es nulo en el DTO, desvincular
        }
        // ---------------------------------

        Tarea actualizar = tareaRepository.save(existente);

        // Mapear de vuelta al DTO y asegurar que los IDs de las relaciones se establezcan
        TareaDTO dto = modelMapper.map(actualizar, TareaDTO.class);
        if (actualizar.getEstudiante() != null) {
            dto.setId_estudiante(actualizar.getEstudiante().getIdEstudiante());
        }
        if (actualizar.getHorario() != null) {
            dto.setId_horario(actualizar.getHorario().getIdHorario());
        }
        return dto;
    }

    // Eliminar lógico
    public TareaDTO eliminar(Long id) {
        ModelMapper modelMapper = new ModelMapper();
        Tarea entidad = tareaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Tarea no encontrada con ID: " + id)); // Usar RecursoNoEncontradoException
        entidad.setEstado(false);
        entidad = tareaRepository.save(entidad);
        TareaDTO dto = modelMapper.map(entidad, TareaDTO.class);
        // Asegurarse de mapear los IDs de relaciones al DTO de respuesta
        if (entidad.getEstudiante() != null) {
            dto.setId_estudiante(entidad.getEstudiante().getIdEstudiante());
        }
        if (entidad.getHorario() != null) {
            dto.setId_horario(entidad.getHorario().getIdHorario());
        }
        return dto;
    }

    // Obtener tareas activas por estudiante
    public List<TareaDTO> TareasActivasPorEstudiante(Long idEstudiante) {
        List<Tarea> tareas = tareaRepository.TareasActivasPorEstudiante(idEstudiante);

        List<TareaDTO> tareaDTOS = new ArrayList<>();
        ModelMapper modelMapper = new ModelMapper();
        for (Tarea tarea : tareas) {
            TareaDTO tareaDTO = modelMapper.map(tarea, TareaDTO.class);
            if (tarea.getEstudiante() != null) { // Solo si el estudiante existe
                tareaDTO.setId_estudiante(tarea.getEstudiante().getIdEstudiante());
            }
            if (tarea.getHorario() != null) { // Solo si el horario existe
                tareaDTO.setId_horario(tarea.getHorario().getIdHorario());
            }
            tareaDTOS.add(tareaDTO);
        }
        return tareaDTOS;
    }

    // Obtener tareas con una prioridad especifica y ordenarlas por fecha limite
    public List<TareaDTO> TareasPorPrioridad(String prioridad) {
        List<Tarea> tareas = tareaRepository.TareasPorPrioridad(prioridad);

        List<TareaDTO> tareaDTOS = new ArrayList<>();
        ModelMapper modelMapper = new ModelMapper();
        for (Tarea tarea : tareas) {
            TareaDTO tareaDTO = modelMapper.map(tarea, TareaDTO.class);
            if (tarea.getEstudiante() != null) { // Solo si el estudiante existe
                tareaDTO.setId_estudiante(tarea.getEstudiante().getIdEstudiante());
            }
            if (tarea.getHorario() != null) { // Solo si el horario existe
                tareaDTO.setId_horario(tarea.getHorario().getIdHorario());
            }
            tareaDTOS.add(tareaDTO);
        }
        return tareaDTOS;
    }
}