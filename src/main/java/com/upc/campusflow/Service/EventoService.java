package com.upc.campusflow.Service;

import com.upc.campusflow.DTO.EventoDTO;
import com.upc.campusflow.Exception.RecursoNoEncontradoException;
import com.upc.campusflow.Model.Evento;
import com.upc.campusflow.Model.Estudiante; // Importar Estudiante
import com.upc.campusflow.Model.Profesor;
import com.upc.campusflow.Repository.EventoRepository;
import com.upc.campusflow.Repository.ProfesorRepository;
import com.upc.campusflow.Repository.EstudianteRepository; // Importar EstudianteRepository
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Importar Transactional

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional; // Importar Optional

@Service
public class EventoService {

    final EventoRepository iEventoRepository;
    final ProfesorRepository iProfesorRepository;
    final EstudianteRepository iEstudianteRepository; // Inyectar EstudianteRepository
    private ModelMapper modelMapper=new ModelMapper();

    public EventoService(EventoRepository iEventoRepository, ProfesorRepository iProfesorRepository, EstudianteRepository iEstudianteRepository) {
        this.iEventoRepository = iEventoRepository;
        this.iProfesorRepository = iProfesorRepository;
        this.iEstudianteRepository = iEstudianteRepository; // Inicializar EstudianteRepository
    }


    public EventoDTO guardar(EventoDTO eventoDTO) {
        Evento evento = modelMapper.map(eventoDTO, Evento.class);

        // Si necesitas establecer el profesor basado en idProfesor
        if (eventoDTO.getIdProfesor() != null) {
            Profesor profesor = new Profesor();
            profesor.setIdProfesor(eventoDTO.getIdProfesor());
            evento.setProfesor(profesor);
        }

        Evento eventoGuardado = iEventoRepository.save(evento);
        return modelMapper.map(eventoGuardado, EventoDTO.class);
    }

    public EventoDTO buscarPorId(Long id) {
        Evento evento = iEventoRepository.findById(id).orElse(null);
        if (evento == null) return null;

        ModelMapper modelMapper = new ModelMapper();
        EventoDTO eventoDTO = modelMapper.map(evento, EventoDTO.class);
        if (evento.getProfesor() != null) {
            eventoDTO.setIdProfesor(evento.getProfesor().getIdProfesor());
        }
        return eventoDTO;
    }

    public List<EventoDTO> listar() {
        List<Evento> eventos = iEventoRepository.findAll();
        List<EventoDTO> eventoDTOS = new ArrayList<>();
        ModelMapper modelMapper = new ModelMapper();
        for (Evento evento : eventos) {
            EventoDTO dto = modelMapper.map(evento, EventoDTO.class);
            // Asegurarse de que el ID del profesor se mapea correctamente
            if (evento.getProfesor() != null) {
                dto.setIdProfesor(evento.getProfesor().getIdProfesor());
            }
            eventoDTOS.add(dto);
        }
        return eventoDTOS;
    }

