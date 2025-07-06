package com.upc.campusflow.Service;

import com.upc.campusflow.DTO.NotaDTO;
import com.upc.campusflow.Model.Asignatura;
import com.upc.campusflow.Model.Estudiante;
import com.upc.campusflow.Model.Nota;
import com.upc.campusflow.Repository.NotaRepository;
import com.upc.campusflow.Repository.AsignaturaRepository;
import com.upc.campusflow.Repository.EstudianteRepository;
import com.upc.campusflow.Exception.RecursoNoEncontradoException;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors; // Asegúrate de que esta importación esté presente

@Service
public class NotaService {

    final NotaRepository notaRepository;
    final AsignaturaRepository asignaturaRepository;
    final EstudianteRepository estudianteRepository;

    public NotaService(NotaRepository notaRepository, AsignaturaRepository asignaturaRepository, EstudianteRepository estudianteRepository) {
        this.notaRepository = notaRepository;
        this.asignaturaRepository = asignaturaRepository;
        this.estudianteRepository = estudianteRepository;
    }

    public List<NotaDTO> Listar(){
        List<Nota> notas = notaRepository.findAll()
                .stream()
                .filter(Nota::isEstado)
                .toList();

        List<NotaDTO> notaDTOS = new ArrayList<>();
        ModelMapper modelMapper = new ModelMapper();

        for (Nota nota : notas) {
            NotaDTO notaDTO = modelMapper.map(nota, NotaDTO.class);
            if(nota.getAsignatura() != null){
                notaDTO.setId_asignatura(nota.getAsignatura().getIdAsignatura());
                notaDTO.setNombreAsignatura(nota.getAsignatura().getNombre()); // ¡Añadido!
            }
            if(nota.getEstudiante() != null){
                notaDTO.setId_estudiante(nota.getEstudiante().getIdEstudiante());
            }
            notaDTOS.add(notaDTO);
        }
        return notaDTOS;
    }

    public NotaDTO guardar(NotaDTO notaDTO) {
        ModelMapper modelMapper = new ModelMapper();
        Nota nota = modelMapper.map(notaDTO, Nota.class);

        if (notaDTO.getId_asignatura() != null) {
            Asignatura asignatura = asignaturaRepository.findById(notaDTO.getId_asignatura())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Asignatura no encontrada con ese ID"));
            nota.setAsignatura(asignatura);
        } else {
            throw new IllegalArgumentException("El ID de la asignatura es obligatorio para registrar una nota.");
        }

        if (notaDTO.getId_estudiante() != null) {
            Estudiante estudiante = estudianteRepository.findById(notaDTO.getId_estudiante())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Estudiante no encontrado con ese ID"));
            nota.setEstudiante(estudiante);
        } else {
            throw new IllegalArgumentException("El ID del estudiante es obligatorio para registrar una nota.");
        }

        nota = notaRepository.save(nota);

        notaDTO = modelMapper.map(nota, NotaDTO.class);
        if(nota.getAsignatura() != null){
            notaDTO.setId_asignatura(nota.getAsignatura().getIdAsignatura());
            notaDTO.setNombreAsignatura(nota.getAsignatura().getNombre()); // ¡Añadido!
        }
        if(nota.getEstudiante() != null){
            notaDTO.setId_estudiante(nota.getEstudiante().getIdEstudiante());
        }
        return notaDTO;
    }

    public NotaDTO modificar(Long id, NotaDTO notaDTO) {
        ModelMapper modelMapper = new ModelMapper();
        Nota existente = notaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Nota no encontrada con ID: " + id));

        Long originalId = existente.getIdNota();
        modelMapper.map(notaDTO, existente);
        existente.setIdNota(originalId);

        if (notaDTO.getId_asignatura() != null) {
            Asignatura asignatura = asignaturaRepository.findById(notaDTO.getId_asignatura())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Asignatura no encontrada con ID: " + notaDTO.getId_asignatura()));
            existente.setAsignatura(asignatura);
        } else {
            existente.setAsignatura(null);
        }

        if (notaDTO.getId_estudiante() != null) {
            Estudiante estudiante = estudianteRepository.findById(notaDTO.getId_estudiante())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Estudiante no encontrado con ID: " + notaDTO.getId_estudiante()));
            existente.setEstudiante(estudiante);
        } else {
            existente.setEstudiante(null);
        }

        Nota actualizado = notaRepository.save(existente);
        NotaDTO dto = modelMapper.map(actualizado, NotaDTO.class);
        if (actualizado.getAsignatura() != null) {
            dto.setId_asignatura(actualizado.getAsignatura().getIdAsignatura());
            dto.setNombreAsignatura(actualizado.getAsignatura().getNombre()); // ¡Añadido!
        }
        if (actualizado.getEstudiante() != null) {
            dto.setId_estudiante(actualizado.getEstudiante().getIdEstudiante());
        }
        return dto;
    }

