package com.upc.campusflow.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor

public class Nota {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idNota;
    private String Tipo;
    private double Puntaje;
    private double Peso_Nota;
    @ManyToOne
    @JoinColumn(name = "id_asignatura")
    private Asignatura asignatura;
    private boolean Estado = true;

    @ManyToOne
    @JoinColumn(name = "id_estudiante")
    private Estudiante estudiante;

}
