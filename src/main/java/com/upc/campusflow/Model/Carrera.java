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
public class Carrera {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCarrera;
    private String Nombre;
    private String Malla_curricular;
    @OneToMany(mappedBy = "carrera", cascade  = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Asignatura> asignaturas;

    @OneToMany(mappedBy = "carrera", cascade  = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Estudiante> estudiantes;

    @OneToMany(mappedBy = "carrera", cascade  = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Profesor> profesores;

    private boolean Estado = true;
}