    public EventoDTO modificar(Long id, EventoDTO eventoDTO) {
        Evento existingEvento = iEventoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Evento no encontrado con ID: " + id));

        if (!existingEvento.isEstado() && !eventoDTO.isEstado()) {
            throw new IllegalArgumentException("No se puede modificar un evento inactivo (estado=false). Para modificarlo, debe cambiar su estado a activo (estado=true) en la petición.");
        }

        if (eventoDTO.getIdProfesor() == null) {
            throw new IllegalArgumentException("El ID del profesor es requerido para modificar un evento.");
        }
        Profesor profesor = iProfesorRepository.findById(eventoDTO.getIdProfesor())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Profesor con ID " + eventoDTO.getIdProfesor() + " no encontrado."
                ));

        if (eventoDTO.getFechaInicio() == null || eventoDTO.getFechaFin() == null) {
            throw new IllegalArgumentException("Fecha de inicio y Fecha de fin son requeridas.");
        }
        if (eventoDTO.getFechaFin().isBefore(eventoDTO.getFechaInicio())) {
            throw new IllegalArgumentException("La Fecha de Fin no puede ser anterior a la Fecha de Inicio.");
        }

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        modelMapper.createTypeMap(EventoDTO.class, Evento.class)
                .addMappings(mapper -> {
                    mapper.skip(Evento::setIdEvento);
                    mapper.skip(Evento::setProfesor);
                    mapper.skip(Evento::setEstudiantes);
                });

        modelMapper.map(eventoDTO, existingEvento);

        existingEvento.setProfesor(profesor);
        existingEvento.setEstado(eventoDTO.isEstado());

        Evento updatedEvento = iEventoRepository.save(existingEvento);

        EventoDTO returnDTO = modelMapper.map(updatedEvento, EventoDTO.class);
        if (updatedEvento.getProfesor() != null) {
            returnDTO.setIdProfesor(updatedEvento.getProfesor().getIdProfesor());
        }
        return returnDTO;
    }

    public EventoDTO eliminar(Long id) {
        ModelMapper modelMapper = new ModelMapper();
        Evento evento = iEventoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evento no encontrado con ID: " + id));

        evento.setEstado(false);
        evento = iEventoRepository.save(evento);
        return modelMapper.map(evento, EventoDTO.class);
    }

    public List<EventoDTO> listarPorProfesor(Long idProfesor) {
        List<Evento> eventos = iEventoRepository.findEventosByProfesorId(idProfesor);
        List<EventoDTO> eventoDTOs = new ArrayList<>();
        ModelMapper modelMapper = new ModelMapper();
        for (Evento evento : eventos) {
            EventoDTO dto = modelMapper.map(evento, EventoDTO.class);
            dto.setIdProfesor(evento.getProfesor().getIdProfesor());
            eventoDTOs.add(dto);
        }
        return eventoDTOs;
    }

    public List<EventoDTO> proximos5EventosDeEstudiante(Long idEstudiante) {
        List<Evento> eventos = iEventoRepository.findNext5EventosByEstudiante(
                idEstudiante,
                LocalDate.now(),
                PageRequest.of(0, 5)
        );
        List<EventoDTO> eventosDTO = new ArrayList<>();
        ModelMapper modelMapper = new ModelMapper();
        for (Evento evento : eventos) {
            // Asegurarse de que el ID del profesor se mapea correctamente
            EventoDTO dto = modelMapper.map(evento, EventoDTO.class);
            if (evento.getProfesor() != null) {
                dto.setIdProfesor(evento.getProfesor().getIdProfesor());
            }
            eventosDTO.add(dto);
        }
        return eventosDTO;
    }


    private final ModelMapper mapper = new ModelMapper();
    /**
     * Devuelve los 3 eventos en los que más estudiantes participaron
     */
    public List<EventoDTO> top3EventosMasParticipacion() {
        var eventos = iEventoRepository.findTop3EventosByParticipacion(PageRequest.of(0, 3));
        List<EventoDTO> dtos = new ArrayList<>();
        for (Evento ev : eventos) {
            // Asegurarse de que el ID del profesor se mapea correctamente
            EventoDTO dto = mapper.map(ev, EventoDTO.class);
            if (ev.getProfesor() != null) {
                dto.setIdProfesor(ev.getProfesor().getIdProfesor());
            }
            dtos.add(dto);
        }
        return dtos;
    }

    /**
     * Nuevo método de servicio: Obtener eventos por ID de Carrera del profesor.
     * @param idCarrera El ID de la carrera.
     * @return Una lista de EventoDTO activos y futuros relacionados con esa carrera.
     */
    public List<EventoDTO> obtenerEventosPorCarrera(Long idCarrera) {
        List<Evento> eventos = iEventoRepository.findByProfesorCarreraIdAndEstadoTrueAndUpcoming(idCarrera);
        List<EventoDTO> eventoDTOS = new ArrayList<>();
        ModelMapper modelMapper = new ModelMapper();
        for (Evento evento : eventos) {
            EventoDTO dto = modelMapper.map(evento, EventoDTO.class);
            if (evento.getProfesor() != null) {
                dto.setIdProfesor(evento.getProfesor().getIdProfesor());
            }
            eventoDTOS.add(dto);
        }
        return eventoDTOS;
    }

    /**
     * Nuevo método de servicio: Unir un estudiante a un evento.
     * @param idEvento El ID del evento al que unirse.
     * @param idEstudiante El ID del estudiante que se une.
     * @return El EventoDTO actualizado.
     * @throws RecursoNoEncontradoException Si el evento o el estudiante no se encuentran.
     * @throws IllegalArgumentException Si el estudiante ya está unido al evento.
     */
    @Transactional // Asegura que la operación sea atómica
    public EventoDTO unirseAEvento(Long idEvento, Long idEstudiante) {
        Evento evento = iEventoRepository.findById(idEvento)
                .orElseThrow(() -> new RecursoNoEncontradoException("Evento no encontrado con ID: " + idEvento));

        Estudiante estudiante = iEstudianteRepository.findById(idEstudiante)
                .orElseThrow(() -> new RecursoNoEncontradoException("Estudiante no encontrado con ID: " + idEstudiante));

        // Verificar si el estudiante ya está unido al evento
        if (evento.getEstudiantes().contains(estudiante)) {
            throw new IllegalArgumentException("El estudiante ya está unido a este evento.");
        }

        // Añadir el estudiante a la lista de estudiantes del evento
        evento.getEstudiantes().add(estudiante);
        // Asegurarse de que la relación inversa también se actualice si es necesario (aunque JPA lo maneja bien)
        // if (!estudiante.getEventos().contains(evento)) {
        //     estudiante.getEventos().add(evento);
        // }

        Evento updatedEvento = iEventoRepository.save(evento);
        // iEstudianteRepository.save(estudiante); // No es estrictamente necesario si la relación es bidireccional y el owning side es Evento

        ModelMapper modelMapper = new ModelMapper();
        EventoDTO returnDTO = modelMapper.map(updatedEvento, EventoDTO.class);
        if (updatedEvento.getProfesor() != null) {
            returnDTO.setIdProfesor(updatedEvento.getProfesor().getIdProfesor());
        }
        return returnDTO;
    }
}
