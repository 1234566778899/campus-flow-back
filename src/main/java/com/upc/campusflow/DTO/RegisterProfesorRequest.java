package com.upc.campusflow.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterProfesorRequest {
    private String nombre;
    private String apellido;
    private String email;
    private String username;
    private String password;
    private String especialidad;
    private String numColegiatura;
    private Long idCarrera;
}