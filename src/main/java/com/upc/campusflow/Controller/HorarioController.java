package com.upc.campusflow.Controller;

import com.upc.campusflow.DTO.HorarioDTO;
import com.upc.campusflow.DTO.ProfesorDTO;
import com.upc.campusflow.Service.HorarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/horarios")
public class HorarioController {
    final HorarioService horarioService;

    public HorarioController(HorarioService horarioService) {
        this.horarioService = horarioService;
    }

    //listado

    @GetMapping
    public ResponseEntity<List<HorarioDTO>> listar(){
        return ResponseEntity.ok(horarioService.listar());
    }

    //guardar
    @PostMapping
    public ResponseEntity<HorarioDTO> guardar(@RequestBody HorarioDTO horarioDTO){
        return ResponseEntity.ok(horarioService.guardar(horarioDTO));
    }
    // Modificar
    @PutMapping("/{id}")
    public ResponseEntity<HorarioDTO> modificar(@PathVariable Long id, @RequestBody HorarioDTO horarioDTO) {
        return ResponseEntity.ok(horarioService.modificar(id, horarioDTO));
    }

    // Eliminar lógico
    @DeleteMapping("/{id}")
    public ResponseEntity<HorarioDTO> eliminarLogico(@PathVariable Long id) {
        return ResponseEntity.ok(horarioService.eliminar(id));
    }
}
