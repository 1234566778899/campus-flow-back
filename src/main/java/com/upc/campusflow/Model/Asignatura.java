package com.upc.campusflow.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Asignatura {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idAsignatura;
    private String Nombre;
    private double Creditos;

    private int Ciclo_Academico;

    @ManyToOne
    @JoinColumn(name ="id_carrera")
    private Carrera carrera;

    @OneToMany(mappedBy = "asignatura", cascade  = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Horario> horarios;

    @OneToMany(mappedBy = "asignatura", cascade  = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Nota> notas;
    private boolean Estado = true;
}
