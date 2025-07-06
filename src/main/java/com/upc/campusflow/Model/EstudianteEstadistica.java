package com.upc.campusflow.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EstudianteEstadistica {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idEstudianteEstadistica;

   //
    private LocalDateTime TotalHoraEstudiante;

    private int TareasCompletadas;

    //
    private LocalDate UltimaConexion;

    //Ver
    @OneToOne
    @JoinColumn(name = "id_estudiante")
    private Estudiante estudiante;

    @OneToMany(mappedBy = "estudianteEstadistica", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Recompensa> recompensas;

    private boolean Estado = true;
}