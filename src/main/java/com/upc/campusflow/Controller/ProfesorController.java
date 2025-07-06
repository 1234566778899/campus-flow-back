package com.upc.campusflow.Controller;

import com.upc.campusflow.DTO.ProfesorDTO;
import com.upc.campusflow.DTO.RegisterProfesorRequest; // Importar el nuevo DTO
import com.upc.campusflow.Service.ProfesorService;
import com.upc.campusflow.Exception.RecursoNoEncontradoException; // Importar tu excepción personalizada
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@CrossOrigin
@RestController
@RequestMapping("/profesor")
public class ProfesorController {
    final ProfesorService profesorService;

    public ProfesorController(ProfesorService profesorService) {
        this.profesorService = profesorService;
    }

    //listar
    @GetMapping
    public ResponseEntity<List<ProfesorDTO>> listar(){
        return ResponseEntity.ok(profesorService.listar());
    }

    //guardar
    @PostMapping
    public ResponseEntity<ProfesorDTO> guardar(@RequestBody ProfesorDTO profesorDTO){
        return ResponseEntity.ok(profesorService.guardar(profesorDTO));
    }
    // Modificar
    @PutMapping("/{id}")
    public ResponseEntity<ProfesorDTO> modificar(@PathVariable Long id, @RequestBody ProfesorDTO profesorDTO) {
        return ResponseEntity.ok(profesorService.modificar(id, profesorDTO));
    }

    // Eliminar lógico
    @DeleteMapping("/{id}")
    public ResponseEntity<ProfesorDTO> eliminarLogico(@PathVariable Long id) {
        return ResponseEntity.ok(profesorService.eliminar(id));
    }

    // --- Nuevo endpoint para el registro combinado de Profesor y Usuario ---
    @PostMapping("/register") // Puedes elegir un path diferente si lo deseas, ej. /profesor/crear
    public ResponseEntity<ProfesorDTO> registerProfesor(@RequestBody RegisterProfesorRequest request) {
        System.out.println("Starting professor registration with payload: " + request.toString()); // Opcional: usar System.out para depuración básica
        try {
            ProfesorDTO nuevoProfesor = profesorService.registrarProfesor(request);
            System.out.println("Professor registered successfully with ID: " + nuevoProfesor.getIdProfesor()); // Opcional
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoProfesor);
        } catch (IllegalArgumentException e) {
            System.err.println("Validation error during professor registration: " + e.getMessage()); // Opcional
            return ResponseEntity.badRequest().body(new ProfesorDTO()); // Retorna un DTO vacío o un objeto de error
        } catch (RecursoNoEncontradoException e) {
            System.err.println("Resource not found during professor registration: " + e.getMessage()); // Opcional
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ProfesorDTO()); // Retorna un DTO vacío o un objeto de error
        } catch (Exception e) {
            System.err.println("Unexpected error during professor registration: " + e.getMessage()); // Opcional
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ProfesorDTO()); // Retorna un DTO vacío o un objeto de error
        }
    }
}
