package com.upc.campusflow.Controller;


import com.upc.campusflow.DTO.CarreraDTO;
import com.upc.campusflow.Service.CarreraService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@CrossOrigin
@RestController
@RequestMapping("/carrera")
public class CarreraController {
    final CarreraService carreraService;

    public CarreraController(CarreraService carreraService) {
        this.carreraService = carreraService;
    }

    //listar
    @GetMapping
    public ResponseEntity<List<CarreraDTO>> Listar(){
        return ResponseEntity.ok(carreraService.listar());
    }
    //guardar
    @PostMapping
    public ResponseEntity<CarreraDTO> guardar(@RequestBody CarreraDTO carreraDTO){
        return ResponseEntity.ok(carreraService.guardar(carreraDTO));
    }

    // Modificar
    @PutMapping("/{id}")
    public ResponseEntity<CarreraDTO> modificar(@PathVariable Long id, @RequestBody CarreraDTO dto) {
        return ResponseEntity.ok(carreraService.modificar(id, dto));
    }

    // Eliminar lógico
    @DeleteMapping("/{id}")
    public ResponseEntity<CarreraDTO> eliminarLogico(@PathVariable Long id) {
        return ResponseEntity.ok(carreraService.eliminar(id));
    }

}

