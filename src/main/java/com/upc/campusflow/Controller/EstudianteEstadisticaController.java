package com.upc.campusflow.Controller;

import com.upc.campusflow.DTO.EstudianteEstadisticaDTO;
import com.upc.campusflow.Service.EstudianteEstadisticaService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@CrossOrigin
@RestController
@RequestMapping("/estudiante-estadistica")
public class EstudianteEstadisticaController {
    final EstudianteEstadisticaService estudianteEstadisticaService;

    public EstudianteEstadisticaController(EstudianteEstadisticaService estudianteEstadisticaService) {
        this.estudianteEstadisticaService = estudianteEstadisticaService;
    }
    //listar
    @GetMapping
    public ResponseEntity<List<EstudianteEstadisticaDTO>> Listar(){
        return ResponseEntity.ok(estudianteEstadisticaService.Listar());
    }
    //guardar
    @PostMapping
    public ResponseEntity<EstudianteEstadisticaDTO> guardar(@RequestBody EstudianteEstadisticaDTO estudianteEstadisticaDTO){
        return ResponseEntity.ok(estudianteEstadisticaService.guardar(estudianteEstadisticaDTO));
    }
    // Modificar
    @PutMapping("/{id}")
    public ResponseEntity<EstudianteEstadisticaDTO> modificar(@PathVariable Long id,
                                                              @RequestBody EstudianteEstadisticaDTO dto) {
        EstudianteEstadisticaDTO actualizado = estudianteEstadisticaService.modificar(id, dto);
        return ResponseEntity.ok(actualizado);
    }

    // Eliminar lógico
    @DeleteMapping("/{id}")
    public ResponseEntity<EstudianteEstadisticaDTO> eliminarLogico(@PathVariable Long id) {
        EstudianteEstadisticaDTO eliminado = estudianteEstadisticaService.eliminar(id);
        return ResponseEntity.ok(eliminado);
    }

}
