package com.upc.campusflow.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Recompensa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long IDRecompensa;
    private String Plataforma;
    private String URL;

    @ManyToOne
    @JoinColumn(name = "id_EstudianteEstadistica")
    private EstudianteEstadistica estudianteEstadistica;

    private boolean Estado = true;
}
