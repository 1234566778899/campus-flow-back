package com.upc.campusflow.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfesorDTO {

    private Long idProfesor;
    private String especialidad;
    private String numColegiatura;
    private boolean Estado;
    private Long Usuario;
    private Long idCarrera;
}
