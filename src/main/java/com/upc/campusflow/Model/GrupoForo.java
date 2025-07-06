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
public class GrupoForo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long IdGrupoForo;
    private String Titulo;
    private String Descripcion;

    private String Campo;

    private Date FechaCreacion;

    //ver en asignatura implementación tmb
    @OneToOne
    @JoinColumn(name = "id_Asigneatura")
    private Asignatura asignatura;

    @OneToMany(mappedBy = "grupoForo", cascade  = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Publicacion> Publicaciones;

    private boolean Estado = true;
}
