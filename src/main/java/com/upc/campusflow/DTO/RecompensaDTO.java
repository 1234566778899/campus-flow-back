package com.upc.campusflow.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecompensaDTO {
    private Long IDRecompensa;

    private String Plataforma;
    private String URL;
    private Long id_EstudianteEstadistica;
    private boolean Estado;
}
