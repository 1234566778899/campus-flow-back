package com.upc.campusflow.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime; // Necesitarás importar esto para el timestamp

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorRespuesta {
    private String mensaje;
    private String fecha; // Puedes usar String para mantenerlo simple, o LocalDateTime si prefieres el objeto
    private int estado;
}