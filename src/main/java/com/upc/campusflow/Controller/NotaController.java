package com.upc.campusflow.Controller;

import com.upc.campusflow.DTO.NotaDTO;
import com.upc.campusflow.Service.NotaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@CrossOrigin
@RestController
@RequestMapping("/nota")
public class NotaController {
    final NotaService notaService;

    public NotaController(NotaService notaService) {
        this.notaService = notaService;
    }
    //listar
    @GetMapping
    public ResponseEntity<List<NotaDTO>> Listar(){
        return ResponseEntity.ok(notaService.Listar());
    }
    //guardar
    @PostMapping
    public ResponseEntity<NotaDTO> guardar(@RequestBody NotaDTO notaDTO){
        return ResponseEntity.ok(notaService.guardar(notaDTO));
    }
    // Modificar
    @PutMapping("/{id}")
    public ResponseEntity<NotaDTO> modificar(@PathVariable Long id, @RequestBody NotaDTO notaDTO) {
        return ResponseEntity.ok(notaService.modificar(id, notaDTO));
    }

    // Eliminar lógico
    @DeleteMapping("/{id}")
    public ResponseEntity<NotaDTO> eliminarLogico(@PathVariable Long id) {
        return ResponseEntity.ok(notaService.eliminar(id));
    }


    @GetMapping("/asignatura/{id}")
    public ResponseEntity<List<NotaDTO>> obtenerNotasPorAsignatura(@PathVariable("id") Long idAsignatura) {
        return ResponseEntity.ok(notaService.obtenerNotasPorAsignatura(idAsignatura));
    }

    // Obtener notas de un estudiante dentro de un rango de puntaje
    @GetMapping("/estudiante/{idEstudiante}/rango")
    public ResponseEntity<List<NotaDTO>> obtenerNotasPorEstudianteYRangoPuntaje(
            @PathVariable Long idEstudiante,
            @RequestParam double puntajeMinimo,
            @RequestParam double puntajeMaximo) {
        List<NotaDTO> notas = notaService.obtenerNotasPorEstudianteYRangoPuntaje(idEstudiante, puntajeMinimo, puntajeMaximo);
        return ResponseEntity.ok(notas);
    }

    // ¡NUEVO ENDPOINT para obtener todas las notas de un estudiante!
    // Esta ruta es /nota/estudiante/{idEstudiante}
    @GetMapping("/estudiante/{idEstudiante}")
    public ResponseEntity<List<NotaDTO>> obtenerNotasDeEstudiante(@PathVariable Long idEstudiante) {
        return ResponseEntity.ok(notaService.obtenerNotasPorEstudiante(idEstudiante));
    }
}
