package com.upc.campusflow.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterEstudianteRequest {
    private String nombre;
    private String apellido;
    private String email;
    private String username;
    private String password;
    private int ciclo;
    private Long idCarrera;
}
