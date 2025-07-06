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
public class Estudiante {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idEstudiante;

    private int Ciclo;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Nota> notas;

    @ManyToOne
    @JoinColumn(name = "idCarrera")
    private Carrera carrera;

    // --- ¡CAMBIO CRÍTICO AQUÍ! ---
    // Hacemos que Estudiante sea el dueño de la relación con Usuario
    @OneToOne // Un estudiante tiene un Usuario, y un Usuario puede ser un Estudiante.
    @JoinColumn(name = "usuario_id") // Esta será la columna de clave foránea en la tabla 'estudiante'
    private Usuario usuarios; // Mantén el nombre del campo 'usuarios' si así lo usas en el servicio

    @ManyToMany(mappedBy = "estudiantes")
    private List<Evento> eventos;

    private boolean Estado = true;

}
