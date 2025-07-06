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
public class Publicacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long IdPublicacion;
    private String Contenido;
    private LocalDate Fecha;
    private String label;
    private boolean Estado = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idGrupoForo")
    private GrupoForo grupoForo;

    // 1 publicación tiene varios recursos
    @OneToMany(mappedBy = "publicacion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Recurso> recursos;
}
