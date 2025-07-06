package com.upc.campusflow.Service;

import com.upc.campusflow.DTO.RecompensaDTO;
import com.upc.campusflow.Exception.RecursoNoEncontradoException;
import com.upc.campusflow.Model.EstudianteEstadistica;
import com.upc.campusflow.Model.Recompensa;
import com.upc.campusflow.Repository.EstudianteEstadisticaRepository;
import com.upc.campusflow.Repository.RecompensaRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecompensaService {
    final RecompensaRepository recompensaRepository;
    final EstudianteEstadisticaRepository estudianteEstadisticaRepository;

    public RecompensaService(RecompensaRepository recompensaRepository, EstudianteEstadisticaRepository estudianteEstadisticaRepository) {
        this.recompensaRepository = recompensaRepository;
        this.estudianteEstadisticaRepository = estudianteEstadisticaRepository;
    }

    //Listar
    public List<RecompensaDTO> Listar() {
        List<Recompensa> recompensas = recompensaRepository.findAll().stream()
                .filter(Recompensa::isEstado)
                .collect(Collectors.toList());

        List<RecompensaDTO> recompensaDTOS = new ArrayList<>();
        ModelMapper modelMapper = new ModelMapper();

        for (Recompensa recompensa : recompensas) {
            RecompensaDTO recompensaDTO = modelMapper.map(recompensa, RecompensaDTO.class);
            if (recompensa.getEstudianteEstadistica() != null) {
                recompensaDTO.setId_EstudianteEstadistica(recompensa.getEstudianteEstadistica().getIdEstudianteEstadistica());
            }
            recompensaDTOS.add(recompensaDTO);
        }
        return recompensaDTOS;
    }

    //Guardar
    public RecompensaDTO guardar(RecompensaDTO recompensaDTO){
        ModelMapper modelMapper = new ModelMapper();
        Recompensa recompensa = modelMapper.map(recompensaDTO, Recompensa.class);

        // --- MODIFICACIÓN AQUÍ para asegurar que no sea null al guardar ---
        if(recompensaDTO.getId_EstudianteEstadistica() == null){
            throw new IllegalArgumentException("El ID de EstudianteEstadistica es obligatorio para guardar una recompensa.");
        }

        EstudianteEstadistica estudianteEstadistica = estudianteEstadisticaRepository
                .findById(recompensaDTO.getId_EstudianteEstadistica())
                .orElseThrow(() -> new RecursoNoEncontradoException("EstudianteEstadistica no encontrada con ese ID"));

        recompensa.setEstudianteEstadistica(estudianteEstadistica);
        // ------------------------------------------------------------------

        recompensa = recompensaRepository.save(recompensa);
        recompensaDTO = modelMapper.map(recompensa, RecompensaDTO.class);
        if (recompensa.getEstudianteEstadistica() != null) {
            recompensaDTO.setId_EstudianteEstadistica(recompensa.getEstudianteEstadistica().getIdEstudianteEstadistica());
        }
        return recompensaDTO;
    }

    //Modificar
    public RecompensaDTO modificar(Long id, RecompensaDTO recompensaDTO) {
        Recompensa existente = recompensaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Recompensa no encontrada con ID: " + id));

        ModelMapper modelMapper = new ModelMapper();

        Long originalId = existente.getIDRecompensa();
        modelMapper.map(recompensaDTO, existente);
        existente.setIDRecompensa(originalId);

        // --- MODIFICACIÓN AQUÍ para asegurar que no sea null al modificar ---
        if (recompensaDTO.getId_EstudianteEstadistica() == null) {
            throw new IllegalArgumentException("El ID de EstudianteEstadistica es obligatorio para modificar una recompensa.");
        }

        EstudianteEstadistica estudianteEstadistica = estudianteEstadisticaRepository
                .findById(recompensaDTO.getId_EstudianteEstadistica())
                .orElseThrow(() -> new RecursoNoEncontradoException("EstudianteEstadistica no encontrada con ese ID: " + recompensaDTO.getId_EstudianteEstadistica()));

        existente.setEstudianteEstadistica(estudianteEstadistica);
        // ------------------------------------------------------------------

        Recompensa actualizado = recompensaRepository.save(existente);
        RecompensaDTO dto = modelMapper.map(actualizado, RecompensaDTO.class);
        if (actualizado.getEstudianteEstadistica() != null) {
            dto.setId_EstudianteEstadistica(actualizado.getEstudianteEstadistica().getIdEstudianteEstadistica());
        }
        return dto;
    }

    //Eliminar lógico
    public RecompensaDTO eliminar(Long id) {
        ModelMapper modelMapper = new ModelMapper();
        Recompensa entidad = recompensaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Recompenza no encontrada con ID: " + id));
        entidad.setEstado(false);
        entidad = recompensaRepository.save(entidad);
        RecompensaDTO dto = modelMapper.map(entidad, RecompensaDTO.class);
        return dto;
    }
}