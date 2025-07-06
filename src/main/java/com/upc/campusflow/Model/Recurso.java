package com.upc.campusflow.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor

public class Recurso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idRecurso;
    private String tipoArchivo;
    private String url;
    private Date fechaSubida;


    @ManyToOne
    @JoinColumn(name="id_tarea")
    private Tarea tarea;


    @ManyToOne
    @JoinColumn(name="id_publicacion")
    private Publicacion publicacion;

    private boolean Estado = true;
}
