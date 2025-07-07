package com.upc.campusflow.Controller;

import com.upc.campusflow.DTO.EventoDTO;
import com.upc.campusflow.Service.EventoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/evento")
public class EventoController {

    final EventoService eventoService;

    public EventoController(EventoService eventoService) {
        this.eventoService = eventoService;
    }

    // Listar todos los eventos
    @GetMapping
    public ResponseEntity<List<EventoDTO>> listarEventos() {
        return ResponseEntity.ok(eventoService.listar());
    }

    // Obtener evento por ID
    @GetMapping("/{id}")
    public ResponseEntity<EventoDTO> buscarPorId(@PathVariable Long id) {
        EventoDTO evento = eventoService.buscarPorId(id);
        if (evento == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(evento);
    }

    // Guardar un nuevo evento
    @PostMapping
    public ResponseEntity<EventoDTO> guardarEvento(@RequestBody EventoDTO eventoDTO) {
        System.out.println("data: "+eventoDTO);
        return ResponseEntity.ok(eventoService.guardar(eventoDTO));
    }

    // Modificar un evento existente
    @PutMapping("/{id}")
    public ResponseEntity<EventoDTO> modificarEvento(@PathVariable Long id, @RequestBody EventoDTO eventoDTO) {
        return ResponseEntity.ok(eventoService.modificar(id, eventoDTO));
    }

    // Eliminar lógicamente un evento
    @DeleteMapping("/{id}")
    public ResponseEntity<EventoDTO> eliminarEvento(@PathVariable Long id) {
        return ResponseEntity.ok(eventoService.eliminar(id));
    }

    // Obtener eventos por ID de profesor
    @GetMapping("/profesor/{idProfesor}")
    public ResponseEntity<List<EventoDTO>> listarEventosPorProfesor(@PathVariable Long idProfesor) {
        return ResponseEntity.ok(eventoService.listarPorProfesor(idProfesor));
    }

    // Obtener los próximos 5 eventos de un estudiante
    @GetMapping("/estudiante/{idEstudiante}/proximos")
    public ResponseEntity<List<EventoDTO>> proximos5EventosDeEstudiante(@PathVariable Long idEstudiante) {
        return ResponseEntity.ok(eventoService.proximos5EventosDeEstudiante(idEstudiante));
    }

    // Obtener los 3 eventos con más participación
    @GetMapping("/top3-participacion")
    public ResponseEntity<List<EventoDTO>> top3EventosMasParticipacion() {
        return ResponseEntity.ok(eventoService.top3EventosMasParticipacion());
    }

    /**
     * Nuevo endpoint: Obtener eventos por ID de Carrera del profesor.

     */
    @GetMapping("/carrera/{idCarrera}")
    public ResponseEntity<List<EventoDTO>> obtenerEventosPorCarrera(@PathVariable Long idCarrera) {
        return ResponseEntity.ok(eventoService.obtenerEventosPorCarrera(idCarrera));
    }

    /**
     * Nuevo endpoint: Unir un estudiante a un evento.

     */
    @PostMapping("/{idEvento}/unirse/{idEstudiante}")
    public ResponseEntity<EventoDTO> unirseAEvento(@PathVariable Long idEvento, @PathVariable Long idEstudiante) {
        return ResponseEntity.ok(eventoService.unirseAEvento(idEvento, idEstudiante));
    }
}
