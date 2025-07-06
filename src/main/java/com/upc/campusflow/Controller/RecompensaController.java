package com.upc.campusflow.Controller;

import com.upc.campusflow.DTO.RecompensaDTO;
import com.upc.campusflow.Service.RecompensaService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@CrossOrigin
@RestController
@RequestMapping("/recompensa")
public class RecompensaController {
    final RecompensaService recompensaService;

    public RecompensaController(RecompensaService recompensaService) {
        this.recompensaService = recompensaService;
    }
    //listar
    @GetMapping
    public ResponseEntity<List<RecompensaDTO>> Listar(){
        return ResponseEntity.ok(recompensaService.Listar());
    }
    //guardar
    @PostMapping
    public ResponseEntity<RecompensaDTO> guardar(@RequestBody RecompensaDTO recompensaDTO){
        return ResponseEntity.ok(recompensaService.guardar(recompensaDTO));
    }
    // Modificar
    @PutMapping("/{id}")
    public ResponseEntity<RecompensaDTO> modificar(@PathVariable Long id, @RequestBody RecompensaDTO dto) {
        RecompensaDTO actualizado = recompensaService.modificar(id, dto);
        return ResponseEntity.ok(actualizado);
    }

    // Eliminar lógico
    @DeleteMapping("/{id}")
    public ResponseEntity<RecompensaDTO> eliminarLogico(@PathVariable Long id) {
        RecompensaDTO eliminado = recompensaService.eliminar(id);
        return ResponseEntity.ok(eliminado);
    }
}
