package com.upc.campusflow.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HorarioDTO {
    private long idHorario;
    private Date dia;
    private Date horaInicio;
    private Date horaFin;
    private boolean Estado;
    private long idAsignatura;
}
