package com.upc.campusflow.Controller;

import com.upc.campusflow.DTO.RecursoDTO;
import com.upc.campusflow.Service.RecursoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.logging.Logger;
@CrossOrigin
@RestController
@RequestMapping("/recursos")
public class RecursoController {
    final RecursoService recursoService;

    public RecursoController(RecursoService recursoService) { this.recursoService = recursoService;
    }

    // Obtener lista de recursos
    @GetMapping
    public ResponseEntity<List<RecursoDTO>> listar() {
        return ResponseEntity.ok(recursoService.listar());  // Devuelve la lista de recursos
    }
    //guardar
    @PostMapping
    public ResponseEntity<RecursoDTO> guardar(@RequestBody RecursoDTO recursoDTO) {
        return ResponseEntity.ok(recursoService.guardar(recursoDTO));  // Devuelve el recurso guardado con un código 201
    }
    //modificar
    @PutMapping("/{id}")
    public ResponseEntity<RecursoDTO> modificar(@PathVariable Long id, @RequestBody RecursoDTO recursoDTO) {
        return ResponseEntity.ok(recursoService.modificar(id, recursoDTO));
    }
    //Eliminar
    @DeleteMapping("/{id}")
    public ResponseEntity<RecursoDTO> eliminar(@PathVariable Long id) {
        return ResponseEntity.ok(recursoService.eliminar(id));
    }
}
