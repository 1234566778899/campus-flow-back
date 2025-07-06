package com.upc.campusflow.DTO;

import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String password;
    private String email;
    private String nombre;
    private String apellido;
    private Long rolId;  // Ahora recibimos el ID del rol
}
