package com.upc.campusflow.DTO;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AsignaturaDTO {
    private Long idAsignatura;
    private String Nombre;
    private double Creditos;
    private int Ciclo_Academico;
    private Long id_carrera;
    private boolean Estado;
}
