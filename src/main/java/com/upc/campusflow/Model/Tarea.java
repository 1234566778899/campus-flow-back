package com.upc.campusflow.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor

public class Tarea {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTarea;
    private String titulo;
    private String descripcion;
    private Date fechaLimite;
    private String prioridad;

    @ManyToOne
    @JoinColumn(name="id_estudiante")
    private Estudiante estudiante;

    @ManyToOne
    @JoinColumn(name="id_horario")
    private Horario horario;

    @OneToMany(mappedBy = "tarea", cascade  = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Recurso> recursos;

    private boolean estado = true;

}
