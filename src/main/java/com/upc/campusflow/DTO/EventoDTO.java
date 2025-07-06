package com.upc.campusflow.DTO;


import com.upc.campusflow.Model.Estudiante;
import com.upc.campusflow.Model.Profesor;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventoDTO {
    private Long IdEvento;
    private String Nombre;
    private LocalDate FechaInicio;
    private LocalDate FechaFin;
    private String Descripcion;
    private int PuntajeRecompensa;
    private Long idProfesor;
    private boolean Estado;
}
