package com.upc.campusflow.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TareaDTO {
    private Long idTarea;
    private String titulo;
    private String descripcion;
    private Date fechaLimite;
    private String prioridad;
    private Long id_estudiante;  // ID del estudiante
    private Long id_horario;     // ID del horario/asignatura
    private boolean estado = true;

    // Campos adicionales para mostrar información relacionada (opcional)
    private String nombreEstudiante;   // Para mostrar el nombre del estudiante
    private String nombreAsignatura;   // Para mostrar el nombre de la asignatura
    private String profesorAsignatura; // Para mostrar el profesor de la asignatura
}
