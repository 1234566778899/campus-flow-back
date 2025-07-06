package com.upc.campusflow.Service;

import com.upc.campusflow.DTO.PublicacionDTO;
import com.upc.campusflow.Exception.RecursoNoEncontradoException;
import com.upc.campusflow.Model.GrupoForo;
import com.upc.campusflow.Model.Publicacion;
import com.upc.campusflow.Repository.GrupoForoRepository;
import com.upc.campusflow.Repository.PublicacionRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class PublicacionService {

    final PublicacionRepository publicacionRepository;
    final GrupoForoRepository grupoForoRepository;

    public PublicacionService(PublicacionRepository publicacionRepository, GrupoForoRepository grupoForoRepository) {
        this.publicacionRepository = publicacionRepository;
        this.grupoForoRepository = grupoForoRepository;
    }

    // Listar
    public List<PublicacionDTO> listar() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        // --- SUGGESTED CHANGE HERE ---
        // Explicitly define the mapping from Publicacion.grupoForo to PublicacionDTO.idGrupoForo
        modelMapper.createTypeMap(Publicacion.class, PublicacionDTO.class)
                .addMapping(src -> src.getGrupoForo().getIdGrupoForo(), PublicacionDTO::setIdGrupoForo)
                .addMappings(mapper -> {
                    // Ensure null handling for getGrupoForo()
                    mapper.when(context -> context.getSource() != null && ((Publicacion) context.getSource()).getGrupoForo() == null)
                            .map(src -> null, PublicacionDTO::setIdGrupoForo);
                });
        // This ensures ModelMapper knows how to get the ID, and handles null GrupoForo gracefully.
        // The `when` clause helps with cases where `getGrupoForo()` might return null directly.
        // Note: With STRICT, ModelMapper expects all properties to be mapped or skipped.
        // So, if other properties also cause issues, they might need explicit mapping or skipping.
        // For example, if there's a 'contenido' field that maps directly, you don't need a specific rule.
        // The error is specifically about `getGrupoForo()`, so we focus there.
        // --- END SUGGESTED CHANGE ---

        List<Publicacion> publicaciones = publicacionRepository.findAll().stream().filter(Publicacion::isEstado).toList();
        List<PublicacionDTO> publicacionDTOS = new ArrayList<>();

        for (Publicacion publicacion : publicaciones) {
            PublicacionDTO publicacionDTO = modelMapper.map(publicacion, PublicacionDTO.class);
            publicacionDTOS.add(publicacionDTO);
        }
        return publicacionDTOS;
    }

    // --- Rest of the methods remain the same as your last provided code block ---

    // Guardar
    public PublicacionDTO guardar(PublicacionDTO publicacionDTO) {
        ModelMapper modelMapper = new ModelMapper();
        Publicacion publicacion = modelMapper.map(publicacionDTO, Publicacion.class);

        if (publicacionDTO.getFecha() != null && publicacionDTO.getFecha().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de publicación no puede ser posterior a la fecha actual.");
        }
        if (publicacionDTO.getIdGrupoForo() == null) {
            throw new IllegalArgumentException("El ID de GrupoForo no puede ser nulo al registrar una publicación.");
        }

        Optional<GrupoForo> optionalGrupoForo = grupoForoRepository.findById(publicacionDTO.getIdGrupoForo());
        if (optionalGrupoForo.isPresent()) {
            publicacion.setGrupoForo(optionalGrupoForo.get());
        } else {
            throw new RecursoNoEncontradoException("No se encontró el GrupoForo con ID: " + publicacionDTO.getIdGrupoForo() + ". No se puede registrar la publicación.");
        }

        if (publicacion.getGrupoForo() == null) {
            throw new RuntimeException("Error interno: El objeto GrupoForo no pudo ser asignado a la Publicación antes de guardar.");
        }

        publicacion.setEstado(publicacionDTO.isEstado());
        publicacion = publicacionRepository.save(publicacion);

        PublicacionDTO dto = modelMapper.map(publicacion, PublicacionDTO.class);
        if (publicacion.getGrupoForo() != null) {
            dto.setIdGrupoForo(publicacion.getGrupoForo().getIdGrupoForo());
        }

        return dto;
    }

    // Modificar
    public PublicacionDTO modificar(Long id, PublicacionDTO publicacionDTO) {
        Publicacion existingPublicacion = publicacionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Publicación no encontrada con ID: " + id));

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        modelMapper.createTypeMap(PublicacionDTO.class, Publicacion.class)
                .addMappings(mapper -> {
                    mapper.skip(Publicacion::setIdPublicacion);
                    mapper.skip(Publicacion::setGrupoForo);
                });

        modelMapper.createTypeMap(Publicacion.class, PublicacionDTO.class)
                .addMappings(mapper -> {
                    mapper.map(src -> src.getGrupoForo() != null ? src.getGrupoForo().getIdGrupoForo() : null, PublicacionDTO::setIdGrupoForo);
                });

        if (publicacionDTO.getFecha() != null && publicacionDTO.getFecha().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de publicación no puede ser posterior a la fecha actual.");
        }
        if (publicacionDTO.getIdGrupoForo() == null) {
            throw new IllegalArgumentException("El ID de GrupoForo es requerido para modificar una publicación.");
        }

        GrupoForo grupoForo = grupoForoRepository.findById(publicacionDTO.getIdGrupoForo())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "GrupoForo con ID " + publicacionDTO.getIdGrupoForo() + " no encontrado."
                ));

        modelMapper.map(publicacionDTO, existingPublicacion);

        existingPublicacion.setGrupoForo(grupoForo);
        existingPublicacion.setEstado(publicacionDTO.isEstado());

        Publicacion updatedPublicacion = publicacionRepository.save(existingPublicacion);

        PublicacionDTO dto = modelMapper.map(updatedPublicacion, PublicacionDTO.class);

        return dto;
    }

    // Eliminar lógico
    public PublicacionDTO eliminar(Long id) {
        ModelMapper modelMapper = new ModelMapper();

        Publicacion publicacion = publicacionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Publicación no encontrada con ID: " + id));

        publicacion.setEstado(false);
        publicacion = publicacionRepository.save(publicacion);

        PublicacionDTO dto = modelMapper.map(publicacion, PublicacionDTO.class);
        if (publicacion.getGrupoForo() != null) {
            dto.setIdGrupoForo(publicacion.getGrupoForo().getIdGrupoForo());
        }
        return dto;
    }

    // Buscar por ID
    public PublicacionDTO buscarPorId(Long id) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.createTypeMap(Publicacion.class, PublicacionDTO.class)
                .addMappings(mapper -> {
                    mapper.map(src -> src.getGrupoForo() != null ? src.getGrupoForo().getIdGrupoForo() : null, PublicacionDTO::setIdGrupoForo);
                });

        Publicacion publicacion = publicacionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Publicación no encontrada con ID: " + id));

        PublicacionDTO dto = modelMapper.map(publicacion, PublicacionDTO.class);
        return dto;
    }

    // Resumen por Label
    public Map<String, Long> resumenPorLabel() {
        return publicacionRepository
                .countByLabel()
                .stream()
                .collect(Collectors.toMap(
                        arr -> (String) arr[0],
                        arr -> (Long) arr[1]
                ));
    }

    /**
     * Lista publicaciones de un grupo de foro con un label específico
     */
    public List<PublicacionDTO> listarPorGrupoYLabel(Long idGrupoForo, String label) {
        List<Publicacion> publicaciones = publicacionRepository.findByGrupoForoAndLabel(idGrupoForo, label);
        return mapToDTO(publicaciones);
    }

    /**
     * Lista publicaciones filtradas por grupo de foro y fecha exacta
     */
    public List<PublicacionDTO> listarPorGrupoYFecha(Long idGrupoForo, LocalDate fecha) {
        List<Publicacion> publicaciones = publicacionRepository.findByGrupoForoAndFecha(idGrupoForo, fecha);
        return mapToDTO(publicaciones);
    }

    // Método auxiliar para mapear List<Publicacion> a List<PublicacionDTO>
    private List<PublicacionDTO> mapToDTO(List<Publicacion> publicaciones) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        modelMapper.createTypeMap(Publicacion.class, PublicacionDTO.class)
                .addMappings(mapper -> {
                    mapper.map(src -> src.getGrupoForo() != null ? src.getGrupoForo().getIdGrupoForo() : null, PublicacionDTO::setIdGrupoForo);
                });

        List<PublicacionDTO> dtos = new ArrayList<>();
        for (Publicacion p : publicaciones) {
            dtos.add(modelMapper.map(p, PublicacionDTO.class));
        }
        return dtos;
    }
}