    public NotaDTO eliminar(Long id) {
        ModelMapper modelMapper = new ModelMapper();
        Nota entidad = notaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Nota no encontrada con ID: " + id));
        entidad.setEstado(false);
        entidad =  notaRepository.save(entidad);
        NotaDTO dto = modelMapper.map(entidad, NotaDTO.class);
        if (entidad.getAsignatura() != null) {
            dto.setId_asignatura(entidad.getAsignatura().getIdAsignatura());
            dto.setNombreAsignatura(entidad.getAsignatura().getNombre()); // ¡Añadido!
        }
        if (entidad.getEstudiante() != null) {
            dto.setId_estudiante(entidad.getEstudiante().getIdEstudiante());
        }
        return  dto;
    }

    public List<NotaDTO> obtenerNotasPorAsignatura(Long idAsignatura) {
        List<Nota> notas = notaRepository.findNotasByAsignaturaId(idAsignatura);
        List<NotaDTO> notaDTOS = new ArrayList<>();
        ModelMapper modelMapper = new ModelMapper();
        for (Nota nota : notas) {
            NotaDTO notaDTO = modelMapper.map(nota, NotaDTO.class);
            notaDTO.setId_asignatura(nota.getAsignatura().getIdAsignatura());
            notaDTO.setNombreAsignatura(nota.getAsignatura().getNombre()); // ¡Añadido!
            if (nota.getEstudiante() != null) {
                notaDTO.setId_estudiante(nota.getEstudiante().getIdEstudiante());
            }
            notaDTOS.add(notaDTO);
        }
        return notaDTOS;
    }

    public List<NotaDTO> obtenerNotasPorEstudianteYRangoPuntaje(Long idEstudiante, double puntajeMinimo, double puntajeMaximo) {
        List<Nota> notas = notaRepository.findByEstudianteIdAndPuntajeBetween(idEstudiante, puntajeMinimo, puntajeMaximo);
        List<NotaDTO> notaDTOS = new ArrayList<>();
        ModelMapper modelMapper = new ModelMapper();
        for (Nota nota : notas) {
            NotaDTO notaDTO = modelMapper.map(nota, NotaDTO.class);
            if (nota.getAsignatura() != null) {
                notaDTO.setId_asignatura(nota.getAsignatura().getIdAsignatura());
                notaDTO.setNombreAsignatura(nota.getAsignatura().getNombre()); // ¡Añadido!
            }
            if (nota.getEstudiante() != null) {
                notaDTO.setId_estudiante(nota.getEstudiante().getIdEstudiante());
            }
            notaDTOS.add(notaDTO);
        }
        return notaDTOS;
    }

    // ¡NUEVO MÉTODO CLAVE para obtener todas las notas de un estudiante!
    public List<NotaDTO> obtenerNotasPorEstudiante(Long idEstudiante) {
        List<Nota> notas = notaRepository.findByEstudianteId(idEstudiante); // Llama al método del repositorio
        List<NotaDTO> notaDTOS = new ArrayList<>();
        ModelMapper modelMapper = new ModelMapper();
        for (Nota nota : notas) {
            NotaDTO notaDTO = modelMapper.map(nota, NotaDTO.class);
            if (nota.getAsignatura() != null) {
                notaDTO.setId_asignatura(nota.getAsignatura().getIdAsignatura());
                notaDTO.setNombreAsignatura(nota.getAsignatura().getNombre()); // ¡Añadido!
            }
            if (nota.getEstudiante() != null) {
                notaDTO.setId_estudiante(nota.getEstudiante().getIdEstudiante());
            }
            notaDTOS.add(notaDTO);
        }
        return notaDTOS;
    }
}
