package com.upc.campusflow.Controller;

import com.upc.campusflow.DTO.GrupoForoDTO;
import com.upc.campusflow.Service.GrupoForoService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@CrossOrigin
@RestController
@RequestMapping("/grupoForo")
public class GrupoForoController {

    final GrupoForoService grupoForoService;

    public GrupoForoController(GrupoForoService grupoForoService) {
        this.grupoForoService = grupoForoService;
    }

    //listar
    @GetMapping
    public ResponseEntity<List<GrupoForoDTO>> Listar(){
        return ResponseEntity.ok(grupoForoService.listar());
    }
    //guardar
    @PostMapping
    public ResponseEntity<GrupoForoDTO> guardar(@RequestBody GrupoForoDTO grupoForoDTO){
        return ResponseEntity.ok(grupoForoService.guardar(grupoForoDTO));
    }
    // Modificar
    @PutMapping("/{id}")
    public ResponseEntity<GrupoForoDTO> modificar(@PathVariable Long id,
                                                  @RequestBody GrupoForoDTO dto) {
        GrupoForoDTO actualizado = grupoForoService.modificar(id, dto);
        return ResponseEntity.ok(actualizado);
    }

    // Eliminar lógico
    @DeleteMapping("/{id}")
    public ResponseEntity<GrupoForoDTO> eliminarLogico(@PathVariable Long id) {
        GrupoForoDTO eliminado = grupoForoService.eliminar(id);
        return ResponseEntity.ok(eliminado);
    }


}