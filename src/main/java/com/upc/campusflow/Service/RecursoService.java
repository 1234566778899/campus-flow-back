package com.upc.campusflow.Service;

import com.upc.campusflow.DTO.RecursoDTO;
import com.upc.campusflow.Exception.RecursoNoEncontradoException; // Assuming you have this custom exception
import com.upc.campusflow.Model.Publicacion;
import com.upc.campusflow.Model.Recurso;
import com.upc.campusflow.Model.Tarea;
import com.upc.campusflow.Repository.PublicacionRepository; // Inject this
import com.upc.campusflow.Repository.RecursoRepository;
import com.upc.campusflow.Repository.TareaRepository;       // Inject this
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors; // Added for clarity with toList()

@Service
public class RecursoService {
    final RecursoRepository recursoRepository;
    final TareaRepository tareaRepository;           // <--- INYECTA ESTE
    final PublicacionRepository publicacionRepository; // <--- INYECTA ESTE

    public RecursoService(RecursoRepository recursoRepository,
                          TareaRepository tareaRepository,
                          PublicacionRepository publicacionRepository) {
        this.recursoRepository = recursoRepository;
        this.tareaRepository = tareaRepository;
        this.publicacionRepository = publicacionRepository;
    }

    // Listar todos los recursos
    public List<RecursoDTO> listar() {
        List<Recurso> recursos = recursoRepository.findAll()
                .stream()
                .filter(Recurso::isEstado)
                .collect(Collectors.toList()); // Use .collect(Collectors.toList()) for compatibility

        List<RecursoDTO> recursoDTOs = new ArrayList<>();
        ModelMapper modelMapper = new ModelMapper();
        for (Recurso recurso : recursos) {
            RecursoDTO recursoDTO = modelMapper.map(recurso, RecursoDTO.class);
            // Asegúrate de que los IDs se extraigan de las entidades relacionadas
            if (recurso.getTarea() != null) {
                recursoDTO.setId_tarea(recurso.getTarea().getIdTarea());
            }
            if (recurso.getPublicacion() != null) {
                recursoDTO.setId_publicacion(recurso.getPublicacion().getIdPublicacion());
            }
            recursoDTOs.add(recursoDTO);
        }
        return recursoDTOs;
    }

    // Guardar recurso
    public RecursoDTO guardar(RecursoDTO recursoDTO) {
        ModelMapper modelMapper = new ModelMapper();
        Recurso recurso = modelMapper.map(recursoDTO, Recurso.class);

        // --- MODIFICACIONES CLAVE AQUÍ ---
        // Asegurarse de que el DTO tenga al menos uno de los IDs, o ambos si es mandatorio
        if (recursoDTO.getId_tarea() == null && recursoDTO.getId_publicacion() == null) {
            throw new IllegalArgumentException("Se debe proporcionar al menos un ID de Tarea o Publicacion para guardar un recurso.");
        }

        if (recursoDTO.getId_tarea() != null) {
            Tarea tarea = tareaRepository.findById(recursoDTO.getId_tarea())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Tarea no encontrada con ID: " + recursoDTO.getId_tarea()));
            recurso.setTarea(tarea);
        } else {
            recurso.setTarea(null); // Si no se proporciona ID de Tarea, desvincular
        }

        if (recursoDTO.getId_publicacion() != null) {
            Publicacion publicacion = publicacionRepository.findById(recursoDTO.getId_publicacion())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Publicacion no encontrada con ID: " + recursoDTO.getId_publicacion()));
            recurso.setPublicacion(publicacion);
        } else {
            recurso.setPublicacion(null); // Si no se proporciona ID de Publicacion, desvincular
        }
        // ---------------------------------

        recurso = recursoRepository.save(recurso);

        // Mapear de vuelta al DTO y asegurar que los IDs de las relaciones se establezcan
        RecursoDTO responseDTO = modelMapper.map(recurso, RecursoDTO.class);
        if (recurso.getTarea() != null) {
            responseDTO.setId_tarea(recurso.getTarea().getIdTarea());
        }
        if (recurso.getPublicacion() != null) {
            responseDTO.setId_publicacion(recurso.getPublicacion().getIdPublicacion());
        }
        return responseDTO;
    }

    //Modificar
    public RecursoDTO modificar(Long id, RecursoDTO recursoDTO) {
        ModelMapper modelMapper = new ModelMapper();
        Recurso existe = recursoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Recurso no encontrado con ID: " + id)); // Usar la excepción personalizada

        // Guardar el ID original de existe para evitar errores de cambio de ID
        Long originalId = existe.getIdRecurso();
        modelMapper.map(recursoDTO, existe); // Mapea los campos del DTO a la entidad existente
        existe.setIdRecurso(originalId); // Restaura el ID original después del mapeo

        // --- MODIFICACIONES CLAVE AQUÍ ---
        // Asegurarse de que el DTO tenga al menos uno de los IDs, o ambos si es mandatorio
        if (recursoDTO.getId_tarea() == null && recursoDTO.getId_publicacion() == null) {
            // Decidir si es un error o si se permite desvincular si ambos son null
            // Por ahora, asumimos que al menos uno debe estar presente para la modificación
            throw new IllegalArgumentException("Al modificar, se debe proporcionar al menos un ID de Tarea o Publicacion para vincular el recurso.");
        }


        if (recursoDTO.getId_tarea() != null) {
            Tarea tarea = tareaRepository.findById(recursoDTO.getId_tarea())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Tarea no encontrada con ID: " + recursoDTO.getId_tarea()));
            existe.setTarea(tarea);
        } else {
            existe.setTarea(null); // Si el DTO envía null, desvincular
        }

        if (recursoDTO.getId_publicacion() != null) {
            Publicacion publicacion = publicacionRepository.findById(recursoDTO.getId_publicacion())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Publicacion no encontrada con ID: " + recursoDTO.getId_publicacion()));
            existe.setPublicacion(publicacion);
        } else {
            existe.setPublicacion(null); // Si el DTO envía null, desvincular
        }
        // ---------------------------------

        Recurso actualizado = recursoRepository.save(existe);

        // Mapear de vuelta al DTO y asegurar que los IDs de las relaciones se establezcan
        RecursoDTO dto = modelMapper.map(actualizado, RecursoDTO.class);
        if (actualizado.getTarea() != null) {
            dto.setId_tarea(actualizado.getTarea().getIdTarea());
        }
        if (actualizado.getPublicacion() != null) {
            dto.setId_publicacion(actualizado.getPublicacion().getIdPublicacion());
        }
        return dto;
    }

    //Eliminar lógico
    public RecursoDTO eliminar(Long id) {
        ModelMapper modelMapper = new ModelMapper();
        Recurso entidad = recursoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Recurso no encontrado con ID: " + id)); // Usar la excepción personalizada
        entidad.setEstado(false);
        entidad = recursoRepository.save(entidad);
        RecursoDTO dto = modelMapper.map(entidad, RecursoDTO.class);
        // Asegurarse de mapear los IDs de relaciones al DTO de respuesta
        if (entidad.getTarea() != null) {
            dto.setId_tarea(entidad.getTarea().getIdTarea());
        }
        if (entidad.getPublicacion() != null) {
            dto.setId_publicacion(entidad.getPublicacion().getIdPublicacion());
        }
        return dto;
    }
}