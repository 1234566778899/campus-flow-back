package com.upc.campusflow.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EstudianteEstadisticaDTO {
    private Long idEstudianteEstadistica;
    private LocalDateTime TotalHoraEstudiante;
    private int TareasCompletadas;
    private LocalDate UltimaConexion;
    //private int niveldePrioridad; => no se que variable es esta, pero no croe que deberiamos introducirla
    private Long Id_Estudiante;
    private boolean Estado;

}