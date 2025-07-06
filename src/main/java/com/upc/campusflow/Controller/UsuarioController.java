package com.upc.campusflow.Controller;

import com.upc.campusflow.DTO.UsuarioDTO;
import com.upc.campusflow.Service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/usuarios")
public class UsuarioController {
    final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    //listar
    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> Listar(){
        return ResponseEntity.ok(usuarioService.listar());
    }

    //guardar
    @PostMapping
    public ResponseEntity<UsuarioDTO> guardar(@RequestBody UsuarioDTO usuarioDTO){
        return ResponseEntity.ok(usuarioService.guardar(usuarioDTO));
    }

    // Buscar por Id
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> BuscarPorID(@PathVariable Long id){
        return ResponseEntity.ok(usuarioService.buscarPorId(id));
    }


    // Modificar
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDTO> modificar(@PathVariable Long id, @RequestBody UsuarioDTO usuarioDTO) {
        return ResponseEntity.ok(usuarioService.modificar(id, usuarioDTO));
    }

    // Eliminar lógico
    @DeleteMapping("/{id}")
    public ResponseEntity<UsuarioDTO> eliminarLogico(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.eliminar(id));
    }

}