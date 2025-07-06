package com.upc.campusflow.Controller;

import com.upc.campusflow.DTO.TareaDTO;
import com.upc.campusflow.Service.TareaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200") // Especifica el origen del frontend
@RestController
@RequestMapping("/api/campusflow/tareas") // Ruta corregida para coincidir con el servicio Angular
public class TareaController {
    private final TareaService tareaService;

    public TareaController(TareaService tareaService) {
        this.tareaService = tareaService;
    }

    // Obtener lista de tareas
    @GetMapping
    public ResponseEntity<List<TareaDTO>> listar() {
        try {
            List<TareaDTO> tareas = tareaService.listar();
            return ResponseEntity.ok(tareas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Guardar nueva tarea
    @PostMapping
    public ResponseEntity<TareaDTO> guardar(@RequestBody TareaDTO tareaDTO) {
        try {
            TareaDTO tareaPersistida = tareaService.guardar(tareaDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(tareaPersistida);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }



    // Modificar tarea existente
    @PutMapping("/{id}")
    public ResponseEntity<TareaDTO> modificar(@PathVariable Long id, @RequestBody TareaDTO tareaDTO) {
        try {
            TareaDTO tareaActualizada = tareaService.modificar(id, tareaDTO);
            return ResponseEntity.ok(tareaActualizada);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Eliminar tarea por ID (eliminación lógica)
    @DeleteMapping("/{id}")
    public ResponseEntity<TareaDTO> eliminar(@PathVariable Long id) {
        try {
            TareaDTO tareaEliminada = tareaService.eliminar(id);
            return ResponseEntity.ok(tareaEliminada);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Obtener todas las tareas activas de un estudiante específico
    @GetMapping("/estudiante/{idEstudiante}/activas")
    public ResponseEntity<List<TareaDTO>> tareasActivasPorEstudiante(@PathVariable Long idEstudiante) {
        try {
            List<TareaDTO> tareaDTOS = tareaService.TareasActivasPorEstudiante(idEstudiante);
            return ResponseEntity.ok(tareaDTOS);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Obtener las tareas con una prioridad específica y ordenarlas por fecha límite
    @GetMapping("/prioridad/{prioridad}")
    public ResponseEntity<List<TareaDTO>> tareasPorPrioridad(@PathVariable String prioridad) {
        try {
            List<TareaDTO> tareaDTOS = tareaService.TareasPorPrioridad(prioridad);
            return ResponseEntity.ok(tareaDTOS);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}