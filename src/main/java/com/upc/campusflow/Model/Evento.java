package com.upc.campusflow.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Evento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long IdEvento;

    private String Nombre;
    private LocalDate FechaInicio;
    private LocalDate FechaFin;
    private String Descripcion;
    private int PuntajeRecompensa;


    @ManyToOne
    @JoinColumn(name = "IdProfesor")
    private Profesor profesor;


    @ManyToMany
    @JoinTable(
            name = "evento_estudiante",
            joinColumns = @JoinColumn(name = "evento_id"),
            inverseJoinColumns = @JoinColumn(name = "estudiante_id")
    )
    private List<Estudiante> estudiantes;

    private boolean Estado = true;
}
